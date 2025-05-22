package id.ac.ui.cs.advprog.coupon.service;

import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.repository.CouponRepository;
import id.ac.ui.cs.advprog.coupon.strategy.DiscountStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CouponServiceTest {

    @Autowired
    private CouponRepository repository;

    private CouponService couponService;

    @BeforeEach
    void setUp() {
        DiscountStrategyFactory factory = new DiscountStrategyFactory();
        couponService = new CouponService(repository, factory);

        Coupon coupon = new Coupon("DISKON10", CouponType.PERCENTAGE, new BigDecimal("10"),
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
        Coupon expired = new Coupon("EXPIRED", CouponType.PERCENTAGE, new BigDecimal("10"),
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
        Coupon exhausted = new Coupon("EXHAUSTED", CouponType.FIXED, new BigDecimal("10000"),
                new BigDecimal("20000"), LocalDateTime.now().plusDays(1), 1);
        exhausted.setUsedCount(1);
        repository.save(exhausted);

        assertThrows(IllegalStateException.class, () ->
                couponService.applyCoupon("EXHAUSTED", new BigDecimal("30000"))
        );
    }

    @Test
    void testUsedCountIncrementedAfterApply() {
        couponService.applyCoupon("DISKON10", new BigDecimal("100000"));
        Coupon updated = repository.findById("DISKON10").orElseThrow();
        assertEquals(1, updated.getUsedCount());
    }

    @Test
    void testUpdateCouponQuotaSuccessfully() {
        Coupon original = repository.findById("DISKON10").orElseThrow();
        assertEquals(5, original.getQuota());

        Coupon updated = new Coupon("DISKON10", original.getType(), original.getValue(),
                original.getMinimumPurchase(), original.getExpiredAt(), 10);
        updated.setUsedCount(original.getUsedCount());

        couponService.updateCoupon(updated);

        Coupon afterUpdate = repository.findById("DISKON10").orElseThrow();
        assertEquals(10, afterUpdate.getQuota());
        assertEquals(original.getType(), afterUpdate.getType());
        assertEquals(original.getValue(), afterUpdate.getValue());
    }

    @Test
    void testUpdateCouponWithDifferentValueShouldThrow() {
        Coupon original = repository.findById("DISKON10").orElseThrow();

        Coupon invalidUpdate = new Coupon("DISKON10", original.getType(), new BigDecimal("20"),
                original.getMinimumPurchase(), original.getExpiredAt(), 10);
        invalidUpdate.setUsedCount(original.getUsedCount());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            couponService.updateCoupon(invalidUpdate);
        });

        assertEquals("Only quota can be updated", exception.getMessage());
    }

    @Test
    void testUpdateCouponWithLowerQuotaThanUsedCountShouldThrow() {
        Coupon original = repository.findById("DISKON10").orElseThrow();
        original.setUsedCount(4);
        repository.save(original);

        Coupon update = new Coupon("DISKON10", original.getType(), original.getValue(),
                original.getMinimumPurchase(), original.getExpiredAt(), 2);
        update.setUsedCount(4); // simulasikan already-used

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            couponService.updateCoupon(update);
        });

        assertEquals("Quota cannot be less than used count", exception.getMessage());
    }

}
