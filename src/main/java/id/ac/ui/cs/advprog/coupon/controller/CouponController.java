package id.ac.ui.cs.advprog.coupon.controller;

import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.repository.CouponRepository;
import id.ac.ui.cs.advprog.coupon.service.CouponService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;
    private final CouponRepository couponRepository;

    public CouponController(CouponService couponService, CouponRepository couponRepository) {
        this.couponService = couponService;
        this.couponRepository = couponRepository;
    }

    @PostMapping
    public String createCoupon(@RequestBody CouponRequest request) {
        Coupon coupon = toCoupon(request);
        couponRepository.save(coupon);
        return "Coupon created successfully";
    }

    @GetMapping("/{code}")
    public Coupon getCoupon(@PathVariable String code) {
        Coupon coupon = couponRepository.find(code);
        if (coupon == null) throw new IllegalArgumentException("Coupon not found");
        return coupon;
    }

    @PutMapping("/{code}")
    public String updateCoupon(@PathVariable String code, @RequestBody CouponRequest request) {
        if (!code.equals(request.getCode())) {
            throw new IllegalArgumentException("Coupon code mismatch");
        }

        if (couponRepository.find(code) == null) {
            throw new IllegalArgumentException("Coupon not found");
        }

        Coupon updated = toCoupon(request);
        couponRepository.update(updated);
        return "Coupon updated successfully";
    }

    @DeleteMapping("/{code}")
    public String deleteCoupon(@PathVariable String code) {
        if (couponRepository.find(code) == null) {
            throw new IllegalArgumentException("Coupon not found");
        }

        couponRepository.delete(code);
        return "Coupon deleted successfully";
    }

    @PostMapping("/{code}/apply")
    public BigDecimal applyCoupon(@PathVariable String code, @RequestParam BigDecimal total) {
        return couponService.applyCoupon(code, total);
    }

    private Coupon toCoupon(CouponRequest request) {
        Coupon coupon = new Coupon(
                request.getCode(),
                request.getType(),
                request.getValue(),
                request.getMinimumPurchase(),
                request.getExpiredAt(),
                request.getQuota()
        );
        coupon.setUsedCount(request.getUsedCount());
        return coupon;
    }

    public static class CouponRequest {
        private String code;
        private String type;
        private BigDecimal value;
        private BigDecimal minimumPurchase;
        private LocalDateTime expiredAt;
        private int quota;
        private int usedCount;

        // Getters & Setters (sama seperti sebelumnya)
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

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
}
