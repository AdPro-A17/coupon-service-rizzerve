package id.ac.ui.cs.advprog.coupon.controller;

import id.ac.ui.cs.advprog.coupon.dto.CouponRequest;
import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    private Coupon coupon1;
    private Coupon coupon2;
    private CouponRequest couponRequest1;

    @BeforeEach
    void setUp() {
        coupon1 = new Coupon("DISKON10K", CouponType.FIXED, BigDecimal.valueOf(10000), BigDecimal.valueOf(50000), LocalDateTime.now().plusDays(30), 100);
        coupon1.setUsedCount(0);

        coupon2 = new Coupon("POTONGAN10", CouponType.PERCENTAGE, BigDecimal.valueOf(10), BigDecimal.valueOf(20000), LocalDateTime.now().plusDays(60), 50);
        coupon2.setUsedCount(5);

        couponRequest1 = new CouponRequest();
        couponRequest1.setCode("DISKON10K");
        couponRequest1.setType(CouponType.FIXED);
        couponRequest1.setValue(BigDecimal.valueOf(10000));
        couponRequest1.setMinimumPurchase(BigDecimal.valueOf(50000));
        couponRequest1.setExpiredAt(LocalDateTime.now().plusDays(30));
        couponRequest1.setQuota(100);
        couponRequest1.setUsedCount(0);
    }

    private Coupon toCoupon(CouponRequest request) {
        // This is a helper method mirroring the one in the controller,
        // or you can make the controller's toCoupon method public if preferred.
        if (request.getType() == null) {
            // This scenario is handled by the controller's toCoupon method before service call
            // For testing controller logic, we assume valid request or test specific validation failure
            return null;
        }
        Coupon coupon = new Coupon(
                request.getCode(),
                request.getType(),
                request.getValue(),
                request.getMinimumPurchase(),
                request.getExpiredAt(),
                request.getQuota()
        );
        coupon.setUsedCount(request.getUsedCount());
        return coupon;
    }


    @Test
    void createCoupon_shouldReturnCreatedMessage() throws Exception {
        when(couponService.createCoupon(any(Coupon.class))).thenReturn(CompletableFuture.completedFuture("Coupon created successfully"));

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponRequest1)))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon created successfully"));

        verify(couponService, times(1)).createCoupon(any(Coupon.class));
    }

    @Test
    void createCoupon_whenTypeIsNull_shouldReturnBadRequest() throws Exception {
        CouponRequest badRequest = new CouponRequest();
        badRequest.setCode("BADCODE");
        badRequest.setType(null); // Invalid type
        badRequest.setValue(BigDecimal.TEN);

        // The actual exception is thrown by the controller's toCoupon method
        // MockMvc will capture this and return a 400 Bad Request
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Coupon type is required and must be valid (e.g. FIXED, PERCENTAGE)")));

        verify(couponService, never()).createCoupon(any(Coupon.class));
    }

    @Test
    void getCoupon_shouldReturnCoupon() throws Exception {
        when(couponService.getCoupon("DISKON10K")).thenReturn(CompletableFuture.completedFuture(coupon1));

        mockMvc.perform(get("/coupon/DISKON10K"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("DISKON10K")))
                .andExpect(jsonPath("$.type", is("FIXED")))
                .andExpect(jsonPath("$.value", is(10000.0))) // ObjectMapper might convert BigDecimal to double
                .andExpect(jsonPath("$.minimumPurchase", is(50000.0)))
                .andExpect(jsonPath("$.quota", is(100)));

        verify(couponService, times(1)).getCoupon("DISKON10K");
    }



    @Test
    void updateCoupon_shouldReturnSuccessMessage() throws Exception {
        // For void methods that might throw exceptions, use doNothing() or doThrow()
        doNothing().when(couponService).updateCoupon(any(Coupon.class));

        mockMvc.perform(put("/coupon/DISKON10K")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponRequest1)))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon updated successfully"));

        verify(couponService, times(1)).updateCoupon(any(Coupon.class));
    }

    @Test
    void updateCoupon_whenCodeMismatch_shouldReturnBadRequest() throws Exception {
        CouponRequest mismatchedRequest = new CouponRequest();
        mismatchedRequest.setCode("NEWCODE"); // Different from path variable
        mismatchedRequest.setType(CouponType.FIXED);
        mismatchedRequest.setValue(BigDecimal.TEN);


        mockMvc.perform(put("/coupon/OLDCODE") // Path variable is "OLDCODE"
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mismatchedRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Coupon code mismatch")));

        verify(couponService, never()).updateCoupon(any(Coupon.class));
    }

    @Test
    void updateCoupon_whenNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new IllegalStateException("Coupon not found")).when(couponService).updateCoupon(any(Coupon.class));

        mockMvc.perform(put("/coupon/DISKON10K")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponRequest1)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Coupon not found")));

        verify(couponService, times(1)).updateCoupon(any(Coupon.class));
    }

    @Test
    void deleteCoupon_shouldReturnSuccessMessage() throws Exception {
        doNothing().when(couponService).deleteCoupon("DISKON10K");

        mockMvc.perform(delete("/coupon/DISKON10K"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon deleted successfully"));

        verify(couponService, times(1)).deleteCoupon("DISKON10K");
    }

    @Test
    void deleteCoupon_whenNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new IllegalStateException("Coupon not found")).when(couponService).deleteCoupon("NOTFOUND");

        mockMvc.perform(delete("/coupon/NOTFOUND"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Coupon not found")));

        verify(couponService, times(1)).deleteCoupon("NOTFOUND");
    }

    @Test
    void listCoupons_shouldReturnListOfCoupons() throws Exception {
        Collection<Coupon> coupons = Arrays.asList(coupon1, coupon2);
        when(couponService.getAllCoupons()).thenReturn(CompletableFuture.completedFuture(coupons));

        mockMvc.perform(get("/coupon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", is("DISKON10K")))
                .andExpect(jsonPath("$[1].code", is("POTONGAN10")));

        verify(couponService, times(1)).getAllCoupons();
    }

    @Test
    void listCoupons_whenNoCoupons_shouldReturnEmptyList() throws Exception {
        when(couponService.getAllCoupons()).thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

        mockMvc.perform(get("/coupon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(couponService, times(1)).getAllCoupons();
    }

    @Test
    void applyCoupon_shouldReturnAppliedAmount() throws Exception {
        BigDecimal totalAmount = BigDecimal.valueOf(100000);
        BigDecimal discountedAmount = BigDecimal.valueOf(90000);
        when(couponService.applyCoupon("DISKON10K", totalAmount))
                .thenReturn(CompletableFuture.completedFuture(discountedAmount));

        mockMvc.perform(post("/coupon/DISKON10K/apply")
                        .param("total", totalAmount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(discountedAmount.toString())); // Expecting plain BigDecimal as string

        verify(couponService, times(1)).applyCoupon("DISKON10K", totalAmount);
    }

}
