-- Drop existing PL/SQL objects (if they exist) to start fresh
BEGIN
    -- Drop procedures
    FOR proc IN (SELECT object_name FROM user_objects WHERE object_type = 'PROCEDURE')
    LOOP
        EXECUTE IMMEDIATE 'DROP PROCEDURE ' || proc.object_name;
    END LOOP;

    -- Drop functions
    FOR func IN (SELECT object_name FROM user_objects WHERE object_type = 'FUNCTION')
    LOOP
        EXECUTE IMMEDIATE 'DROP FUNCTION ' || func.object_name;
    END LOOP;

    -- Drop triggers
    FOR trig IN (SELECT trigger_name FROM user_triggers)
    LOOP
        EXECUTE IMMEDIATE 'DROP TRIGGER ' || trig.trigger_name;
    END LOOP;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -4043 THEN -- Ignore "object does not exist" error
            RAISE;
        END IF;
END;
/

-- Add status column to Users table if it doesn't exist
BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE Users ADD (status VARCHAR2(20) DEFAULT ''Active'')';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -01430 THEN -- Column already exists
            NULL;
        ELSE
            RAISE;
        END IF;
END;
/

-- Modify Audit_Log table to ensure timestamp has a default value of SYSDATE
BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE Audit_Log MODIFY (timestamp DEFAULT SYSDATE)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -01451 THEN -- Column already has a default value
            NULL;
        ELSE
            RAISE;
        END IF;
END;
/

-- Modify Error_Log table to ensure timestamp has a default value of SYSDATE
BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE Error_Log MODIFY (timestamp DEFAULT SYSDATE)';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -01451 THEN -- Column already has a default value
            NULL;
        ELSE
            RAISE;
        END IF;
END;
/

-- Functions (created first to resolve dependencies)

-- Function 1: CheckInventoryForService
-- Purpose: Checks if there is sufficient inventory for a service
CREATE OR REPLACE FUNCTION CheckInventoryForService (
    p_service_id IN VARCHAR2,
    p_item_name IN VARCHAR2
) RETURN BOOLEAN AS
    v_quantity NUMBER;
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Check inventory quantity for the specified item
    SELECT quantity
    INTO v_quantity
    FROM Inventory
    WHERE item_name = p_item_name;

    -- Return TRUE if quantity is sufficient, FALSE otherwise
    RETURN v_quantity > 0;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN FALSE;
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'CheckInventoryForService', v_error_code, v_error_msg);
        RETURN FALSE;
END CheckInventoryForService;
/

-- Function 2: IsMechanicAvailable
-- Purpose: Checks if a mechanic is available for a given time slot
CREATE OR REPLACE FUNCTION IsMechanicAvailable (
    p_mechanic_id IN VARCHAR2,
    p_appointment_date IN DATE,
    p_estimated_duration IN NUMBER -- in minutes
) RETURN BOOLEAN AS
    v_count NUMBER;
    v_end_time DATE;
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Calculate the end time of the proposed appointment
    v_end_time := p_appointment_date + (p_estimated_duration / 1440); -- Convert minutes to days

    -- Check for overlapping appointments
    SELECT COUNT(*)
    INTO v_count
    FROM Appointments
    WHERE mechanic_id = p_mechanic_id
    AND status = 'Scheduled'
    AND appointment_date <= v_end_time
    AND (appointment_date + (SELECT estimated_time FROM Services WHERE service_id = Appointments.service_id) / 1440) >= p_appointment_date;

    -- Return TRUE if no overlapping appointments, FALSE otherwise
    RETURN v_count = 0;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN TRUE; -- No appointments found, mechanic is available
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'IsMechanicAvailable', v_error_code, v_error_msg);
        RETURN FALSE;
END IsMechanicAvailable;
/

-- Function 3: CalculateAppointmentCost
-- Purpose: Calculates the total cost of an appointment, including service and package costs
CREATE OR REPLACE FUNCTION CalculateAppointmentCost (
    p_appointment_id IN VARCHAR2
) RETURN NUMBER AS
    v_total_cost NUMBER := 0;
    v_service_cost NUMBER;
    v_package_discount NUMBER;
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Get the base cost of the service
    SELECT s.base_cost
    INTO v_service_cost
    FROM Appointments a
    JOIN Services s ON a.service_id = s.service_id
    WHERE a.appointment_id = p_appointment_id;

    -- Get the package discount (if any)
    SELECT COALESCE(sp.discount_price, 0)
    INTO v_package_discount
    FROM Appointments a
    LEFT JOIN Service_Packages sp ON a.package_id = sp.package_id
    WHERE a.appointment_id = p_appointment_id;

    -- Calculate total cost
    v_total_cost := v_service_cost - v_package_discount;

    RETURN v_total_cost;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'CalculateAppointmentCost', v_error_code, v_error_msg);
        RETURN 0;
END CalculateAppointmentCost;
/

-- Function 4: GetCustomerAppointmentCount
-- Purpose: Returns the number of appointments for a customer within a given time period
CREATE OR REPLACE FUNCTION GetCustomerAppointmentCount (
    p_customer_id IN VARCHAR2,
    p_start_date IN DATE,
    p_end_date IN DATE
) RETURN NUMBER AS
    v_count NUMBER;
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Count appointments for the customer within the date range
    SELECT COUNT(*)
    INTO v_count
    FROM Appointments a
    JOIN Vehicles v ON a.vehicle_id = v.vehicle_id
    WHERE v.customer_id = p_customer_id
    AND a.appointment_date BETWEEN p_start_date AND p_end_date;

    RETURN v_count;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'GetCustomerAppointmentCount', v_error_code, v_error_msg);
        RETURN 0;
END GetCustomerAppointmentCount;
/

-- Function 5: GetServicePrice
-- Purpose: Retrieves the price of a service
CREATE OR REPLACE FUNCTION GetServicePrice (
    p_service_id IN VARCHAR2
) RETURN NUMBER AS
    v_price NUMBER;
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    SELECT base_cost
    INTO v_price
    FROM Services
    WHERE service_id = p_service_id;

    RETURN v_price;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'GetServicePrice', v_error_code, v_error_msg);
        RETURN 0;
END GetServicePrice;
/

-- Stored Procedures

-- Procedure 1: INSERT_APPOINTMENT
-- Purpose: Inserts a new appointment, checking inventory and mechanic availability
CREATE OR REPLACE PROCEDURE INSERT_APPOINTMENT (
    p_vehicle_id IN VARCHAR2,
    p_service_id IN VARCHAR2,
    p_package_id IN VARCHAR2,
    p_mechanic_id IN VARCHAR2,
    p_appointment_date IN DATE,
    p_notes IN VARCHAR2,
    p_appointment_id OUT VARCHAR2
) AS
    v_estimated_duration NUMBER;
    v_item_name VARCHAR2(100) := 'Oil Filter'; -- Example item required for the service
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Get the estimated duration of the service
    SELECT estimated_time
    INTO v_estimated_duration
    FROM Services
    WHERE service_id = p_service_id;

    -- Check inventory availability
    IF NOT CheckInventoryForService(p_service_id, v_item_name) THEN
        RAISE_APPLICATION_ERROR(-20001, 'Insufficient inventory for service: ' || p_service_id);
    END IF;

    -- Check mechanic availability
    IF NOT IsMechanicAvailable(p_mechanic_id, p_appointment_date, v_estimated_duration) THEN
        RAISE_APPLICATION_ERROR(-20002, 'Mechanic is not available at the specified time.');
    END IF;

    -- Generate a new appointment ID
    p_appointment_id := 'APPT' || TO_CHAR(seq_appointment.NEXTVAL, 'FM00000');

    -- Insert the new appointment
    INSERT INTO Appointments (
        appointment_id, vehicle_id, service_id, package_id, mechanic_id,
        appointment_date, status, notes
    ) VALUES (
        p_appointment_id, p_vehicle_id, p_service_id, p_package_id, p_mechanic_id,
        p_appointment_date, 'Scheduled', p_notes
    );

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Appointments',
        'INSERT',
        p_mechanic_id,
        'Scheduled appointment ' || p_appointment_id
    );

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20003, 'Invalid service or mechanic ID.');
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INSERT_APPOINTMENT', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END INSERT_APPOINTMENT;
/

-- Procedure 2: DELETE_APPOINTMENT_SERVICE
-- Purpose: Deletes an appointment and its associated services
CREATE OR REPLACE PROCEDURE DELETE_APPOINTMENT_SERVICE (
    p_appointment_id IN VARCHAR2
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Delete related service package details (if any)
    DELETE FROM Service_Package_Details
    WHERE package_id IN (
        SELECT package_id
        FROM Appointments
        WHERE appointment_id = p_appointment_id
    );

    -- Delete the appointment
    DELETE FROM Appointments
    WHERE appointment_id = p_appointment_id;

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Appointments',
        'DELETE',
        NULL,
        'Deleted appointment ' || p_appointment_id
    );

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20004, 'Appointment not found: ' || p_appointment_id);
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'DELETE_APPOINTMENT_SERVICE', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END DELETE_APPOINTMENT_SERVICE;
/

-- Procedure 3: UPDATE_APPOINTMENT_SERVICE
-- Purpose: Updates an appointment’s service details
CREATE OR REPLACE PROCEDURE UPDATE_APPOINTMENT_SERVICE (
    p_appointment_id IN VARCHAR2,
    p_service_id IN VARCHAR2,
    p_package_id IN VARCHAR2,
    p_mechanic_id IN VARCHAR2,
    p_appointment_date IN DATE
) AS
    v_estimated_duration NUMBER;
    v_item_name VARCHAR2(100) := 'Oil Filter'; -- Example item
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Get the estimated duration of the new service
    SELECT estimated_time
    INTO v_estimated_duration
    FROM Services
    WHERE service_id = p_service_id;

    -- Check inventory availability for the new service
    IF NOT CheckInventoryForService(p_service_id, v_item_name) THEN
        RAISE_APPLICATION_ERROR(-20005, 'Insufficient inventory for service: ' || p_service_id);
    END IF;

    -- Check mechanic availability for the new date
    IF NOT IsMechanicAvailable(p_mechanic_id, p_appointment_date, v_estimated_duration) THEN
        RAISE_APPLICATION_ERROR(-20006, 'Mechanic is not available at the specified time.');
    END IF;

    -- Update the appointment
    UPDATE Appointments
    SET service_id = p_service_id,
        package_id = p_package_id,
        mechanic_id = p_mechanic_id,
        appointment_date = p_appointment_date
    WHERE appointment_id = p_appointment_id;

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Appointments',
        'UPDATE',
        p_mechanic_id,
        'Updated appointment ' || p_appointment_id || ' with service ' || p_service_id
    );

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20007, 'Appointment or service not found.');
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'UPDATE_APPOINTMENT_SERVICE', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END UPDATE_APPOINTMENT_SERVICE;
/

-- Procedure 4: INSERT_CUSTOMER
-- Purpose: Inserts a new customer into the Customers table
CREATE OR REPLACE PROCEDURE INSERT_CUSTOMER (
    p_first_name IN VARCHAR2,
    p_last_name IN VARCHAR2,
    p_phone_number IN VARCHAR2,
    p_email IN VARCHAR2,
    p_address IN VARCHAR2,
    p_customer_id OUT VARCHAR2
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Generate a new customer ID
    p_customer_id := 'CUST' || TO_CHAR(seq_customer.NEXTVAL, 'FM00000');

    -- Insert the new customer
    INSERT INTO Customers (
        customer_id, first_name, last_name, phone_number, email, address, created_date
    ) VALUES (
        p_customer_id, p_first_name, p_last_name, p_phone_number, p_email, p_address, SYSDATE
    );

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Customers',
        'INSERT',
        NULL,
        'Inserted customer ' || p_customer_id
    );

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INSERT_CUSTOMER', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END INSERT_CUSTOMER;
/

-- Procedure 5: DELETE_CUSTOMER
-- Purpose: Deletes a customer and their associated vehicles and appointments
CREATE OR REPLACE PROCEDURE DELETE_CUSTOMER (
    p_customer_id IN VARCHAR2
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Delete related appointments
    FOR appointment IN (SELECT appointment_id FROM Appointments WHERE vehicle_id IN (SELECT vehicle_id FROM Vehicles WHERE customer_id = p_customer_id))
    LOOP
        DELETE_APPOINTMENT_SERVICE(appointment.appointment_id);
    END LOOP;

    -- Delete related vehicles
    DELETE FROM Vehicles
    WHERE customer_id = p_customer_id;

    -- Delete the customer
    DELETE FROM Customers
    WHERE customer_id = p_customer_id;

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Customers',
        'DELETE',
        NULL,
        'Deleted customer ' || p_customer_id
    );

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20008, 'Customer not found: ' || p_customer_id);
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'DELETE_CUSTOMER', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END DELETE_CUSTOMER;
/

-- Procedure 6: INSERT_VEHICLE
-- Purpose: Inserts a new vehicle into the Vehicles table
CREATE OR REPLACE PROCEDURE INSERT_VEHICLE (
    p_customer_id IN VARCHAR2,
    p_vin IN VARCHAR2,
    p_make IN VARCHAR2,
    p_model IN VARCHAR2,
    p_year IN NUMBER,
    p_license_plate IN VARCHAR2,
    p_color IN VARCHAR2,
    p_vehicle_id OUT VARCHAR2
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Generate a new vehicle ID
    p_vehicle_id := 'VEH' || TO_CHAR(seq_vehicle.NEXTVAL, 'FM00000');

    -- Insert the new vehicle
    INSERT INTO Vehicles (
        vehicle_id, customer_id, vin, make, model, year, license_plate, color
    ) VALUES (
        p_vehicle_id, p_customer_id, p_vin, p_make, p_model, p_year, p_license_plate, p_color
    );

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Vehicles',
        'INSERT',
        NULL,
        'Inserted vehicle ' || p_vehicle_id || ' for customer ' || p_customer_id
    );

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INSERT_VEHICLE', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END INSERT_VEHICLE;
/

-- Procedure 7: DELETE_VEHICLE
-- Purpose: Deletes a vehicle and its associated appointments
CREATE OR REPLACE PROCEDURE DELETE_VEHICLE (
    p_vehicle_id IN VARCHAR2
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Delete related appointments
    FOR appointment IN (SELECT appointment_id FROM Appointments WHERE vehicle_id = p_vehicle_id)
    LOOP
        DELETE_APPOINTMENT_SERVICE(appointment.appointment_id);
    END LOOP;

    -- Delete the vehicle
    DELETE FROM Vehicles
    WHERE vehicle_id = p_vehicle_id;

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Vehicles',
        'DELETE',
        NULL,
        'Deleted vehicle ' || p_vehicle_id
    );

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20009, 'Vehicle not found: ' || p_vehicle_id);
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'DELETE_VEHICLE', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END DELETE_VEHICLE;
/

-- Procedure 8: INSERT_INVOICE
-- Purpose: Inserts a new invoice (payment record) for an appointment
CREATE OR REPLACE PROCEDURE INSERT_INVOICE (
    p_appointment_id IN VARCHAR2,
    p_payment_method IN VARCHAR2,
    p_payment_id OUT VARCHAR2
) AS
    v_amount NUMBER;
    v_appointment_status VARCHAR2(20);
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Check if the appointment exists and is completed
    SELECT status
    INTO v_appointment_status
    FROM Appointments
    WHERE appointment_id = p_appointment_id;

    IF v_appointment_status != 'Completed' THEN
        RAISE_APPLICATION_ERROR(-20010, 'Appointment must be completed before processing payment.');
    END IF;

    -- Calculate the total cost using the existing function
    v_amount := CalculateAppointmentCost(p_appointment_id);

    -- Generate a new payment ID
    p_payment_id := 'PAY' || TO_CHAR(seq_payment.NEXTVAL, 'FM00000');

    -- Insert the payment (invoice)
    INSERT INTO Payments (
        payment_id, appointment_id, amount, payment_date, payment_method, payment_status, transaction_id
    ) VALUES (
        p_payment_id, p_appointment_id, v_amount, SYSDATE, p_payment_method, 'Paid', 'TXN' || p_payment_id
    );

    -- Update the appointment to mark invoice as generated
    UPDATE Appointments
    SET invoice_generated = 'Y'
    WHERE appointment_id = p_appointment_id;

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Payments',
        'INSERT',
        NULL,
        'Inserted invoice ' || p_payment_id || ' for appointment ' || p_appointment_id
    );

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20011, 'Appointment not found: ' || p_appointment_id);
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INSERT_INVOICE', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END INSERT_INVOICE;
/

-- Procedure 9: DELETE_INVOICE
-- Purpose: Deletes an invoice (payment record) associated with an appointment
CREATE OR REPLACE PROCEDURE DELETE_INVOICE (
    p_payment_id IN VARCHAR2
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Delete the payment (invoice)
    DELETE FROM Payments
    WHERE payment_id = p_payment_id;

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Payments',
        'DELETE',
        NULL,
        'Deleted invoice ' || p_payment_id
    );

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20012, 'Invoice not found: ' || p_payment_id);
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'DELETE_INVOICE', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END DELETE_INVOICE;
/

-- Procedure 10: INSERT_INVENTORY
-- Purpose: Inserts a new inventory item into the Inventory table
CREATE OR REPLACE PROCEDURE INSERT_INVENTORY (
    p_item_name IN VARCHAR2,
    p_quantity IN NUMBER,
    p_low_stock_threshold IN NUMBER,
    p_unit_price IN NUMBER,
    p_item_id OUT VARCHAR2
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Generate a new item ID
    p_item_id := 'ITEM' || TO_CHAR(seq_item.NEXTVAL, 'FM00000');

    -- Insert the new inventory item
    INSERT INTO Inventory (
        item_id, item_name, quantity, low_stock_threshold, unit_price, last_updated
    ) VALUES (
        p_item_id, p_item_name, p_quantity, p_low_stock_threshold, p_unit_price, SYSDATE
    );

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Inventory',
        'INSERT',
        NULL,
        'Inserted inventory item ' || p_item_id
    );

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INSERT_INVENTORY', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END INSERT_INVENTORY;
/

-- Procedure 11: DELETE_INVENTORY
-- Purpose: Deletes an inventory item
CREATE OR REPLACE PROCEDURE DELETE_INVENTORY (
    p_item_id IN VARCHAR2
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Delete the inventory item
    DELETE FROM Inventory
    WHERE item_id = p_item_id;

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Inventory',
        'DELETE',
        NULL,
        'Deleted inventory item ' || p_item_id
    );

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20013, 'Inventory item not found: ' || p_item_id);
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'DELETE_INVENTORY', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END DELETE_INVENTORY;
/

-- Procedure 12: UPDATE_INVENTORY
-- Purpose: Updates the quantity of an inventory item
CREATE OR REPLACE PROCEDURE UPDATE_INVENTORY (
    p_item_id IN VARCHAR2,
    p_quantity IN NUMBER
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Update the inventory quantity
    UPDATE Inventory
    SET quantity = p_quantity,
        last_updated = SYSDATE
    WHERE item_id = p_item_id;

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Inventory',
        'UPDATE',
        NULL,
        'Updated inventory item ' || p_item_id || ' to quantity ' || p_quantity
    );

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20014, 'Inventory item not found: ' || p_item_id);
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'UPDATE_INVENTORY', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END UPDATE_INVENTORY;
/

-- Procedure 13: INSERT_USER
-- Purpose: Inserts a new user into the Users table
CREATE OR REPLACE PROCEDURE INSERT_USER (
    p_username IN VARCHAR2,
    p_password IN VARCHAR2,
    p_role_id IN VARCHAR2,
    p_first_name IN VARCHAR2,
    p_last_name IN VARCHAR2,
    p_email IN VARCHAR2,
    p_user_id OUT VARCHAR2
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Generate a new user ID
    p_user_id := 'USER' || TO_CHAR(seq_user.NEXTVAL, 'FM00000');

    -- Insert the new user
    INSERT INTO Users (
        user_id, username, password, role_id, first_name, last_name, email, created_date
    ) VALUES (
        p_user_id, p_username, p_password, p_role_id, p_first_name, p_last_name, p_email, SYSDATE
    );

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Users',
        'INSERT',
        p_user_id,
        'Inserted user ' || p_username
    );

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INSERT_USER', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END INSERT_USER;
/

-- Procedure 14: MAKE_THE_USER_INACTIVE
-- Purpose: Marks a user as inactive by updating their status
CREATE OR REPLACE PROCEDURE MAKE_THE_USER_INACTIVE (
    p_user_id IN VARCHAR2
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Update the user to mark as inactive
    UPDATE Users
    SET status = 'Inactive'
    WHERE user_id = p_user_id;

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Users',
        'UPDATE',
        p_user_id,
        'Marked user ' || p_user_id || ' as inactive'
    );

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20015, 'User not found: ' || p_user_id);
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'MAKE_THE_USER_INACTIVE', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END MAKE_THE_USER_INACTIVE;
/

-- Procedure 15: GET_EMPLOYEE_DETAILS_BY_JOB_FOR_LOOP
-- Purpose: Retrieves employee (user) details for a specific job role using a FOR loop
CREATE OR REPLACE PROCEDURE GET_EMPLOYEE_DETAILS_BY_JOB_FOR_LOOP (
    p_role_name IN VARCHAR2,
    p_employee_details OUT SYS_REFCURSOR
) AS
    v_role_id VARCHAR2(10);
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Get the role ID for the specified role name
    SELECT role_id
    INTO v_role_id
    FROM Roles
    WHERE role_name = p_role_name;

    -- Open cursor for employee details
    OPEN p_employee_details FOR
    SELECT user_id, username, first_name, last_name, email
    FROM Users
    WHERE role_id = v_role_id;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        OPEN p_employee_details FOR
        SELECT 'No employees found for role ' || p_role_name AS message FROM dual;
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'GET_EMPLOYEE_DETAILS_BY_JOB_FOR_LOOP', v_error_code, v_error_msg);
        RAISE;
END GET_EMPLOYEE_DETAILS_BY_JOB_FOR_LOOP;
/

-- Procedure 16: GET_EMPLOYEE_DETAILS_EXPLICIT_CURSOR
-- Purpose: Retrieves employee details using an explicit cursor
CREATE OR REPLACE PROCEDURE GET_EMPLOYEE_DETAILS_EXPLICIT_CURSOR (
    p_role_name IN VARCHAR2,
    p_employee_details OUT SYS_REFCURSOR
) AS
    v_role_id VARCHAR2(10);
    CURSOR employee_cursor IS
        SELECT user_id, username, first_name, last_name, email
        FROM Users
        WHERE role_id = v_role_id;
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Get the role ID for the specified role name
    SELECT role_id
    INTO v_role_id
    FROM Roles
    WHERE role_name = p_role_name;

    -- Open cursor for employee details
    OPEN p_employee_details FOR
    SELECT user_id, username, first_name, last_name, email
    FROM Users
    WHERE role_id = v_role_id;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        OPEN p_employee_details FOR
        SELECT 'No employees found for role ' || p_role_name AS message FROM dual;
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'GET_EMPLOYEE_DETAILS_EXPLICIT_CURSOR', v_error_code, v_error_msg);
        RAISE;
END GET_EMPLOYEE_DETAILS_EXPLICIT_CURSOR;
/

-- Procedure 17: PP_PROCESSSTUDENTS_RD
-- Purpose: Processes students (users) and returns a ref cursor with details
CREATE OR REPLACE PROCEDURE PP_PROCESSSTUDENTS_RD (
    p_user_details OUT SYS_REFCURSOR
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Open cursor for user details
    OPEN p_user_details FOR
    SELECT u.user_id, u.username, u.first_name, u.last_name, u.email, r.role_name
    FROM Users u
    JOIN Roles r ON u.role_id = r.role_id;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        OPEN p_user_details FOR
        SELECT 'No users found' AS message FROM dual;
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'PP_PROCESSSTUDENTS_RD', v_error_code, v_error_msg);
        RAISE;
END PP_PROCESSSTUDENTS_RD;
/

-- Procedure 18: RD_SPLIT_SECTION
-- Purpose: Splits a section (e.g., appointments) into parts for pagination or reporting
CREATE OR REPLACE PROCEDURE RD_SPLIT_SECTION (
    p_start_date IN DATE,
    p_end_date IN DATE,
    p_page_number IN NUMBER,
    p_page_size IN NUMBER,
    p_appointment_details OUT SYS_REFCURSOR
) AS
    v_start_row NUMBER;
    v_end_row NUMBER;
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Calculate the start and end rows for pagination
    v_start_row := (p_page_number - 1) * p_page_size + 1;
    v_end_row := p_page_number * p_page_size;

    -- Open cursor for paginated appointment details
    OPEN p_appointment_details FOR
    SELECT *
    FROM (
        SELECT a.appointment_id, a.vehicle_id, a.service_id, a.appointment_date, a.status,
               ROW_NUMBER() OVER (ORDER BY a.appointment_date) AS rn
        FROM Appointments a
        WHERE a.appointment_date BETWEEN p_start_date AND p_end_date
    )
    WHERE rn BETWEEN v_start_row AND v_end_row;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        OPEN p_appointment_details FOR
        SELECT 'No appointments found' AS message FROM dual;
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'RD_SPLIT_SECTION', v_error_code, v_error_msg);
        RAISE;
END RD_SPLIT_SECTION;
/

-- Procedure 19: INSERT_SERVICE
-- Purpose: Inserts a new service into the Services table
CREATE OR REPLACE PROCEDURE INSERT_SERVICE (
    p_category_id IN VARCHAR2,
    p_service_name IN VARCHAR2,
    p_description IN VARCHAR2,
    p_base_cost IN NUMBER,
    p_estimated_time IN NUMBER,
    p_service_id OUT VARCHAR2
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Generate a new service ID
    p_service_id := 'SERV' || TO_CHAR(seq_service.NEXTVAL, 'FM00000');

    -- Insert the new service
    INSERT INTO Services (
        service_id, category_id, service_name, description, base_cost, estimated_time
    ) VALUES (
        p_service_id, p_category_id, p_service_name, p_description, p_base_cost, p_estimated_time
    );

    -- Log the action in Audit_Log (omit timestamp, let default SYSDATE apply)
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Services',
        'INSERT',
        NULL,
        'Inserted service ' || p_service_id
    );

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INSERT_SERVICE', v_error_code, v_error_msg);
        ROLLBACK;
        RAISE;
END INSERT_SERVICE;
/

-- Procedure 20: GET_VEHICLE_YOURNAME
-- Purpose: Retrieves vehicle details for a customer
CREATE OR REPLACE PROCEDURE GET_VEHICLE_YOURNAME (
    p_customer_id IN VARCHAR2,
    p_vehicle_details OUT SYS_REFCURSOR
) AS
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Open cursor for vehicle details
    OPEN p_vehicle_details FOR
    SELECT vehicle_id, vin, make, model, year, license_plate, color
    FROM Vehicles
    WHERE customer_id = p_customer_id;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        OPEN p_vehicle_details FOR
        SELECT 'No vehicles found' AS message FROM dual;
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'GET_VEHICLE_YOURNAME', v_error_code, v_error_msg);
        RAISE;
END GET_VEHICLE_YOURNAME;
/

-- Triggers

-- Trigger 1: APPOINTMENTS_INSERT_TRIGGER
-- Purpose: Logs the insertion of a new appointment
CREATE OR REPLACE TRIGGER APPOINTMENTS_INSERT_TRIGGER
AFTER INSERT ON Appointments
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Appointments',
        'INSERT',
        :NEW.mechanic_id,
        'Inserted appointment ' || :NEW.appointment_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'APPOINTMENTS_INSERT_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 2: APPOINTMENTS_UPDATE_TRIGGER
-- Purpose: Logs updates to an appointment
CREATE OR REPLACE TRIGGER APPOINTMENTS_UPDATE_TRIGGER
AFTER UPDATE ON Appointments
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Appointments',
        'UPDATE',
        :NEW.mechanic_id,
        'Updated appointment ' || :NEW.appointment_id || ' status from ' || :OLD.status || ' to ' || :NEW.status
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'APPOINTMENTS_UPDATE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 3: APPOINTMENTS_DELETE_TRIGGER
-- Purpose: Logs the deletion of an appointment
CREATE OR REPLACE TRIGGER APPOINTMENTS_DELETE_TRIGGER
AFTER DELETE ON Appointments
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Appointments',
        'DELETE',
        NULL,
        'Deleted appointment ' || :OLD.appointment_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'APPOINTMENTS_DELETE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 4: APPOINTMENTSERVICES_INSERT_TRIGGER
-- Purpose: Logs the insertion of a service into a package
CREATE OR REPLACE TRIGGER APPOINTMENTSERVICES_INSERT_TRIGGER
AFTER INSERT ON Service_Package_Details
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Service_Package_Details',
        'INSERT',
        NULL,
        'Inserted service ' || :NEW.service_id || ' into package ' || :NEW.package_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'APPOINTMENTSERVICES_INSERT_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 5: CUSTOMERS_INSERT_TRIGGER
-- Purpose: Logs the insertion of a new customer
CREATE OR REPLACE TRIGGER CUSTOMERS_INSERT_TRIGGER
AFTER INSERT ON Customers
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Customers',
        'INSERT',
        NULL,
        'Inserted customer ' || :NEW.customer_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'CUSTOMERS_INSERT_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 6: CUSTOMERS_UPDATE_TRIGGER
-- Purpose: Logs updates to a customer
CREATE OR REPLACE TRIGGER CUSTOMERS_UPDATE_TRIGGER
AFTER UPDATE ON Customers
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Customers',
        'UPDATE',
        NULL,
        'Updated customer ' || :NEW.customer_id || ' email from ' || :OLD.email || ' to ' || :NEW.email
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'CUSTOMERS_UPDATE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 7: CUSTOMERS_DELETE_TRIGGER
-- Purpose: Logs the deletion of a customer
CREATE OR REPLACE TRIGGER CUSTOMERS_DELETE_TRIGGER
AFTER DELETE ON Customers
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Customers',
        'DELETE',
        NULL,
        'Deleted customer ' || :OLD.customer_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'CUSTOMERS_DELETE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 8: VEHICLES_INSERT_TRIGGER
-- Purpose: Logs the insertion of a new vehicle
CREATE OR REPLACE TRIGGER VEHICLES_INSERT_TRIGGER
AFTER INSERT ON Vehicles
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Vehicles',
        'INSERT',
        NULL,
        'Inserted vehicle ' || :NEW.vehicle_id || ' for customer ' || :NEW.customer_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'VEHICLES_INSERT_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 9: VEHICLES_DELETE_TRIGGER
-- Purpose: Logs the deletion of a vehicle
CREATE OR REPLACE TRIGGER VEHICLES_DELETE_TRIGGER
AFTER DELETE ON Vehicles
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2    (4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Vehicles',
        'DELETE',
        NULL,
        'Deleted vehicle ' || :OLD.vehicle_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'VEHICLES_DELETE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 10: INVOICES_INSERT_TRIGGER
-- Purpose: Logs the insertion of a new invoice (payment)
CREATE OR REPLACE TRIGGER INVOICES_INSERT_TRIGGER
AFTER INSERT ON Payments
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Payments',
        'INSERT',
        NULL,
        'Inserted invoice ' || :NEW.payment_id || ' for appointment ' || :NEW.appointment_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INVOICES_INSERT_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 11: INVOICES_UPDATE_TRIGGER
-- Purpose: Logs updates to an invoice (payment)
CREATE OR REPLACE TRIGGER INVOICES_UPDATE_TRIGGER
AFTER UPDATE ON Payments
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Payments',
        'UPDATE',
        NULL,
        'Updated invoice ' || :NEW.payment_id || ' amount from ' || :OLD.amount || ' to ' || :NEW.amount
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INVOICES_UPDATE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 12: INVOICES_DELETE_TRIGGER
-- Purpose: Logs the deletion of an invoice (payment)
CREATE OR REPLACE TRIGGER INVOICES_DELETE_TRIGGER
AFTER DELETE ON Payments
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Payments',
        'DELETE',
        NULL,
        'Deleted invoice ' || :OLD.payment_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INVOICES_DELETE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 13: INVENTORY_INSERT_TRIGGER
-- Purpose: Logs the insertion of a new inventory item
CREATE OR REPLACE TRIGGER INVENTORY_INSERT_TRIGGER
AFTER INSERT ON Inventory
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Inventory',
        'INSERT',
        NULL,
        'Inserted inventory item ' || :NEW.item_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INVENTORY_INSERT_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 14: INVENTORY_UPDATE_TRIGGER
-- Purpose: Logs updates to an inventory item
CREATE OR REPLACE TRIGGER INVENTORY_UPDATE_TRIGGER
AFTER UPDATE ON Inventory
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Inventory',
        'UPDATE',
        NULL,
        'Updated inventory item ' || :NEW.item_id || ' quantity from ' || :OLD.quantity || ' to ' || :NEW.quantity
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INVENTORY_UPDATE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 15: INVENTORY_DELETE_TRIGGER
-- Purpose: Logs the deletion of an inventory item
CREATE OR REPLACE TRIGGER INVENTORY_DELETE_TRIGGER
AFTER DELETE ON Inventory
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Inventory',
        'DELETE',
        NULL,
        'Deleted inventory item ' || :OLD.item_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'INVENTORY_DELETE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 16: HANDLE_STOCK_NOTIFICATIONS
-- Purpose: Creates a notification for the admin when inventory falls below the low stock threshold
CREATE OR REPLACE TRIGGER HANDLE_STOCK_NOTIFICATIONS
AFTER UPDATE OF quantity ON Inventory
FOR EACH ROW
WHEN (NEW.quantity < NEW.low_stock_threshold)
DECLARE
    v_admin_id VARCHAR2(10);
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    -- Find an admin user to notify
    SELECT user_id
    INTO v_admin_id
    FROM Users
    WHERE role_id = (SELECT role_id FROM Roles WHERE role_name = 'Admin')
    AND ROWNUM = 1;

    -- Insert a notification for the admin (omit created_date, let default SYSDATE apply)
    INSERT INTO Notifications (notification_id, user_id, message)
    VALUES (
        'NOTIF' || TO_CHAR(seq_notification.NEXTVAL, 'FM00000'),
        v_admin_id,
        'Low inventory alert: ' || :NEW.item_name || ' has ' || :NEW.quantity || ' units remaining.'
    );
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := 'No admin user found to notify';
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'HANDLE_STOCK_NOTIFICATIONS', v_error_code, v_error_msg);
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'HANDLE_STOCK_NOTIFICATIONS', v_error_code, v_error_msg);
END;
/

-- Trigger 17: SERVICES_INSERT_TRIGGER
-- Purpose: Logs the insertion of a new service
CREATE OR REPLACE TRIGGER SERVICES_INSERT_TRIGGER
AFTER INSERT ON Services
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Services',
        'INSERT',
        NULL,
        'Inserted service ' || :NEW.service_id
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'SERVICES_INSERT_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 18: SERVICES_UPDATE_TRIGGER
-- Purpose: Logs updates to a service
CREATE OR REPLACE TRIGGER SERVICES_UPDATE_TRIGGER
AFTER UPDATE ON Services
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Services',
        'UPDATE',
        NULL,
        'Updated service ' || :NEW.service_id || ' base cost from ' || :OLD.base_cost || ' to ' || :NEW.base_cost
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'SERVICES_UPDATE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 19: USERS_INACTIVE_TRIGGER
-- Purpose: Logs when a user is marked as inactive
CREATE OR REPLACE TRIGGER USERS_INACTIVE_TRIGGER
AFTER UPDATE OF status ON Users
FOR EACH ROW
WHEN (NEW.status = 'Inactive')
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Users',
        'UPDATE',
        :NEW.user_id,
        'Marked user ' || :NEW.username || ' as inactive'
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'USERS_INACTIVE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Trigger 20: USERS_UPDATE_TRIGGER
-- Purpose: Logs updates to a user
CREATE OR REPLACE TRIGGER USERS_UPDATE_TRIGGER
AFTER UPDATE ON Users
FOR EACH ROW
DECLARE
    v_error_code NUMBER;
    v_error_msg VARCHAR2(4000);
BEGIN
    INSERT INTO Audit_Log (log_id, table_name, action, user_id, details)
    VALUES (
        'LOG' || TO_CHAR(seq_audit.NEXTVAL, 'FM00000'),
        'Users',
        'UPDATE',
        :NEW.user_id,
        'Updated user ' || :NEW.username || ' email from ' || :OLD.email || ' to ' || :NEW.email
    );
EXCEPTION
    WHEN OTHERS THEN
        -- Assign SQLCODE and SQLERRM to local variables
        v_error_code := SQLCODE;
        v_error_msg := SQLERRM;
        INSERT INTO Error_Log (error_id, procedure_name, error_code, error_message)
        VALUES ('ERR' || TO_CHAR(seq_error.NEXTVAL, 'FM00000'), 'USERS_UPDATE_TRIGGER', v_error_code, v_error_msg);
END;
/

-- Commit the changes
COMMIT;

-- Verify the PL/SQL objects
SELECT object_name, object_type, status
FROM user_objects
WHERE object_type IN ('PROCEDURE', 'FUNCTION', 'TRIGGER')
ORDER BY object_type, object_name;

-- Recompile any invalid objects
BEGIN
    FOR obj IN (SELECT object_name, object_type
                FROM user_objects
                WHERE status = 'INVALID'
                AND object_type IN ('PROCEDURE', 'FUNCTION', 'TRIGGER'))
    LOOP
        BEGIN
            IF obj.object_type = 'PROCEDURE' THEN
                EXECUTE IMMEDIATE 'ALTER PROCEDURE ' || obj.object_name || ' COMPILE';
            ELSIF obj.object_type = 'FUNCTION' THEN
                EXECUTE IMMEDIATE 'ALTER FUNCTION ' || obj.object_name || ' COMPILE';
            ELSIF obj.object_type = 'TRIGGER' THEN
                EXECUTE IMMEDIATE 'ALTER TRIGGER ' || obj.object_name || ' COMPILE';
            END IF;
            DBMS_OUTPUT.PUT_LINE('Recompiled ' || obj.object_type || ' ' || obj.object_name);
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('Failed to recompile ' || obj.object_type || ' ' || obj.object_name || ': ' || SQLERRM);
        END;
    END LOOP;
END;
/

-- Check for remaining invalid objects
SELECT object_name, object_type, status
FROM user_objects
WHERE status = 'INVALID'
AND object_type IN ('PROCEDURE', 'FUNCTION', 'TRIGGER');