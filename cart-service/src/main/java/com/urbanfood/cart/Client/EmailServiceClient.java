package com.urbanfood.cart.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "email-service", url = "${email.service.url}")
public interface EmailServiceClient {

    @PostMapping("/api/email/send-cart-notification")
    void sendCartNotification();
}