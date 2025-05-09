package id.ac.ui.cs.advprog.coupon.service;

import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.repository.CouponRepository;
import id.ac.ui.cs.advprog.coupon.strategy.DiscountStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CouponServiceTest {
    private CouponService couponService;
    private CouponRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CouponRepository();
        DiscountStrategyFactory factory = new DiscountStrategyFactory();
        couponService = new CouponService(repository, factory);

        Coupon coupon = new Coupon("DISKON10", "PERCENTAGE", new BigDecimal("10"),
                new BigDecimal("0"), LocalDateTime.now().plusDays(1), 5);
        repository.save(coupon);
    }

    @Test
    void testApplyValidPercentageCoupon() {
        BigDecimal total = new BigDecimal("100000");

        BigDecimal result = couponService.applyCoupon("DISKON10", total);
        assertEquals(0, result.compareTo(new BigDecimal("90000")));
    }

    @Test
    void testApplyExpiredCouponShouldThrow() {
        Coupon expired = new Coupon("EXPIRED", "PERCENTAGE", new BigDecimal("10"),
                new BigDecimal("0"), LocalDateTime.now().minusDays(1), 5);
        repository.save(expired);

        assertThrows(IllegalStateException.class, () ->
                couponService.applyCoupon("EXPIRED", new BigDecimal("100000"))
        );
    }

    @Test
    void testApplyNonexistentCouponShouldThrow() {
        assertThrows(IllegalStateException.class, () ->
                couponService.applyCoupon("UNKNOWN", new BigDecimal("50000"))
        );
    }

    @Test
    void testApplyCouponWithExhaustedQuotaShouldThrow() {
        Coupon exhausted = new Coupon("EXHAUSTED", "FIXED", new BigDecimal("10000"),
                new BigDecimal("20000"), LocalDateTime.now().plusDays(1), 1);
        exhausted.setUsedCount(1);
        repository.save(exhausted);

        assertThrows(IllegalStateException.class, () ->
                couponService.applyCoupon("EXHAUSTED", new BigDecimal("30000"))
        );
    }

    @Test
    void testUsedCountIncrementedAfterApply() {
        Coupon coupon = repository.find("DISKON10");
        assertEquals(0, coupon.getUsedCount());

        couponService.applyCoupon("DISKON10", new BigDecimal("100000"));

        Coupon updated = repository.find("DISKON10");
        assertEquals(1, updated.getUsedCount());
    }

}
