package id.ac.ui.cs.advprog.coupon.strategy;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal apply(BigDecimal total);
}
