package id.ac.ui.cs.advprog.coupon.controller;

import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public String createCoupon(@RequestBody CouponRequest request) {
        try {
            couponService.createCoupon(toCoupon(request));
            return "Coupon created successfully";
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{code}")
    public Coupon getCoupon(@PathVariable String code) {
        try {
            return couponService.getCoupon(code);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{code}")
    public String updateCoupon(@PathVariable String code, @RequestBody CouponRequest request) {
        if (!code.equals(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon code mismatch");
        }
        try {
            couponService.updateCoupon(toCoupon(request));
            return "Coupon updated successfully";
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{code}")
    public String deleteCoupon(@PathVariable String code) {
        try {
            couponService.deleteCoupon(code);
            return "Coupon deleted successfully";
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    public Collection<Coupon> listCoupons() {
        return couponService.getAllCoupons();
    }

    @PostMapping("/{code}/apply")
    public BigDecimal applyCoupon(@PathVariable String code, @RequestParam BigDecimal total) {
        try {
            return couponService.applyCoupon(code, total);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
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

        // Getters & Setters
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
