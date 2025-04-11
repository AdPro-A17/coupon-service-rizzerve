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
        Coupon coupon = new Coupon("SAVE10", "PERCENTAGE", new BigDecimal("0.1"), LocalDateTime.now().plusDays(1), false);
        repository.save(coupon);

        Coupon result = repository.find("SAVE10");
        assertNotNull(result);
        assertEquals("SAVE10", result.getCode());
        assertEquals(new BigDecimal("0.1"), result.getValue());
    }

    @Test
    void testFindNonExistingCouponReturnsNull() {
        Coupon result = repository.find("UNKNOWN");
        assertNull(result);
    }
}