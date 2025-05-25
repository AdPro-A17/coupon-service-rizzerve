package id.ac.ui.cs.advprog.coupon.repository;

import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import id.ac.ui.cs.advprog.coupon.model.Coupon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class CouponRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    void testSaveCoupon() {
        // Given
        Coupon coupon = buildSampleCoupon("SAVE10");

        // When
        Coupon saved = couponRepository.save(coupon);
        entityManager.flush(); // Force flush to database

        // Then
        assertNotNull(saved);
        assertEquals(coupon.getCode(), saved.getCode());
        assertEquals(coupon.getValue(), saved.getValue());
        assertEquals(coupon.getType(), saved.getType());
    }

    @Test
    void testFindAllReturnsSavedCoupons() {
        // Given
        Coupon coupon1 = buildSampleCoupon("DISC1");
        Coupon coupon2 = buildSampleCoupon("DISC2");

        // When
        couponRepository.save(coupon1);
        couponRepository.save(coupon2);
        entityManager.flush();

        List<Coupon> coupons = couponRepository.findAll();

        // Then
        assertEquals(2, coupons.size());
        assertTrue(coupons.stream().anyMatch(c -> c.getCode().equals("DISC1")));
        assertTrue(coupons.stream().anyMatch(c -> c.getCode().equals("DISC2")));
    }

    @Test
    void testFindByIdReturnsCorrectCoupon() {
        // Given
        Coupon coupon = buildSampleCoupon("SPECIAL");
        couponRepository.save(coupon);
        entityManager.flush();

        // When
        Optional<Coupon> result = couponRepository.findById("SPECIAL");

        // Then
        assertTrue(result.isPresent());
        assertEquals("SPECIAL", result.get().getCode());
        assertEquals(CouponType.FIXED, result.get().getType());
    }

    @Test
    void testFindByIdReturnsEmptyForUnknownCode() {
        // When
        Optional<Coupon> result = couponRepository.findById("UNKNOWN");

        // Then
        assertTrue(result.isEmpty());
    }

    private Coupon buildSampleCoupon(String code) {
        return new Coupon(
                code,
                CouponType.FIXED,
                new BigDecimal("10000"),
                new BigDecimal("50000"),
                LocalDateTime.now().plusDays(5),
                10
        );
    }
}