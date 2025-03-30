-- Drop existing tables (if they exist) to start fresh
-- Note: Order matters due to foreign key dependencies
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE Audit_Log CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Error_Log CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Notifications CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Payments CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Appointments CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Service_Package_Details CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Service_Packages CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Services CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Service_Categories CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Inventory CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Vehicles CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Users CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Roles CASCADE CONSTRAINTS';
    EXECUTE IMMEDIATE 'DROP TABLE Customers CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN -- Ignore "table does not exist" error
            RAISE;
        END IF;
END;
/

-- Drop existing sequences (if they exist)
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_customer';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_vehicle';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_category';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_service';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_package';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_appointment';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_payment';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_item';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_role';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_user';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_notification';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_audit';
    EXECUTE IMMEDIATE 'DROP SEQUENCE seq_error';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN -- Ignore "sequence does not exist" error
            RAISE;
        END IF;
END;
/

-- Create sequences for auto-generating primary keys
CREATE SEQUENCE seq_customer START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_vehicle START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_category START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_service START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_package START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_appointment START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_payment START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_item START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_role START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_user START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_notification START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_audit START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_error START WITH 1 INCREMENT BY 1;

-- Create tables with primary and foreign key constraints

-- Customers Table
CREATE TABLE Customers (
    customer_id VARCHAR2(10) PRIMARY KEY,
    first_name VARCHAR2(50) NOT NULL,
    last_name VARCHAR2(50) NOT NULL,
    phone_number VARCHAR2(15),
    email VARCHAR2(100),
    address VARCHAR2(255),
    created_date DATE DEFAULT SYSDATE
);

-- Vehicles Table
CREATE TABLE Vehicles (
    vehicle_id VARCHAR2(10) PRIMARY KEY,
    customer_id VARCHAR2(10),
    vin VARCHAR2(17) NOT NULL UNIQUE,
    make VARCHAR2(50),
    model VARCHAR2(50),
    year NUMBER(4),
    license_plate VARCHAR2(10),
    color VARCHAR2(30),
    CONSTRAINT fk_vehicle_customer FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);

-- Service_Categories Table
CREATE TABLE Service_Categories (
    category_id VARCHAR2(10) PRIMARY KEY,
    category_name VARCHAR2(50) NOT NULL,
    description VARCHAR2(255)
);

-- Services Table
CREATE TABLE Services (
    service_id VARCHAR2(10) PRIMARY KEY,
    category_id VARCHAR2(10),
    service_name VARCHAR2(100) NOT NULL,
    description VARCHAR2(255),
    base_cost NUMBER(10, 2) NOT NULL,
    estimated_time NUMBER(5), -- in minutes
    CONSTRAINT fk_service_category FOREIGN KEY (category_id) REFERENCES Service_Categories(category_id)
);

-- Service_Packages Table
CREATE TABLE Service_Packages (
    package_id VARCHAR2(10) PRIMARY KEY,
    package_name VARCHAR2(100) NOT NULL,
    description VARCHAR2(255),
    discount_price NUMBER(10, 2)
);

-- Service_Package_Details Table (Composite Key: package_id, service_id)
CREATE TABLE Service_Package_Details (
    package_id VARCHAR2(10),
    service_id VARCHAR2(10),
    CONSTRAINT pk_service_package_detail PRIMARY KEY (package_id, service_id),
    CONSTRAINT fk_package FOREIGN KEY (package_id) REFERENCES Service_Packages(package_id),
    CONSTRAINT fk_service FOREIGN KEY (service_id) REFERENCES Services(service_id)
);

-- Roles Table
CREATE TABLE Roles (
    role_id VARCHAR2(10) PRIMARY KEY,
    role_name VARCHAR2(50) NOT NULL,
    description VARCHAR2(255)
);

-- Users Table
CREATE TABLE Users (
    user_id VARCHAR2(10) PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password VARCHAR2(255) NOT NULL,
    role_id VARCHAR2(10),
    first_name VARCHAR2(50),
    last_name VARCHAR2(50),
    email VARCHAR2(100),
    created_date DATE DEFAULT SYSDATE,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES Roles(role_id)
);

-- Appointments Table
CREATE TABLE Appointments (
    appointment_id VARCHAR2(10) PRIMARY KEY,
    vehicle_id VARCHAR2(10),
    service_id VARCHAR2(10),
    package_id VARCHAR2(10),
    mechanic_id VARCHAR2(10),
    appointment_date DATE NOT NULL,
    completed_date DATE,
    status VARCHAR2(20) DEFAULT 'Scheduled',
    invoice_generated CHAR(1) DEFAULT 'N',
    notes VARCHAR2(255),
    CONSTRAINT fk_appointment_vehicle FOREIGN KEY (vehicle_id) REFERENCES Vehicles(vehicle_id),
    CONSTRAINT fk_appointment_service FOREIGN KEY (service_id) REFERENCES Services(service_id),
    CONSTRAINT fk_appointment_package FOREIGN KEY (package_id) REFERENCES Service_Packages(package_id),
    CONSTRAINT fk_appointment_mechanic FOREIGN KEY (mechanic_id) REFERENCES Users(user_id)
);

-- Payments Table
CREATE TABLE Payments (
    payment_id VARCHAR2(10) PRIMARY KEY,
    appointment_id VARCHAR2(10),
    amount NUMBER(10, 2) NOT NULL,
    payment_date DATE DEFAULT SYSDATE,
    payment_method VARCHAR2(50),
    payment_status VARCHAR2(20) DEFAULT 'Pending',
    transaction_id VARCHAR2(50),
    CONSTRAINT fk_payment_appointment FOREIGN KEY (appointment_id) REFERENCES Appointments(appointment_id)
);

-- Inventory Table
CREATE TABLE Inventory (
    item_id VARCHAR2(10) PRIMARY KEY,
    item_name VARCHAR2(100) NOT NULL,
    quantity NUMBER(5) NOT NULL,
    low_stock_threshold NUMBER(5),
    unit_price NUMBER(10, 2),
    last_updated DATE DEFAULT SYSDATE
);

-- Notifications Table
CREATE TABLE Notifications (
    notification_id VARCHAR2(10) PRIMARY KEY,
    user_id VARCHAR2(10),
    message VARCHAR2(255) NOT NULL,
    created_date DATE DEFAULT SYSDATE,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Audit_Log Table
CREATE TABLE Audit_Log (
    log_id VARCHAR2(10) PRIMARY KEY,
    table_name VARCHAR2(50) NOT NULL,
    action VARCHAR2(20) NOT NULL,
    user_id VARCHAR2(10),
    details VARCHAR2(255),
    timestamp DATE DEFAULT SYSDATE,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Error_Log Table
CREATE TABLE Error_Log (
    error_id VARCHAR2(10) PRIMARY KEY,
    procedure_name VARCHAR2(100),
    error_code VARCHAR2(50),
    error_message VARCHAR2(255),
    timestamp DATE DEFAULT SYSDATE
);