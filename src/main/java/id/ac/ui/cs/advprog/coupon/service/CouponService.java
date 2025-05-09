package id.ac.ui.cs.advprog.coupon.service;

import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.repository.CouponRepository;
import id.ac.ui.cs.advprog.coupon.strategy.DiscountStrategy;
import id.ac.ui.cs.advprog.coupon.strategy.DiscountStrategyFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;

@Service
public class CouponService {
    private final CouponRepository repository;
    private final DiscountStrategyFactory factory;

    public CouponService(CouponRepository repository, DiscountStrategyFactory factory) {
        this.repository = repository;
        this.factory = factory;
    }

    public void createCoupon(Coupon coupon) {
        validateCoupon(coupon);
        repository.save(coupon);
    }

    public Coupon getCoupon(String code) {
        Coupon coupon = repository.find(code);
        if (coupon == null) {
            throw new IllegalStateException("Coupon not found: " + code);
        }
        return coupon;
    }

    public void updateCoupon(Coupon coupon) {
        validateCoupon(coupon);
        repository.update(coupon);
    }

    public void deleteCoupon(String code) {
        if (repository.find(code) == null) {
            throw new IllegalStateException("Coupon not found: " + code);
        }
        repository.delete(code);
    }

    public BigDecimal applyCoupon(String code, BigDecimal total) {
        Coupon coupon = getCoupon(code);
        if (!coupon.isUsable(total)) {
            throw new IllegalStateException("Invalid, expired, or insufficient purchase");
        }

        DiscountStrategy strategy = factory.resolve(coupon);
        BigDecimal discounted = strategy.apply(total);

        coupon.incrementUsedCount();
        repository.update(coupon);

        return discounted;
    }

    public Collection<Coupon> getAllCoupons() {
        return repository.findAll();
    }

    private void validateCoupon(Coupon coupon) {
        if (coupon.getCode() == null || !coupon.getCode().matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Invalid coupon code format.");
        }
        if (coupon.getType() == null || !(coupon.getType().equals("FIXED") || coupon.getType().equals("PERCENTAGE"))) {
            throw new IllegalArgumentException("Coupon type must be FIXED or PERCENTAGE.");
        }
        if (coupon.getValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Coupon value must be non-negative.");
        }
        if (coupon.getMinimumPurchase().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum purchase must be non-negative.");
        }
        if (coupon.getType().equals("PERCENTAGE") && coupon.getValue().compareTo(new BigDecimal("1.0")) > 0) {
            throw new IllegalArgumentException("Percentage value must not exceed 100%.");
        }


    }
}
