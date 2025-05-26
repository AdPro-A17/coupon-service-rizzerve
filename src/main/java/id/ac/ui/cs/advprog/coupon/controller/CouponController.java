package id.ac.ui.cs.advprog.coupon.controller;

import id.ac.ui.cs.advprog.coupon.dto.CouponRequest;
import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.concurrent.CompletableFuture;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.Collection;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<String> createCoupon(@RequestBody CouponRequest request) {
        return couponService.createCoupon(toCoupon(request));
    }

    @GetMapping("/{code}")
    public CompletableFuture<Coupon> getCoupon(@PathVariable String code) {
        return couponService.getCoupon(code);
    }

    @PutMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCoupon(@PathVariable String code) {
        try {
            couponService.deleteCoupon(code);
            return "Coupon deleted successfully";
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping
    public CompletableFuture<Collection<Coupon>> listCoupons() {
        return couponService.getAllCoupons();
    }

    @PostMapping("/{code}/apply")
    public CompletableFuture<BigDecimal> applyCoupon(@PathVariable String code, @RequestParam BigDecimal total) {
        return couponService.applyCoupon(code, total);
    }

    private Coupon toCoupon(CouponRequest request) {
        CouponType couponType = request.getType();
        if (couponType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon type is required and must be valid (e.g. FIXED, PERCENTAGE)");
        }

        Coupon coupon = new Coupon(
                request.getCode(),
                couponType,
                request.getValue(),
                request.getMinimumPurchase(),
                request.getExpiredAt(),
                request.getQuota()
        );
        coupon.setUsedCount(request.getUsedCount());
        return coupon;
    }
}
