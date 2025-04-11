package id.ac.ui.cs.advprog.coupon.strategy;

import java.math.BigDecimal;

public class PercentageDiscount implements DiscountStrategy {
    private final BigDecimal percentage;

    public PercentageDiscount(BigDecimal percentage) {
        this.percentage = percentage;
    }

    @Override
    public BigDecimal apply(BigDecimal total) {
        return total.subtract(total.multiply(percentage));
    }
}