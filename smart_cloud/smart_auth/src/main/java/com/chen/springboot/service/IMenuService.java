package com.chen.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.springboot.entity.Menu;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ifan
 * @since 2022-02-10
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
