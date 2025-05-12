
package com.controller;


import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.ConfigEntity;
import com.service.ConfigService;
import com.utils.PageUtils;
import com.utils.Response;
import com.utils.ValidatorUtils;

/**
 * 登录相关
 */
@RequestMapping("config")
@RestController
public class ConfigController{
	
	@Autowired
	private ConfigService configService;

	/**
     * 列表
     */
    @RequestMapping("/page")
    public Response page(@RequestParam Map<String, Object> params,ConfigEntity config){
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();
    	PageUtils page = configService.queryPage(params);
        return Response.ok().put("data", page);
    }
    
	/**
     * 列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public Response list(@RequestParam Map<String, Object> params,ConfigEntity config){
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();
    	PageUtils page = configService.queryPage(params);
        return Response.ok().put("data", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Response info(@PathVariable("id") String id){
        ConfigEntity config = configService.selectById(id);
        return Response.ok().put("data", config);
    }
    
    /**
     * 详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public Response detail(@PathVariable("id") String id){
        ConfigEntity config = configService.selectById(id);
        return Response.ok().put("data", config);
    }
    
    /**
     * 根据name获取信息
     */
    @RequestMapping("/info")
    public Response infoByName(@RequestParam String name){
        ConfigEntity config = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", "faceFile"));
        return Response.ok().put("data", config);
    }
    
    /**
     * 保存
     */
    @PostMapping("/save")
    public Response save(@RequestBody ConfigEntity config){
//    	ValidatorUtils.validateEntity(config);
    	configService.insert(config);
        return Response.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Response update(@RequestBody ConfigEntity config){
//        ValidatorUtils.validateEntity(config);
        configService.updateById(config);//全部更新
        return Response.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Response delete(@RequestBody Long[] ids){
    	configService.deleteBatchIds(Arrays.asList(ids));
        return Response.ok();
    }
}
