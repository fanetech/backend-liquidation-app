package com.example.demoQrcode.config;

import com.aveplus.uemoa.qr.service.UemoaQRService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration d'auto-configuration pour le module UEMOA QR Code
 */
@Configuration
@ComponentScan(basePackages = "com.aveplus.uemoa.qr")
public class UemoaAutoConfiguration {
    
    /**
     * Cette configuration permet à Spring Boot de scanner le package UEMOA
     * et de créer automatiquement les beans nécessaires
     */
}
