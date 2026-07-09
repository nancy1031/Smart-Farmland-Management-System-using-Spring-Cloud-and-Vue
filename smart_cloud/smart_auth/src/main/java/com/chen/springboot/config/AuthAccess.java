package com.chen.springboot.config;

import java.lang.annotation.*;


//权限授权
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthAccess {

}
