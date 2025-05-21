package id.ac.ui.cs.advprog.coupon.repository;

import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import id.ac.ui.cs.advprog.coupon.model.Coupon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")  // gunakan application-test.properties
public class CouponRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    void testSaveCoupon() {
        Coupon coupon = buildSampleCoupon("SAVE10");
        Coupon saved = couponRepository.save(coupon);

        assertEquals(coupon.getCode(), saved.getCode());
        assertEquals(coupon.getValue(), saved.getValue());
    }

    @Test
    void testFindAllReturnsSavedCoupons() {
        Coupon coupon1 = buildSampleCoupon("DISC1");
        Coupon coupon2 = buildSampleCoupon("DISC2");

        entityManager.persist(coupon1);
        entityManager.persist(coupon2);
        entityManager.flush();

        assertEquals(2, couponRepository.findAll().size());
    }

    @Test
    void testFindByIdReturnsCorrectCoupon() {
        Coupon coupon = buildSampleCoupon("SPECIAL");
        entityManager.persist(coupon);
        entityManager.flush();

        Optional<Coupon> result = couponRepository.findById("SPECIAL");

        assertTrue(result.isPresent());
        assertEquals("SPECIAL", result.get().getCode());
    }

    @Test
    void testFindByIdReturnsEmptyForUnknownCode() {
        Optional<Coupon> result = couponRepository.findById("UNKNOWN");
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
