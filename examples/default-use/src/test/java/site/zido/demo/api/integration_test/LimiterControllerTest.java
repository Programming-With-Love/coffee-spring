package site.zido.demo.api.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import site.zido.coffee.mvc.CommonErrorCode;
import site.zido.coffee.mvc.rest.DefaultResult;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class LimiterControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testLimitShouldWorkOnMultipleCall() throws Exception {
        long startTime = System.currentTimeMillis();
        mvc.perform(get("/limit"))
                .andExpect(status().isOk());
        MvcResult mvcResult = mvc.perform(get("/limit"))
                .andReturn();
        if (System.currentTimeMillis() - startTime < 5 * 1000) {
            Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), mvcResult.getResponse().getStatus());
            String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("rawtypes")
            DefaultResult result = mapper.readValue(content, DefaultResult.class);
            Assertions.assertEquals(3,
                    result.getCode(),
                    "响应的code应当为频率被限制:" + CommonErrorCode.LIMIT);
            Pattern pattern = Pattern.compile("^频率过高，请在 (\\d{1,2}) 秒后重试$");
            Matcher matcher = pattern.matcher(result.getMessage());
            Assertions.assertTrue(matcher.matches(), "返回响应信息错误");
            int lastTime = Integer.parseInt(matcher.group(1));
            Assertions.assertTrue(lastTime < 60, "剩余时间应小于60秒");
            Thread.sleep(6 * 1000);
            mvc.perform(get("/limit"))
                    .andExpect(status().isOk());
            mvc.perform(get("/limit"))
                    .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));
        } else {
            Assertions.fail("未能在60秒内完成请求，测试失败");
        }
    }

    @Test
    public void testLimitParamsShouldWork() throws Exception {
        String phone = "testLimitParamsShouldWork";
        long startTime = System.currentTimeMillis();
        mvc.perform(get("/limit/sms").param("phone", phone))
                .andExpect(status().isOk());
        MvcResult mvcResult = mvc.perform(get("/limit/sms").param("phone", phone))
                .andReturn();
        if (System.currentTimeMillis() - startTime < 5 * 1000) {
            Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), mvcResult.getResponse().getStatus());
            String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("rawtypes")
            DefaultResult result = mapper.readValue(content, DefaultResult.class);
            Assertions.assertEquals(3,
                    result.getCode(),
                    "响应的code应当为频率被限制:" + CommonErrorCode.LIMIT);
            Pattern pattern = Pattern.compile("^频率过高，请在 (\\d{1,2}) 秒后重试$");
            Matcher matcher = pattern.matcher(result.getMessage());
            Assertions.assertTrue(matcher.matches(), "返回响应信息错误");
            int lastTime = Integer.parseInt(matcher.group(1));
            Assertions.assertTrue(lastTime < 60, "剩余时间应小于60秒");
            Thread.sleep(6 * 1000);
            mvc.perform(get("/limit/sms").param("phone", phone))
                    .andExpect(status().isOk());
            mvc.perform(get("/limit/sms").param("phone", phone))
                    .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));
        } else {
            Assertions.fail("未能在60秒内完成请求，测试失败");
        }
    }

    @Test
    public void testLimitPathVariableShouldWork() throws Exception {
        String phone = "testLimitPathVariableShouldWork";
        long startTime = System.currentTimeMillis();
        mvc.perform(get("/limit/{phone}/sms", phone))
                .andExpect(status().isOk());
        MvcResult mvcResult = mvc.perform(get("/limit/{phone}/sms", phone))
                .andReturn();
        if (System.currentTimeMillis() - startTime < 5 * 1000) {
            Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), mvcResult.getResponse().getStatus());
            String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("rawtypes")
            DefaultResult result = mapper.readValue(content, DefaultResult.class);
            Assertions.assertEquals(3,
                    result.getCode(),
                    "响应的code应当为频率被限制:" + CommonErrorCode.LIMIT);
            Pattern pattern = Pattern.compile("^频率过高，请在 (\\d{1,2}) 秒后重试$");
            Matcher matcher = pattern.matcher(result.getMessage());
            Assertions.assertTrue(matcher.matches(), "返回响应信息错误");
            int lastTime = Integer.parseInt(matcher.group(1));
            Assertions.assertTrue(lastTime < 60, "剩余时间应小于60秒");
            Thread.sleep(6 * 1000);
            mvc.perform(get("/limit/{phone}/sms", phone))
                    .andExpect(status().isOk());
            mvc.perform(get("/limit/{phone}/sms", phone))
                    .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));
        } else {
            Assertions.fail("未能在60秒内完成请求，测试失败");
        }
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
