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

    }

    public BigDecimal applyCoupon(String code, BigDecimal total) {

    }
}