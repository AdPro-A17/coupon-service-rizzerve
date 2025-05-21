package id.ac.ui.cs.advprog.coupon.model;

import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CouponTest {

    @Test
    void testCouponNotUsableWhenBelowMinimumPurchase() {
        Coupon coupon = new Coupon("MIN50K", CouponType.FIXED, new BigDecimal("10000"),
                new BigDecimal("50000"), LocalDateTime.now().plusDays(1), 5);
        BigDecimal total = new BigDecimal("40000");

        assertFalse(coupon.isUsable(total));
    }

    @Test
    void testCouponUsableWhenTotalMeetsMinimumPurchaseAndQuota() {
        Coupon coupon = new Coupon("OK", CouponType.FIXED, new BigDecimal("10000"),
                new BigDecimal("30000"), LocalDateTime.now().plusDays(1), 5);
        BigDecimal total = new BigDecimal("30000");

        assertTrue(coupon.isUsable(total));
    }

    @Test
    void testCouponNotUsableWhenQuotaExceeded() {
        Coupon coupon = new Coupon("LIMIT", CouponType.FIXED, new BigDecimal("10000"),
                new BigDecimal("10000"), LocalDateTime.now().plusDays(1), 2);
        coupon.setUsedCount(2); // quota sudah habis

        assertFalse(coupon.isUsable(new BigDecimal("15000")));
    }

    @Test
    void testCouponNotExpired() {
        Coupon coupon = new Coupon("TEST", CouponType.PERCENTAGE, new BigDecimal("10"),
                new BigDecimal("0.0"), LocalDateTime.now().plusDays(1), 5);
        assertFalse(coupon.isExpired());
        assertTrue(coupon.isUsable(new BigDecimal("100")));
    }
}
