package id.ac.ui.cs.advprog.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.coupon.dto.CouponRequest;
import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.repository.CouponRepository;
import id.ac.ui.cs.advprog.coupon.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
public class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @MockBean
    private CouponRepository couponRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        coupon = new Coupon("TEST", CouponType.FIXED, new BigDecimal("10000"),
                new BigDecimal("50000"), LocalDateTime.now().plusDays(1), 5);
        coupon.setUsedCount(0);
    }

    @Test
    void testCreateCoupon() throws Exception {
        CouponRequest request = new CouponRequest();
        request.setCode("TEST");
        request.setType(CouponType.FIXED);
        request.setValue(new BigDecimal("10000"));
        request.setMinimumPurchase(new BigDecimal("50000"));
        request.setExpiredAt(LocalDateTime.now().plusDays(1));
        request.setQuota(5);
        request.setUsedCount(0);

        when(couponService.createCoupon(any(Coupon.class)))
                .thenReturn(CompletableFuture.completedFuture("Coupon created successfully"));

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon created successfully"));
    }

    @Test
    void testGetCoupon() throws Exception {
        when(couponService.getCoupon("TEST"))
                .thenReturn(CompletableFuture.completedFuture(coupon));

        mockMvc.perform(get("/coupon/TEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TEST"))
                .andExpect(jsonPath("$.type").value("FIXED"));
    }

    @Test
    void testUpdateCoupon() throws Exception {
        when(couponRepository.findById("TEST"))
                .thenReturn(Optional.of(coupon));

        CouponRequest request = new CouponRequest();
        request.setCode("TEST");
        request.setType(CouponType.FIXED);
        request.setValue(new BigDecimal("15000"));
        request.setMinimumPurchase(new BigDecimal("50000"));
        request.setExpiredAt(LocalDateTime.now().plusDays(1));
        request.setQuota(10);
        request.setUsedCount(2);

        doNothing().when(couponService).updateCoupon(any(Coupon.class));

        mockMvc.perform(put("/coupon/TEST")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon updated successfully"));
    }

    @Test
    void testDeleteCoupon() throws Exception {
        when(couponRepository.findById("TEST"))
                .thenReturn(Optional.of(coupon));
        doNothing().when(couponService).deleteCoupon("TEST");

        mockMvc.perform(delete("/coupon/TEST"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon deleted successfully"));
    }

    @Test
    void testApplyCoupon() throws Exception {
        when(couponService.applyCoupon("TEST", new BigDecimal("100000")))
                .thenReturn(CompletableFuture.completedFuture(new BigDecimal("90000")));

        mockMvc.perform(post("/coupon/TEST/apply")
                        .param("total", "100000"))
                .andExpect(status().isOk())
                .andExpect(content().string("90000"));
    }
}
