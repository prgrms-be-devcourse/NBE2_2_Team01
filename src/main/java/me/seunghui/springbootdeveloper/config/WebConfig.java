package me.seunghui.springbootdeveloper.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8080")  // 클라이언트의 주소를 정확히 명시
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);  // 쿠키 및 인증 정보 허용
    }
}
