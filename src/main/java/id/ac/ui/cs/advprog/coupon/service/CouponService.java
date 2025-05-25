    package id.ac.ui.cs.advprog.coupon.service;

    import id.ac.ui.cs.advprog.coupon.model.Coupon;
    import id.ac.ui.cs.advprog.coupon.repository.CouponRepository;
    import id.ac.ui.cs.advprog.coupon.strategy.DiscountStrategyFactory;
    import org.springframework.stereotype.Service;
    import org.springframework.scheduling.annotation.Async;
    import java.util.concurrent.CompletableFuture;

    import java.math.BigDecimal;
    import java.util.Collection;

    @Service
    public class CouponService {
        private final CouponRepository repository; // sekarang JpaRepository
        private final DiscountStrategyFactory factory;

        public CouponService(CouponRepository repository, DiscountStrategyFactory factory) {
            this.repository = repository;
            this.factory = factory;
        }

        @Async
        public CompletableFuture<String> createCoupon(Coupon coupon) {
            validateCoupon(coupon);
            if (repository.existsById(coupon.getCode())) {
                throw new IllegalStateException("Coupon already exists: " + coupon.getCode());
            }
            repository.save(coupon);
            return CompletableFuture.completedFuture("Coupon created successfully");
        }

        @Async
        public CompletableFuture<Coupon> getCoupon(String code) {
            return CompletableFuture.supplyAsync(() ->
                    repository.findById(code)
                            .orElseThrow(() -> new IllegalStateException("Coupon not found: " + code)));
        }

        public void updateCoupon(Coupon incomingCoupon) {
            Coupon existingCoupon = repository.findById(incomingCoupon.getCode())
                    .orElseThrow(() -> new IllegalStateException("Cannot update non-existing coupon: " + incomingCoupon.getCode()));

            // Hanya izinkan perubahan pada quota
            if (!incomingCoupon.getType().equals(existingCoupon.getType()) ||
                    incomingCoupon.getValue().compareTo(existingCoupon.getValue()) != 0 ||
                    incomingCoupon.getMinimumPurchase().compareTo(existingCoupon.getMinimumPurchase()) != 0 ||
                    !incomingCoupon.getExpiredAt().equals(existingCoupon.getExpiredAt()) ||
                    incomingCoupon.getUsedCount() != existingCoupon.getUsedCount()) {
                throw new IllegalStateException("Only quota can be updated");
            }

            // Validasi quota baru
            if (incomingCoupon.getQuota() < existingCoupon.getUsedCount()) {
                throw new IllegalStateException("Quota cannot be less than used count");
            }

            // Update hanya field quota
            existingCoupon.setQuota(incomingCoupon.getQuota());
            repository.save(existingCoupon);
        }



        public void deleteCoupon(String code) {
            if (!repository.existsById(code)) {
                throw new IllegalStateException("Coupon not found: " + code);
            }
            repository.deleteById(code);
        }

        @Async
        public CompletableFuture<BigDecimal> applyCoupon(String code, BigDecimal total) {
            return getCoupon(code).thenApply(coupon -> {
                if (!coupon.isUsable(total)) {
                    throw new IllegalStateException("Invalid, expired, or insufficient purchase");
                }
                BigDecimal discounted = factory.resolve(coupon).apply(total);
                coupon.incrementUsedCount();
                repository.save(coupon);
                return discounted;
            });
        }

        @Async
        public CompletableFuture<Collection<Coupon>> getAllCoupons() {
            return CompletableFuture.supplyAsync(() -> repository.findAll());
        }

        private void validateCoupon(Coupon coupon) {
            if (coupon.getCode() == null || !coupon.getCode().matches("^[a-zA-Z0-9_-]+$")) {
                throw new IllegalArgumentException("Invalid coupon code format.");
            }
            if (coupon.getType() == null) {
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
            if (coupon.getQuota() <= 0){
                throw new IllegalArgumentException("Quota value must be greater than zero.");
            }

        }
    }
