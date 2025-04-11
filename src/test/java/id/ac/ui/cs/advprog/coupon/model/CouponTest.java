package id.ac.ui.cs.advprog.coupon.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CouponTest {
    @Test
    void testCouponNotExpired() {
        Coupon coupon = new Coupon("TEST", "PERCENTAGE", new BigDecimal("0.1"), LocalDateTime.now().plusDays(1), false);
        assertFalse(coupon.isExpired());
        assertTrue(coupon.isUsable());
    }

    @Test
    void testCouponExpired() {
        Coupon coupon = new Coupon("OLD", "PERCENTAGE", new BigDecimal("0.1"), LocalDateTime.now().minusDays(1), false);
        assertTrue(coupon.isExpired());
        assertFalse(coupon.isUsable());
    }

    @Test
    void testCouponAlreadyUsed() {
        Coupon coupon = new Coupon("USED", "PERCENTAGE", new BigDecimal("0.1"), LocalDateTime.now().plusDays(1), true);
        assertFalse(coupon.isUsable());
    }
}