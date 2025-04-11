package id.ac.ui.cs.advprog.coupon.repository;

import id.ac.ui.cs.advprog.coupon.model.Coupon;

import java.util.concurrent.ConcurrentHashMap;

public class CouponRepository {
    private final ConcurrentHashMap<String, Coupon> store = new ConcurrentHashMap<>();

    public Coupon find(String code) {
        return store.get(code);
    }

    public void save(Coupon coupon) {
        store.put(coupon.getCode(), coupon);
    }
}