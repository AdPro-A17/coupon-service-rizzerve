package id.ac.ui.cs.advprog.coupon.dto;

import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CouponRequestTest {

    @Test
    public void testCouponRequestGetterSetter() {
        CouponRequest request = new CouponRequest();

        String code = "SAVE10";
        CouponType type = CouponType.PERCENTAGE;
        BigDecimal value = new BigDecimal("10.0");
        BigDecimal minimumPurchase = new BigDecimal("50.0");
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(5);
        int quota = 100;
        int usedCount = 3;

        request.setCode(code);
        request.setType(type);
        request.setValue(value);
        request.setMinimumPurchase(minimumPurchase);
        request.setExpiredAt(expiredAt);
        request.setQuota(quota);
        request.setUsedCount(usedCount);

        assertEquals(code, request.getCode());
        assertEquals(type, request.getType());
        assertEquals(value, request.getValue());
        assertEquals(minimumPurchase, request.getMinimumPurchase());
        assertEquals(expiredAt, request.getExpiredAt());
        assertEquals(quota, request.getQuota());
        assertEquals(usedCount, request.getUsedCount());
    }
}
