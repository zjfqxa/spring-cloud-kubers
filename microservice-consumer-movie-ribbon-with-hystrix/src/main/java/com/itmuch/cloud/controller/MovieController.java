package com.itmuch.cloud.controller;

import com.itmuch.cloud.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.itmuch.cloud.entity.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class MovieController {
  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private UserFeignClient userFeignClient;

  @GetMapping("/movie/{id}")
  public User findByIdFeign(@PathVariable Long id) {
    return this.userFeignClient.findById(id);
  }


  @GetMapping("/movie/address/{id}")
  @HystrixCommand(fallbackMethod = "findByIdFallback")
  public User findById(@PathVariable Long id) {
    return this.restTemplate.getForObject("http://microservice-provider-user/simple/" + id, User.class);
  }

  @GetMapping("/simple/{id}")
  public Long findId(@PathVariable Long id) {
    return id;
  }

  @GetMapping("/sleep/{times}")
  public Long findSleep(@PathVariable Long times) {
    new Thread() {
      public void run() {
        try {
          Thread.sleep(times);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }.start();
    return times;
  }

  public User findByIdFallback(Long id) {
    User user = new User();
    user.setId(0L);
    return user;
  }
}
