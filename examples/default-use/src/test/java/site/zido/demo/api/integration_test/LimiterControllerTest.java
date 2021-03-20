package site.zido.demo.api.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import site.zido.coffee.extra.limiter.FrequencyLimiter;
import site.zido.coffee.mvc.CommonErrorCode;
import site.zido.coffee.mvc.rest.DefaultResult;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class LimiterControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private FrequencyLimiter limiter;

    @Test
    public void testLimitShouldWorkOnMultipleCall() throws Exception {
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(0L);
        mvc.perform(get("/limit"))
                .andExpect(status().isOk());
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(4000L);
        MvcResult mvcResult = mvc.perform(get("/limit"))
                .andReturn();
        Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("rawtypes")
        DefaultResult result = mapper.readValue(content, DefaultResult.class);
        Assertions.assertEquals(3,
                result.getCode(),
                "响应的code应当为频率被限制:" + CommonErrorCode.LIMIT);
        Assertions.assertEquals("频率过高，请在 4 秒后重试", result.getMessage(), "响应信息错误");
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(0L);
        mvc.perform(get("/limit"))
                .andExpect(status().isOk());
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(5000L);
        mvc.perform(get("/limit"))
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));
    }

    @Test
    public void testLimitParamsShouldWork() throws Exception {
        String phone = "testLimitParamsShouldWork";
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(0L);
        mvc.perform(get("/limit/sms").param("phone", phone))
                .andExpect(status().isOk());
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(5000L);
        MvcResult mvcResult = mvc.perform(get("/limit/sms").param("phone", phone))
                .andReturn();
        Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("rawtypes")
        DefaultResult result = mapper.readValue(content, DefaultResult.class);
        Assertions.assertEquals(3,
                result.getCode(),
                "响应的code应当为频率被限制:" + CommonErrorCode.LIMIT);
        Assertions.assertEquals("频率过高，请在 5 秒后重试", result.getMessage(), "响应信息错误");
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(0L);
        mvc.perform(get("/limit/sms").param("phone", phone))
                .andExpect(status().isOk());
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(5000L);
        mvc.perform(get("/limit/sms").param("phone", phone))
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));
    }

    @Test
    public void testLimitPathVariableShouldWork() throws Exception {
        String phone = "testLimitPathVariableShouldWork";
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(0L);
        mvc.perform(get("/limit/{phone}/sms", phone))
                .andExpect(status().isOk());
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(5000L);
        MvcResult mvcResult = mvc.perform(get("/limit/{phone}/sms", phone))
                .andReturn();
        Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("rawtypes")
        DefaultResult result = mapper.readValue(content, DefaultResult.class);
        Assertions.assertEquals(3,
                result.getCode(),
                "响应的code应当为频率被限制:" + CommonErrorCode.LIMIT);
        Assertions.assertEquals("频率过高，请在 5 秒后重试", result.getMessage(), "响应信息错误");
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(0L);
        mvc.perform(get("/limit/{phone}/sms", phone))
                .andExpect(status().isOk());
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(5000L);
        mvc.perform(get("/limit/{phone}/sms", phone))
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));
    }

    /**
     * 并发测试
     * <p>
     * 当并发请求过来，仅允许一个请求访问成功，其他拒绝
     *
     * @throws Exception ex
     */
    @Test
    public void testWhenConcurrentShouldOneWork() throws Exception {
        String phone = "testWhenConcurrentShouldOneWork";
        CountDownLatch runningLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(10);
        AtomicInteger success = new AtomicInteger(0);
        when(limiter.tryGet(anyString(), eq(TimeUnit.SECONDS.toMillis(5)))).thenReturn(0L).thenReturn(5000L);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    runningLatch.await();
                    if (System.currentTimeMillis() - startTime < 6 * 1000) {
                        mvc.perform(get("/limit/sms").param("phone", phone))
                                .andDo(result -> {
                                    if (result.getResponse().getStatus() == 200) {
                                        success.incrementAndGet();
                                    }
                                });
                    } else {
                        System.out.println("等待超时");
                    }
                    finishLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        runningLatch.countDown();
        finishLatch.await();
        Assertions.assertEquals(1, success.get(), "同时必须有且仅有一次请求执行成功");
    }
}
