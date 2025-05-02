package id.ac.ui.cs.advprog.coupon.strategy;

import id.ac.ui.cs.advprog.coupon.model.Coupon;

public class DiscountStrategyFactory {
    public DiscountStrategy resolve(Coupon coupon) {
        return switch (coupon.getType()) {
            case "FIXED" -> new FixedDiscount(coupon.getValue());
            case "PERCENTAGE" -> new PercentageDiscount(coupon.getValue());
            default -> throw new IllegalArgumentException("Unknown coupon type: " + coupon.getType());
        };
    }
}
