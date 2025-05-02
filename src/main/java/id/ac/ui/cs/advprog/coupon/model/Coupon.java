package id.ac.ui.cs.advprog.coupon.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Coupon {
    private String code;
    private String type;
    private BigDecimal value;
    private LocalDateTime expiredAt;
    private boolean used;
    
    public Coupon(String code, String type, BigDecimal value, LocalDateTime expiredAt, boolean used) {
        this.code = code;
        this.type = type;
        this.value = value;
        this.expiredAt = expiredAt;
        this.used = used;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public boolean isUsable() {
        return !used && !isExpired();
    }

    // Getters & setters
    public String getCode() { return code; }
    public String getType() { return type; }
    public BigDecimal getValue() { return value; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}