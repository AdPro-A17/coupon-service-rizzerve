package id.ac.ui.cs.advprog.coupon.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Coupon {
    private String code;
    private String type; // Misal: "FIXED", "PERCENTAGE"
    private BigDecimal value;
    private BigDecimal minimumPurchase;
    private LocalDateTime expiredAt;
    private int quota; // Jumlah maksimum pemakaian
    private int usedCount; // Jumlah pemakaian saat ini

    public Coupon(String code, String type, BigDecimal value, BigDecimal minimumPurchase,
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

    // Getters
    public String getCode() { return code; }
    public String getType() { return type; }
    public BigDecimal getValue() { return value; }
    public BigDecimal getMinimumPurchase() { return minimumPurchase; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public int getQuota() { return quota; }
    public int getUsedCount() { return usedCount; }

    // Setters
    public void setQuota(int quota) { this.quota = quota; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }
}
