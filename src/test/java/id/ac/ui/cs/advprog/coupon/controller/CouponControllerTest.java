package id.ac.ui.cs.advprog.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.coupon.dto.CouponRequest;
import id.ac.ui.cs.advprog.coupon.enums.CouponType;
import id.ac.ui.cs.advprog.coupon.model.Coupon;
import id.ac.ui.cs.advprog.coupon.service.CouponService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
public class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;


}
