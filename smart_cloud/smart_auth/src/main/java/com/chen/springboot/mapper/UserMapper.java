package com.chen.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.springboot.controller.dto.UserPasswordDTO;
import com.chen.springboot.entity.User;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ifan
 * @since 2022-01-26
 */
public interface UserMapper extends BaseMapper<User> {

    @Update("update sys_user set password = #{newPassword} where username = #{username} and password = #{password}")
    int updatePassword(UserPasswordDTO userPasswordDTO);

//    Page<User> findPage(Page<User> page, @Param("username") String username, @Param("email") String email, @Param("address") String address);
//
 }
