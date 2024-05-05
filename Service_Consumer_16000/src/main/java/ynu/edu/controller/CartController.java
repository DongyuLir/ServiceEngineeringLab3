package ynu.edu.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.annotation.Resource;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ynu.edu.entity.CommonResult;
import ynu.edu.entity.User;
import ynu.edu.feign.ServiceProviderService;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/cart")
public class CartController {
@Resource
private ServiceProviderService serviceProviderService;
    @GetMapping("/getCartById1/{userId}")
//    @CircuitBreaker(name="backendA", fallbackMethod = "getCartByIdDownA")
//    @Bulkhead(name = "bulkhead1", fallbackMethod = "getCartByIdDownBulkheadA")
//    @RateLimiter(name="rate1", fallbackMethod = "getCartByIdDownRateLimiter")
//    @TimeLimiter(name="backendA", fallbackMethod = "getCartByIdDown")
    @Retry(name="retry1", fallbackMethod = "getCartByIdDown")
    public CommonResult<User> getCartById1(@PathVariable("userId") Integer userId) throws InterruptedException{
        System.out.println(new Date()+"getCartById1");
        return serviceProviderService.getUserById(userId);
    }

    @GetMapping("/getCartById2/{userId}")
    @CircuitBreaker(name="backendB", fallbackMethod = "getCartByIdDownB")
    public CommonResult<User> getCartById2(@PathVariable("userId") Integer userId) throws InterruptedException{
        System.out.println(new Date()+"getCartById2");
        return serviceProviderService.getUserById(userId);
    }

    public CommonResult<User> getCartByIdDownRateLimiter(Integer userId, Throwable e){
        //e.printStackTrace();//可以用来定位错误原因，当为了显示清晰，就先不输出这个了
        String message = "获取用户"+userId+"rate1: getCartByIdDownRateLimiter";
        System.out.println(message);
        return new CommonResult<User>(400,"A: fall back服务降级",new User());
    }

    public CommonResult<User> getCartByIdDownA(Integer userId, Throwable e){
        //e.printStackTrace();//可以用来定位错误原因，当为了显示清晰，就先不输出这个了
        String message = "获取用户"+userId+"backendA circuit breaker";
        System.out.println(message);
        return new CommonResult<User>(400,"A: fall back服务降级",new User());
    }

    public CommonResult<User> getCartByIdDown(Integer userId, Throwable e){
        //e.printStackTrace();//可以用来定位错误原因，当为了显示清晰，就先不输出这个了
        String message = "获取用户"+userId+"fall back服务降级";
        System.out.println(message);
        return new CommonResult<User>(400,"fall back服务降级",new User());
    }

    public CommonResult<User> getCartByIdDownBulkheadA(Integer userId, Throwable e){
        //e.printStackTrace();//可以用来定位错误原因，当为了显示清晰，就先不输出这个了
        String message = "获取用户"+userId+"fall back: Bulkhead1";
        System.out.println(message);
        return new CommonResult<User>(400,"A: fall back服务降级",new User());
    }

    public CommonResult<User> getCartByIdDownB(Integer userId, Throwable e){
        //e.printStackTrace();//可以用来定位错误原因，当为了显示清晰，就先不输出这个了
        String message = "获取用户"+userId+"backendB circuit breaker";
        System.out.println(message);
        return new CommonResult<User>(400,"B: fall back服务降级",new User());
    }
}
