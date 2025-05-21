package id.ac.ui.cs.advprog.coupon.model;

import id.ac.ui.cs.advprog.coupon.enums.CouponType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Coupon {
    private String code;
    private CouponType type;
    private BigDecimal value;
    private BigDecimal minimumPurchase;
    private LocalDateTime expiredAt;
    private int quota;
    private int usedCount;

    public Coupon(String code, CouponType type, BigDecimal value, BigDecimal minimumPurchase,
                  LocalDateTime expiredAt, int quota) {
        this.code = code;
        this.type = type;
        this.value = value;
        this.minimumPurchase = minimumPurchase;
        this.expiredAt = expiredAt;
        this.quota = quota;
        this.usedCount = 0;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public boolean isUsable(BigDecimal totalPurchase) {
        return !isExpired()
                && totalPurchase.compareTo(minimumPurchase) >= 0
                && usedCount < quota;
    }

    public void incrementUsedCount() {
        this.usedCount += 1;
    }

    public String getCode() { return code; }
    public CouponType getType() { return type; }
    public BigDecimal getValue() { return value; }
    public BigDecimal getMinimumPurchase() { return minimumPurchase; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public int getQuota() { return quota; }
    public int getUsedCount() { return usedCount; }

    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }
}
