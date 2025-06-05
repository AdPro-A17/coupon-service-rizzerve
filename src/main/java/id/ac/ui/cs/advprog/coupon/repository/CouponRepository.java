package id.ac.ui.cs.advprog.coupon.repository;

import id.ac.ui.cs.advprog.coupon.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, String> {
}
