package id.ac.ui.cs.advprog.coupon.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CouponTest {


    @Test
    void testCouponNotUsableWhenBelowMinimumPurchase() {
        Coupon coupon = new Coupon("MIN50K", "FIXED", new BigDecimal("10000"),
                new BigDecimal("50000"), LocalDateTime.now().plusDays(1), false);
        BigDecimal total = new BigDecimal("40000");

        assertFalse(coupon.isUsable(total));
    }

    @Test
    void testCouponUsableWhenTotalMeetsMinimumPurchase() {
        Coupon coupon = new Coupon("OK", "FIXED", new BigDecimal("10000"),
                new BigDecimal("30000"), LocalDateTime.now().plusDays(1), false);
        BigDecimal total = new BigDecimal("30000");

        assertTrue(coupon.isUsable(total));
    }

    @Test
    void testCouponNotExpired() {
        Coupon coupon = new Coupon("TEST", "PERCENTAGE", new BigDecimal("0.1"),
                new BigDecimal("0.0"), LocalDateTime.now().plusDays(1), false);
        assertFalse(coupon.isExpired());
        assertTrue(coupon.isUsable(new BigDecimal("100"))); // total belanja besar
    }
}
