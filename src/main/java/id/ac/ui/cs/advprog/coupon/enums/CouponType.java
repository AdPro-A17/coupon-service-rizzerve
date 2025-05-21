package id.ac.ui.cs.advprog.coupon.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CouponType {
    FIXED,
    PERCENTAGE;

    @JsonCreator
    public static CouponType fromString(String key) {
        if (key == null) return null;
        try {
            return CouponType.valueOf(key.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Coupon type must be FIXED or PERCENTAGE.");
        }
    }
}
