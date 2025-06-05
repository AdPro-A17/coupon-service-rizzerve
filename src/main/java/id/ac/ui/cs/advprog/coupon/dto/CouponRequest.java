package id.ac.ui.cs.advprog.coupon.dto;

import id.ac.ui.cs.advprog.coupon.enums.CouponType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponRequest {
    private String code;
    private CouponType type;
    private BigDecimal value;
    private BigDecimal minimumPurchase;
    private LocalDateTime expiredAt;
    private int quota;
    private int usedCount;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public CouponType getType() { return type; }
    public void setType(CouponType type) { this.type = type; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public BigDecimal getMinimumPurchase() { return minimumPurchase; }
    public void setMinimumPurchase(BigDecimal minimumPurchase) { this.minimumPurchase = minimumPurchase; }

    public LocalDateTime getExpiredAt() { return expiredAt; }
    public void setExpiredAt(LocalDateTime expiredAt) { this.expiredAt = expiredAt; }

    public int getQuota() { return quota; }
    public void setQuota(int quota) { this.quota = quota; }

    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }
}
