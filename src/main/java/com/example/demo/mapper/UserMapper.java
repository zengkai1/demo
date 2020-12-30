package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.co.LoginUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  用户映射Mapper
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since: 2020/12/14 16:55
 */
public interface UserMapper extends BaseMapper<LoginUser> {

}
