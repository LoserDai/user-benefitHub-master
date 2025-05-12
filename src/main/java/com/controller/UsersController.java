
package com.controller;


import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.UsersEntity;
import com.service.TokenService;
import com.utils.MPUtil;
import com.utils.PageUtils;
import com.utils.Response;

/**
 * 登录相关
 */
@RequestMapping("users")
@RestController
public class UsersController {
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private TokenService tokenService;

	/**
	 * 登录
	 */
	@IgnoreAuth
	@PostMapping(value = "/login")
	public Response login(String username, String password, String captcha, HttpServletRequest request) {
		UsersEntity user = usersService.selectOne(new EntityWrapper<UsersEntity>().eq("username", username));
		if(user==null || !user.getPassword().equals(password)) {
			return Response.error("账号或密码不正确");
		}
		String token = tokenService.generateToken(user.getId(),username, "users", user.getRole());
		Response r = Response.ok();
		r.put("token", token);
		r.put("role",user.getRole());
		r.put("userId",user.getId());
		return r;
	}
	
	/**
	 * 注册
	 */
	@IgnoreAuth
	@PostMapping(value = "/register")
	public Response register(@RequestBody UsersEntity user){
//    	ValidatorUtils.validateEntity(user);
    	if(usersService.selectOne(new EntityWrapper<UsersEntity>().eq("username", user.getUsername())) !=null) {
    		return Response.error("用户已存在");
    	}
        usersService.insert(user);
        return Response.ok();
    }

	/**
	 * 退出
	 */
	@GetMapping(value = "logout")
	public Response logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return Response.ok("退出成功");
	}
	
	/**
     * 密码重置
     */
    @IgnoreAuth
	@RequestMapping(value = "/resetPass")
    public Response resetPass(String username, HttpServletRequest request){
    	UsersEntity user = usersService.selectOne(new EntityWrapper<UsersEntity>().eq("username", username));
    	if(user==null) {
    		return Response.error("账号不存在");
    	}
    	user.setPassword("123456");
        usersService.update(user,null);
        return Response.ok("密码已重置为：123456");
    }
	
	/**
     * 列表
     */
    @RequestMapping("/page")
    public Response page(@RequestParam Map<String, Object> params,UsersEntity user){
        EntityWrapper<UsersEntity> ew = new EntityWrapper<UsersEntity>();
    	PageUtils page = usersService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.allLike(ew, user), params), params));
        return Response.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/list")
    public Response list( UsersEntity user){
       	EntityWrapper<UsersEntity> ew = new EntityWrapper<UsersEntity>();
      	ew.allEq(MPUtil.allEQMapPre( user, "user")); 
        return Response.ok().put("data", usersService.selectListView(ew));
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Response info(@PathVariable("id") String id){
        UsersEntity user = usersService.selectById(id);
        return Response.ok().put("data", user);
    }
    
    /**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public Response getCurrUser(HttpServletRequest request){
    	Integer id = (Integer)request.getSession().getAttribute("userId");
        UsersEntity user = usersService.selectById(id);
        return Response.ok().put("data", user);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public Response save(@RequestBody UsersEntity user){
//    	ValidatorUtils.validateEntity(user);
    	if(usersService.selectOne(new EntityWrapper<UsersEntity>().eq("username", user.getUsername())) !=null) {
    		return Response.error("用户已存在");
    	}
    	user.setPassword("123456");
        usersService.insert(user);
        return Response.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Response update(@RequestBody UsersEntity user){
//        ValidatorUtils.validateEntity(user);
        usersService.updateById(user);//全部更新
        return Response.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Response delete(@RequestBody Long[] ids){
        usersService.deleteBatchIds(Arrays.asList(ids));
        return Response.ok();
    }
}
