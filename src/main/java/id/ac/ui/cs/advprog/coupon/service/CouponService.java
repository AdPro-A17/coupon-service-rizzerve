package id.ac.ui.cs.advprog.coupon.service;

import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.repository.CouponRepository;
import id.ac.ui.cs.advprog.coupon.strategy.DiscountStrategy;
import id.ac.ui.cs.advprog.coupon.strategy.DiscountStrategyFactory;

import java.math.BigDecimal;

public class CouponService {
    private final CouponRepository repository;
    private final DiscountStrategyFactory factory;

    public CouponService(CouponRepository repository, DiscountStrategyFactory factory) {
        this.repository = repository;
        this.factory = factory;
    }

    public BigDecimal applyCoupon(String code, BigDecimal total) {
        Coupon coupon = repository.find(code);
        if (coupon == null || !coupon.isUsable(total)) {
            throw new IllegalStateException("Invalid or expired or insufficient purchase");
        }

        DiscountStrategy strategy = factory.resolve(coupon);
        BigDecimal discounted = strategy.apply(total);

        coupon.incrementUsedCount();        // ⬅️ tambahkan pemakaian
        repository.save(coupon);            // ⬅️ simpan kembali kupon

        return discounted;
    }
}
