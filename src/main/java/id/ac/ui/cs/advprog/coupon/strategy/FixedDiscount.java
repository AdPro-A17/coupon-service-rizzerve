package id.ac.ui.cs.advprog.coupon.strategy;

import java.math.BigDecimal;

public class FixedDiscount implements DiscountStrategy {
    private final BigDecimal value;

    public FixedDiscount(BigDecimal value) {
        this.value = value;
    }

    @Override
    public BigDecimal apply(BigDecimal total) {
        return total.subtract(value).max(BigDecimal.ZERO);
    }
}
