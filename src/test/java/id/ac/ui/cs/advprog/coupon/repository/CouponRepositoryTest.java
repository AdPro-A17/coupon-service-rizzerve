package id.ac.ui.cs.advprog.coupon.repository;

import id.ac.ui.cs.advprog.coupon.model.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CouponRepositoryTest {
    private CouponRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CouponRepository();
    }

    @Test
    void testSaveAndFindCoupon() {
        Coupon coupon = new Coupon("SAVE10", "PERCENTAGE", new BigDecimal("10"),
                new BigDecimal("0.0"), LocalDateTime.now().plusDays(1), 10);
        repository.save(coupon);

        Coupon result = repository.find("SAVE10");
        assertNotNull(result);
        assertEquals("SAVE10", result.getCode());
        assertEquals(new BigDecimal("10"), result.getValue());
    }

    @Test
    void testFindNonExistingCouponReturnsNull() {
        Coupon result = repository.find("UNKNOWN");
        assertNull(result);
    }

    @Test
    void testUpdateCouponOverwritesExisting() {
        Coupon original = new Coupon("UPDATE1", "FIXED", new BigDecimal("5000"),
                new BigDecimal("20000"), LocalDateTime.now().plusDays(1), 5);
        repository.save(original);

        Coupon updated = new Coupon("UPDATE1", "FIXED", new BigDecimal("10000"),
                new BigDecimal("20000"), LocalDateTime.now().plusDays(1), 10);
        repository.save(updated); // overwrite

        Coupon result = repository.find("UPDATE1");
        assertNotNull(result);
        assertEquals(new BigDecimal("10000"), result.getValue());
        assertEquals(10, result.getQuota());
    }

    @Test
    void testDeleteCoupon() {
        Coupon coupon = new Coupon("DELETE1", "PERCENTAGE", new BigDecimal("5"),
                new BigDecimal("10000"), LocalDateTime.now().plusDays(1), 5);
        repository.save(coupon);

        // Simulasi delete manual
        repository.delete("DELETE1");

        Coupon result = repository.find("DELETE1");
        assertNull(result);
    }
}
