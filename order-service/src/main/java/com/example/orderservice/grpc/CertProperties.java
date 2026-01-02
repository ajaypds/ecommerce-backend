package com.example.orderservice.grpc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "certs")
@Data
public class CertProperties {
    private String certificateChain;
    private String privateKey;
    private String trustCertCollection;
}
