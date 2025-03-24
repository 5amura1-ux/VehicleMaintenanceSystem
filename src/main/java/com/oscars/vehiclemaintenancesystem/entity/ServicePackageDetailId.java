package com.oscars.vehiclemaintenancesystem.entity;

import java.io.Serializable;

public class ServicePackageDetailId implements Serializable {
    private String packageId;
    private String serviceId;

    public ServicePackageDetailId() {}

    public ServicePackageDetailId(String packageId, String serviceId) {
        this.packageId = packageId;
        this.serviceId = serviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServicePackageDetailId)) return false;
        ServicePackageDetailId that = (ServicePackageDetailId) o;
        return packageId.equals(that.packageId) && serviceId.equals(that.serviceId);
    }

    @Override
    public int hashCode() {
        return 31 * packageId.hashCode() + serviceId.hashCode();
    }
}