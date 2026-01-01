package com.mitiok.audio_app_2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
public class HomeController {
  @GetMapping("/health")
  public String health() {
    return "OK";
  }
}

