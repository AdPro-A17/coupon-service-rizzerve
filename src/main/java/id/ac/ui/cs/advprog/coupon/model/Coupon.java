package id.ac.ui.cs.advprog.coupon.model;

import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons") // Eksplisit table name
public class Coupon {

    @Id
    @Column(name = "code")
    private String code; // Pakai code sebagai primary key

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CouponType type;

    @Column(name = "value", precision = 19, scale = 2)
    private BigDecimal value;

    @Column(name = "minimum_purchase", precision = 19, scale = 2)
    private BigDecimal minimumPurchase;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "quota")
    private int quota;

    @Column(name = "used_count")
    private int usedCount;

    public Coupon() {
        // Default constructor dibutuhkan oleh JPA
    }

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

    // Getters dan setters
    public String getCode() { return code; }
    public CouponType getType() { return type; }
    public BigDecimal getValue() { return value; }
    public BigDecimal getMinimumPurchase() { return minimumPurchase; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public int getQuota() { return quota; }
    public int getUsedCount() { return usedCount; }

    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }
    public void setType(CouponType type) { this.type = type; }
    public void setValue(BigDecimal value) { this.value = value; }
    public void setMinimumPurchase(BigDecimal minimumPurchase) { this.minimumPurchase = minimumPurchase; }
    public void setExpiredAt(LocalDateTime expiredAt) { this.expiredAt = expiredAt; }
    public void setQuota(int quota) { this.quota = quota; }
    public void setCode(String code) { this.code = code; }
}