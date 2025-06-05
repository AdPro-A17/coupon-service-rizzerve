package id.ac.ui.cs.advprog.coupon.strategy;

import java.math.BigDecimal;

public class PercentageDiscount implements DiscountStrategy {
    private final BigDecimal percent;

    public PercentageDiscount(BigDecimal percent) {
        this.percent = percent;
    }

    @Override
    public BigDecimal apply(BigDecimal total) {
        return total.multiply(BigDecimal.ONE.subtract(percent.divide(BigDecimal.valueOf(100))));
    }
}