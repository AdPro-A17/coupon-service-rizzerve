package id.ac.ui.cs.advprog.coupon.strategy;

import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import id.ac.ui.cs.advprog.coupon.model.Coupon;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DiscountStrategyTest {

    @Test
    void testResolveFixedDiscount() {
        DiscountStrategyFactory factory = new DiscountStrategyFactory();
        Coupon fixedCoupon = new Coupon("FIXED123", CouponType.FIXED, BigDecimal.valueOf(50), null, null, 10);

        DiscountStrategy strategy = factory.resolve(fixedCoupon);

        assertTrue(strategy instanceof FixedDiscount);
        assertEquals(BigDecimal.valueOf(50), ((FixedDiscount) strategy).apply(BigDecimal.valueOf(100)));
    }


    @Test
    void testResolveInvalidCouponType() {
        DiscountStrategyFactory factory = new DiscountStrategyFactory();
        Coupon invalidCoupon = new Coupon("INVALID123", null, BigDecimal.valueOf(50), null, null, 10);

        assertThrows(NullPointerException.class, () -> factory.resolve(invalidCoupon));
    }
}