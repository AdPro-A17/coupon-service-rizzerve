package id.ac.ui.cs.advprog.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.coupon.dto.CouponRequest;
import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
public class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CouponService couponService;

    private Coupon testCoupon;
    private CouponRequest testCouponRequest;

    @BeforeEach
    void setUp() {
        testCoupon = new Coupon(
                "TEST123",
                CouponType.FIXED,
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(100),
                LocalDateTime.now().plusDays(7),
                10
        );

        testCouponRequest = new CouponRequest();
        testCouponRequest.setCode("TEST123");
        testCouponRequest.setType(CouponType.FIXED);
        testCouponRequest.setValue(BigDecimal.valueOf(50));
        testCouponRequest.setMinimumPurchase(BigDecimal.valueOf(100));
        testCouponRequest.setExpiredAt(LocalDateTime.now().plusDays(7));
        testCouponRequest.setQuota(10);
    }



    @Test
    @WithMockUser(roles = "USER")
    void testCreateCouponWithoutAdminRole() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCouponRequest)))
                .andExpect(status().isForbidden());
    }
}