package com.deepprotech.deepproject.common.config;

import com.deepprotech.deepproject.common.audit.AuditContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuditContextInterceptor auditContextInterceptor;

    public WebConfig(AuditContextInterceptor auditContextInterceptor) {
        this.auditContextInterceptor = auditContextInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditContextInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
    }
}