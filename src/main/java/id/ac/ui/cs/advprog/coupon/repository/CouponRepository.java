package id.ac.ui.cs.advprog.coupon.repository;

import id.ac.ui.cs.advprog.coupon.model.Coupon;

import java.util.concurrent.ConcurrentHashMap;

public class CouponRepository {
    private final ConcurrentHashMap<String, Coupon> store = new ConcurrentHashMap<>();

    public Coupon find(String code) {
        return store.get(code);
    }

    public void save(Coupon coupon) {
        // Jangan timpa kupon yang sudah ada
        if (store.containsKey(coupon.getCode())) {
            throw new IllegalStateException("Coupon with code already exists: " + coupon.getCode());
        }
        store.put(coupon.getCode(), coupon);
    }

    public void update(Coupon coupon) {
        if (!store.containsKey(coupon.getCode())) {
            throw new IllegalStateException("Cannot update non-existing coupon: " + coupon.getCode());
        }
        store.put(coupon.getCode(), coupon);
    }

    public void delete(String code) {
        store.remove(code);
    }
}
