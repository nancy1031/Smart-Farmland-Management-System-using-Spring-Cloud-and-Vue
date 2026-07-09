package com.chen.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.springboot.controller.dto.UserDTO;
import com.chen.springboot.controller.dto.UserPasswordDTO;
import com.chen.springboot.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ifan
 * @since 2022-01-26
 */
public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    void updatePassword(UserPasswordDTO userPasswordDTO);

//    Page<User> findPage(Page<User> objectPage, String username, String email, String address);

}

