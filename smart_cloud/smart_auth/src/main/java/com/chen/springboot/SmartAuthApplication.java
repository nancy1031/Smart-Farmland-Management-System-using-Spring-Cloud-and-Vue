//package com.chen.springboot;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class SmartAuthApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(SmartAuthApplication.class, args);
//    }
//
//}

package com.chen.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SmartAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartAuthApplication.class, args);
    }

    /**
     * 跨域配置类
     * 放在这里作为内部类，方便管理且避免文件名冲突
     */
    @Configuration
    static class CorsConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    // 允许所有来源访问（开发环境用 * 没问题）
                    .allowedOriginPatterns("*")
                    // 允许的请求方法
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    // 允许携带 Cookie/凭证
                    .allowCredentials(true)
                    // 预检请求的有效期，单位为秒
                    .maxAge(3600);
        }
    }
}
