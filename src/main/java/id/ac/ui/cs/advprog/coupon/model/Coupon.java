package id.ac.ui.cs.advprog.coupon.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Coupon {
    private String code;
    private String type; // Misal: "FIXED", "PERCENTAGE"
    private BigDecimal value;
    private BigDecimal minimumPurchase;
    private LocalDateTime expiredAt;
    private boolean used;

    public Coupon(String code, String type, BigDecimal value, BigDecimal minimumPurchase,
                  LocalDateTime expiredAt, boolean used) {
        this.code = code;
        this.type = type;
        this.value = value;
        this.minimumPurchase = minimumPurchase;
        this.expiredAt = expiredAt;
        this.used = used;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public boolean isUsable(BigDecimal totalPurchase) {
        return !used && !isExpired() && totalPurchase.compareTo(minimumPurchase) >= 0;
    }

    public String getCode() { return code; }
    public String getType() { return type; }
    public BigDecimal getValue() { return value; }
    public BigDecimal getMinimumPurchase() { return minimumPurchase; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}