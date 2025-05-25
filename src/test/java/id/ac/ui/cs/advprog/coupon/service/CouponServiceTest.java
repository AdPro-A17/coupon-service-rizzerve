package id.ac.ui.cs.advprog.coupon.service;

import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import id.ac.ui.cs.advprog.coupon.repository.CouponRepository;
import id.ac.ui.cs.advprog.coupon.strategy.DiscountStrategy;
import id.ac.ui.cs.advprog.coupon.strategy.DiscountStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private DiscountStrategyFactory discountStrategyFactory;

    @Mock
    private DiscountStrategy discountStrategy; // Mock for the strategy itself

    @InjectMocks
    private CouponService couponService;

    private Coupon validCoupon;
    private Coupon percentageCoupon;

    @BeforeEach
    void setUp() {
        // Initialize a valid fixed coupon for testing
        validCoupon = new Coupon(
                "DISKON10K",
                CouponType.FIXED,
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(50000),
                LocalDateTime.now().plusDays(30),
                100
        );
        validCoupon.setUsedCount(0);

        // Initialize a valid percentage coupon for testing
        percentageCoupon = new Coupon(
                "DISKON20P",
                CouponType.PERCENTAGE,
                BigDecimal.valueOf(0.2), // 20%
                BigDecimal.valueOf(30000),
                LocalDateTime.now().plusDays(15),
                50
        );
        percentageCoupon.setUsedCount(0);
    }

    // --- createCoupon Tests ---
    @Test
    void createCoupon_shouldCreateSuccessfully_whenCouponIsValidAndNotExists() throws ExecutionException, InterruptedException {
        // Mock repository behavior: coupon does not exist, save is successful
        when(couponRepository.existsById(validCoupon.getCode())).thenReturn(false);
        when(couponRepository.save(any(Coupon.class))).thenReturn(validCoupon);

        // Call the service method
        CompletableFuture<String> resultFuture = couponService.createCoupon(validCoupon);
        String result = resultFuture.get(); // Wait for CompletableFuture to complete

        // Assertions
        assertEquals("Coupon created successfully", result);
        // Verify that existsById and save methods were called once
        verify(couponRepository, times(1)).existsById(validCoupon.getCode());
        verify(couponRepository, times(1)).save(validCoupon);
    }

    @Test
    void createCoupon_shouldThrowIllegalStateException_whenCouponAlreadyExists() {
        // Mock repository behavior: coupon already exists
        when(couponRepository.existsById(validCoupon.getCode())).thenReturn(true);

        // Call the service method and assert that an exception is thrown
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            couponService.createCoupon(validCoupon).get(); // .get() to trigger execution and potential exception
        });

        // Assertions
        assertEquals("Coupon already exists: " + validCoupon.getCode(), exception.getMessage());
        // Verify that existsById was called and save was not
        verify(couponRepository, times(1)).existsById(validCoupon.getCode());
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void createCoupon_shouldThrowIllegalArgumentException_whenCouponCodeIsInvalid() {
        // Create a coupon with an invalid code
        Coupon invalidCodeCoupon = new Coupon("INVALID CODE!", CouponType.FIXED, BigDecimal.TEN, BigDecimal.ZERO, LocalDateTime.now().plusDays(1), 10);
        // Call the service method and assert that an exception is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.createCoupon(invalidCodeCoupon).get();
        });
        assertEquals("Invalid coupon code format.", exception.getMessage());
        verify(couponRepository, never()).existsById(anyString());
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void createCoupon_shouldThrowIllegalArgumentException_whenCouponTypeIsNull() {
        // Create a coupon with null type
        Coupon nullTypeCoupon = new Coupon("VALIDCODE", null, BigDecimal.TEN, BigDecimal.ZERO, LocalDateTime.now().plusDays(1), 10);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.createCoupon(nullTypeCoupon).get();
        });
        assertEquals("Coupon type must be FIXED or PERCENTAGE.", exception.getMessage());
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void createCoupon_shouldThrowIllegalArgumentException_whenCouponValueIsNegative() {
        Coupon negativeValueCoupon = new Coupon("VALIDCODE", CouponType.FIXED, BigDecimal.valueOf(-10), BigDecimal.ZERO, LocalDateTime.now().plusDays(1), 10);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.createCoupon(negativeValueCoupon).get();
        });
        assertEquals("Coupon value must be non-negative.", exception.getMessage());
    }

    @Test
    void createCoupon_shouldThrowIllegalArgumentException_whenMinimumPurchaseIsNegative() {
        Coupon negativeMinPurchaseCoupon = new Coupon("VALIDCODE", CouponType.FIXED, BigDecimal.TEN, BigDecimal.valueOf(-100), LocalDateTime.now().plusDays(1), 10);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.createCoupon(negativeMinPurchaseCoupon).get();
        });
        assertEquals("Minimum purchase must be non-negative.", exception.getMessage());
    }



    @Test
    void createCoupon_shouldThrowIllegalArgumentException_whenQuotaIsZeroOrLess() {
        Coupon zeroQuotaCoupon = new Coupon("VALIDCODE", CouponType.FIXED, BigDecimal.TEN, BigDecimal.ZERO, LocalDateTime.now().plusDays(1), 0);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.createCoupon(zeroQuotaCoupon).get();
        });
        assertEquals("Quota value must be greater than zero.", exception.getMessage());

        Coupon negativeQuotaCoupon = new Coupon("VALIDCODE2", CouponType.FIXED, BigDecimal.TEN, BigDecimal.ZERO, LocalDateTime.now().plusDays(1), -5);
        exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.createCoupon(negativeQuotaCoupon).get();
        });
        assertEquals("Quota value must be greater than zero.", exception.getMessage());
    }


    // --- getCoupon Tests ---
    @Test
    void getCoupon_shouldReturnCoupon_whenCouponExists() throws ExecutionException, InterruptedException {
        // Mock repository behavior: coupon is found
        when(couponRepository.findById(validCoupon.getCode())).thenReturn(Optional.of(validCoupon));

        // Call the service method
        CompletableFuture<Coupon> resultFuture = couponService.getCoupon(validCoupon.getCode());
        Coupon foundCoupon = resultFuture.get();

        // Assertions
        assertNotNull(foundCoupon);
        assertEquals(validCoupon.getCode(), foundCoupon.getCode());
        // Verify findById was called
        verify(couponRepository, times(1)).findById(validCoupon.getCode());
    }

    @Test
    void getCoupon_shouldThrowIllegalStateException_whenCouponNotFound() {
        String nonExistentCode = "NOTFOUND";
        // Mock repository behavior: coupon is not found
        when(couponRepository.findById(nonExistentCode)).thenReturn(Optional.empty());

        // Call the service method and assert that an exception is thrown
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            couponService.getCoupon(nonExistentCode).get(); // .get() to resolve CompletableFuture and throw wrapped exception
        });

        // Assertions
        // The actual exception is wrapped in ExecutionException by CompletableFuture
        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertEquals("Coupon not found: " + nonExistentCode, exception.getCause().getMessage());
        // Verify findById was called
        verify(couponRepository, times(1)).findById(nonExistentCode);
    }

    // --- updateCoupon Tests ---


    @Test
    void updateCoupon_shouldThrowIllegalStateException_whenCouponNotFound() {
        Coupon nonExistentCoupon = new Coupon("NONEXISTENT", CouponType.FIXED, BigDecimal.TEN, BigDecimal.ZERO, LocalDateTime.now().plusDays(1), 10);
        // Mock repository behavior: coupon is not found
        when(couponRepository.findById(nonExistentCoupon.getCode())).thenReturn(Optional.empty());

        // Call the service method and assert exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            couponService.updateCoupon(nonExistentCoupon);
        });

        // Assertions
        assertEquals("Cannot update non-existing coupon: " + nonExistentCoupon.getCode(), exception.getMessage());
        verify(couponRepository, times(1)).findById(nonExistentCoupon.getCode());
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void updateCoupon_shouldThrowIllegalStateException_whenAttemptingToChangeNonQuotaField_Type() {
        Coupon existingCoupon = new Coupon("UPDATECODE", CouponType.FIXED, BigDecimal.TEN, BigDecimal.ZERO, LocalDateTime.now().plusDays(5), 10);
        Coupon incomingCoupon = new Coupon("UPDATECODE", CouponType.PERCENTAGE, BigDecimal.TEN, BigDecimal.ZERO, LocalDateTime.now().plusDays(5), 10); // Type changed

        when(couponRepository.findById(existingCoupon.getCode())).thenReturn(Optional.of(existingCoupon));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            couponService.updateCoupon(incomingCoupon);
        });
        assertEquals("Only quota can be updated", exception.getMessage());
        verify(couponRepository, times(1)).findById(existingCoupon.getCode());
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void updateCoupon_shouldThrowIllegalStateException_whenAttemptingToChangeNonQuotaField_Value() {
        Coupon existingCoupon = new Coupon("UPDATECODE", CouponType.FIXED, BigDecimal.TEN, BigDecimal.ZERO, LocalDateTime.now().plusDays(5), 10);
        Coupon incomingCoupon = new Coupon("UPDATECODE", CouponType.FIXED, BigDecimal.ONE, BigDecimal.ZERO, LocalDateTime.now().plusDays(5), 10); // Value changed

        when(couponRepository.findById(existingCoupon.getCode())).thenReturn(Optional.of(existingCoupon));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            couponService.updateCoupon(incomingCoupon);
        });
        assertEquals("Only quota can be updated", exception.getMessage());
    }




    // --- deleteCoupon Tests ---
    @Test
    void deleteCoupon_shouldDeleteSuccessfully_whenCouponExists() {
        // Mock repository behavior
        when(couponRepository.existsById(validCoupon.getCode())).thenReturn(true);
        doNothing().when(couponRepository).deleteById(validCoupon.getCode()); // For void methods

        // Call the service method
        assertDoesNotThrow(() -> couponService.deleteCoupon(validCoupon.getCode()));

        // Verify existsById and deleteById were called
        verify(couponRepository, times(1)).existsById(validCoupon.getCode());
        verify(couponRepository, times(1)).deleteById(validCoupon.getCode());
    }

    @Test
    void deleteCoupon_shouldThrowIllegalStateException_whenCouponNotFound() {
        String nonExistentCode = "NOTFOUND";
        // Mock repository behavior
        when(couponRepository.existsById(nonExistentCode)).thenReturn(false);

        // Call the service method and assert exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            couponService.deleteCoupon(nonExistentCode);
        });

        // Assertions
        assertEquals("Coupon not found: " + nonExistentCode, exception.getMessage());
        verify(couponRepository, times(1)).existsById(nonExistentCode);
        verify(couponRepository, never()).deleteById(anyString());
    }

    // --- applyCoupon Tests ---
    @Test
    void applyCoupon_shouldReturnDiscountedTotalAndIncrementUsedCount_whenCouponIsUsable() throws ExecutionException, InterruptedException {
        BigDecimal originalTotal = BigDecimal.valueOf(100000);
        BigDecimal expectedDiscountedTotal = BigDecimal.valueOf(90000); // 100000 - 10000 (fixed discount)
        int initialUsedCount = validCoupon.getUsedCount();

        // Mock repository and factory behavior
        // getCoupon part of applyCoupon will call findById
        when(couponRepository.findById(validCoupon.getCode())).thenReturn(Optional.of(validCoupon));
        when(discountStrategyFactory.resolve(validCoupon)).thenReturn(discountStrategy);
        when(discountStrategy.apply(originalTotal)).thenReturn(expectedDiscountedTotal);
        when(couponRepository.save(any(Coupon.class))).thenReturn(validCoupon); // Mock save after incrementing used count

        // Call the service method
        CompletableFuture<BigDecimal> resultFuture = couponService.applyCoupon(validCoupon.getCode(), originalTotal);
        BigDecimal actualDiscountedTotal = resultFuture.get();

        // Assertions
        assertEquals(0, expectedDiscountedTotal.compareTo(actualDiscountedTotal)); // Using compareTo for BigDecimal
        assertEquals(initialUsedCount + 1, validCoupon.getUsedCount()); // Check if used count was incremented
        // Verify interactions
        verify(couponRepository, times(1)).findById(validCoupon.getCode());
        verify(discountStrategyFactory, times(1)).resolve(validCoupon);
        verify(discountStrategy, times(1)).apply(originalTotal);
        verify(couponRepository, times(1)).save(validCoupon);
    }

    @Test
    void applyCoupon_shouldThrowIllegalStateException_whenCouponIsUnusable_Expired() {
        // Create an expired coupon
        Coupon expiredCoupon = new Coupon("EXPIRED", CouponType.FIXED, BigDecimal.TEN, BigDecimal.ZERO, LocalDateTime.now().minusDays(1), 10);
        when(couponRepository.findById(expiredCoupon.getCode())).thenReturn(Optional.of(expiredCoupon));

        BigDecimal total = BigDecimal.valueOf(100);

        // Call the service method and assert exception
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            couponService.applyCoupon(expiredCoupon.getCode(), total).get();
        });
        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertEquals("Invalid, expired, or insufficient purchase", exception.getCause().getMessage());
        verify(couponRepository, times(1)).findById(expiredCoupon.getCode());
        verify(discountStrategyFactory, never()).resolve(any(Coupon.class));
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void applyCoupon_shouldThrowIllegalStateException_whenCouponIsUnusable_QuotaExceeded() {
        validCoupon.setQuota(5);
        validCoupon.setUsedCount(5); // Quota met
        when(couponRepository.findById(validCoupon.getCode())).thenReturn(Optional.of(validCoupon));
        BigDecimal total = BigDecimal.valueOf(60000); // Meets minimum purchase

        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            couponService.applyCoupon(validCoupon.getCode(), total).get();
        });
        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertEquals("Invalid, expired, or insufficient purchase", exception.getCause().getMessage());
    }

    @Test
    void applyCoupon_shouldThrowIllegalStateException_whenCouponIsUnusable_MinimumPurchaseNotMet() {
        when(couponRepository.findById(validCoupon.getCode())).thenReturn(Optional.of(validCoupon));
        BigDecimal total = BigDecimal.valueOf(10000); // Less than 50000 minimum

        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            couponService.applyCoupon(validCoupon.getCode(), total).get();
        });
        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertEquals("Invalid, expired, or insufficient purchase", exception.getCause().getMessage());
    }


    @Test
    void applyCoupon_shouldThrowIllegalStateException_whenCouponNotFoundDuringApplication() {
        String nonExistentCode = "NOTFOUNDAPPLY";
        when(couponRepository.findById(nonExistentCode)).thenReturn(Optional.empty());
        BigDecimal total = BigDecimal.valueOf(100);

        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            couponService.applyCoupon(nonExistentCode, total).get();
        });
        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertEquals("Coupon not found: " + nonExistentCode, exception.getCause().getMessage());
    }


    // --- getAllCoupons Tests ---
    @Test
    void getAllCoupons_shouldReturnAllCoupons() throws ExecutionException, InterruptedException {
        // Create a list of coupons
        List<Coupon> coupons = Arrays.asList(validCoupon, percentageCoupon);
        // Mock repository behavior
        when(couponRepository.findAll()).thenReturn(coupons);

        // Call the service method
        CompletableFuture<Collection<Coupon>> resultFuture = couponService.getAllCoupons();
        Collection<Coupon> foundCoupons = resultFuture.get();

        // Assertions
        assertNotNull(foundCoupons);
        assertEquals(2, foundCoupons.size());
        assertTrue(foundCoupons.contains(validCoupon));
        assertTrue(foundCoupons.contains(percentageCoupon));
        // Verify findAll was called
        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void getAllCoupons_shouldReturnEmptyList_whenNoCouponsExist() throws ExecutionException, InterruptedException {
        // Mock repository behavior to return an empty list
        when(couponRepository.findAll()).thenReturn(List.of());

        CompletableFuture<Collection<Coupon>> resultFuture = couponService.getAllCoupons();
        Collection<Coupon> foundCoupons = resultFuture.get();

        assertNotNull(foundCoupons);
        assertTrue(foundCoupons.isEmpty());
        verify(couponRepository, times(1)).findAll();
    }
}
