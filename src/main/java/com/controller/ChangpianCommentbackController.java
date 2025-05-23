
package com.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.*;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.Response;
import com.alibaba.fastjson.*;

/**
 * 商品评价
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/changpianCommentback")
public class ChangpianCommentbackController {
    private static final Logger logger = LoggerFactory.getLogger(ChangpianCommentbackController.class);

    @Autowired
    private ChangpianCommentbackService changpianCommentbackService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表service
    @Autowired
    private ChangpianService changpianService;
    @Autowired
    private YonghuService yonghuService;



    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public Response page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return Response.error(511,"Access is forbidden, please contact the system administrator!");
        else if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","id");
        }
        PageUtils page = changpianCommentbackService.queryPage(params);

        //字典表数据转换
        List<ChangpianCommentbackView> list =(List<ChangpianCommentbackView>)page.getList();
        for(ChangpianCommentbackView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return Response.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public Response info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        ChangpianCommentbackEntity changpianCommentback = changpianCommentbackService.selectById(id);
        if(changpianCommentback !=null){
            //entity转view
            ChangpianCommentbackView view = new ChangpianCommentbackView();
            BeanUtils.copyProperties( changpianCommentback , view );//把实体数据重构到view中

                //级联表
                ChangpianEntity changpian = changpianService.selectById(changpianCommentback.getChangpianId());
                if(changpian != null){
                    BeanUtils.copyProperties( changpian , view ,new String[]{ "id", "createTime", "insertTime", "updateTime"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setChangpianId(changpian.getId());
                }
                //级联表
                YonghuEntity yonghu = yonghuService.selectById(changpianCommentback.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createTime", "insertTime", "updateTime"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return Response.ok().put("data", view);
        }else {
            return Response.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public Response save(@RequestBody ChangpianCommentbackEntity changpianCommentback, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,changpianCommentback:{}",this.getClass().getName(),changpianCommentback.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return Response.error(511,"永远不会进入");
        else if("用户".equals(role))
            changpianCommentback.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));

        changpianCommentback.setInsertTime(new Date());
        changpianCommentback.setCreateTime(new Date());
        changpianCommentbackService.insert(changpianCommentback);
        return Response.ok();
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public Response update(@RequestBody ChangpianCommentbackEntity changpianCommentback, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,changpianCommentback:{}",this.getClass().getName(),changpianCommentback.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return Response.error(511,"永远不会进入");
//        else if("用户".equals(role))
//            changpianCommentback.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
        //根据字段查询是否有相同数据
        Wrapper<ChangpianCommentbackEntity> queryWrapper = new EntityWrapper<ChangpianCommentbackEntity>()
            .eq("id",0)
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ChangpianCommentbackEntity changpianCommentbackEntity = changpianCommentbackService.selectOne(queryWrapper);
        changpianCommentback.setUpdateTime(new Date());
        if(changpianCommentbackEntity==null){
            changpianCommentbackService.updateById(changpianCommentback);//根据id更新
            return Response.ok();
        }else {
            return Response.error(511,"表中有相同数据");
        }
    }



    /**
    * 删除
    */
    @RequestMapping("/delete")
    public Response delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        changpianCommentbackService.deleteBatchIds(Arrays.asList(ids));
        return Response.ok();
    }


    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public Response save( String fileName, HttpServletRequest request){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<ChangpianCommentbackEntity> changpianCommentbackList = new ArrayList<>();//上传的东西
            Map<String, List<String>> seachFields= new HashMap<>();//要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if(lastIndexOf == -1){
                return Response.error(511,"该文件没有后缀");
            }else{
                String suffix = fileName.substring(lastIndexOf);
                if(!".xls".equals(suffix)){
                    return Response.error(511,"只支持后缀为xls的excel文件");
                }else{
                    URL resource = this.getClass().getClassLoader().getResource("../../upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return Response.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            ChangpianCommentbackEntity changpianCommentbackEntity = new ChangpianCommentbackEntity();
//                            changpianCommentbackEntity.setChangpianId(Integer.valueOf(data.get(0)));   //商品 要改的
//                            changpianCommentbackEntity.setYonghuId(Integer.valueOf(data.get(0)));   //用户 要改的
//                            changpianCommentbackEntity.setChangpianCommentbackText(data.get(0));                    //评价内容 要改的
//                            changpianCommentbackEntity.setInsertTime(date);//时间
//                            changpianCommentbackEntity.setReplyText(data.get(0));                    //回复内容 要改的
//                            changpianCommentbackEntity.setUpdateTime(sdf.parse(data.get(0)));          //回复时间 要改的
//                            changpianCommentbackEntity.setCreateTime(date);//时间
                            changpianCommentbackList.add(changpianCommentbackEntity);


                            //把要查询是否重复的字段放入map中
                        }

                        //查询是否重复
                        changpianCommentbackService.insertBatch(changpianCommentbackList);
                        return Response.ok();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return Response.error(511,"批量插入数据异常，请联系管理员");
        }
    }





    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public Response list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = changpianCommentbackService.queryPage(params);

        //字典表数据转换
        List<ChangpianCommentbackView> list =(List<ChangpianCommentbackView>)page.getList();
        for(ChangpianCommentbackView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段
        return Response.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public Response detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        ChangpianCommentbackEntity changpianCommentback = changpianCommentbackService.selectById(id);
            if(changpianCommentback !=null){


                //entity转view
                ChangpianCommentbackView view = new ChangpianCommentbackView();
                BeanUtils.copyProperties( changpianCommentback , view );//把实体数据重构到view中

                //级联表
                    ChangpianEntity changpian = changpianService.selectById(changpianCommentback.getChangpianId());
                if(changpian != null){
                    BeanUtils.copyProperties( changpian , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setChangpianId(changpian.getId());
                }
                //级联表
                    YonghuEntity yonghu = yonghuService.selectById(changpianCommentback.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return Response.ok().put("data", view);
            }else {
                return Response.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public Response add(@RequestBody ChangpianCommentbackEntity changpianCommentback, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,changpianCommentback:{}",this.getClass().getName(),changpianCommentback.toString());
        changpianCommentback.setInsertTime(new Date());
        changpianCommentback.setCreateTime(new Date());
        changpianCommentbackService.insert(changpianCommentback);
        return Response.ok();
        }


}
