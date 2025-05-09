package id.ac.ui.cs.advprog.coupon.repository;

import id.ac.ui.cs.advprog.coupon.model.Coupon;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CouponRepository {
    private final ConcurrentHashMap<String, Coupon> store = new ConcurrentHashMap<>();

    public Coupon find(String code) {
        return store.get(code);
    }

    public void save(Coupon coupon) {
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

    public Collection<Coupon> findAll() {
        return store.values();
    }
}
