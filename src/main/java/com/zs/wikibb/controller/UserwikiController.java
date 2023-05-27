package com.zs.wikibb.controller;

import com.alibaba.fastjson.JSONObject;
import com.zs.wikibb.req.UserwikiLoginReq;
import com.zs.wikibb.req.UserwikiQueryReq;
import com.zs.wikibb.req.UserwikiResetPasswordReq;
import com.zs.wikibb.req.UserwikiSaveReq;
import com.zs.wikibb.resp.CommonResp;
import com.zs.wikibb.resp.PageResp;
import com.zs.wikibb.resp.UserwikiLoginResp;
import com.zs.wikibb.resp.UserwikiQueryResp;
import com.zs.wikibb.service.UserwikiService;
import com.zs.wikibb.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user_wiki")
public class UserwikiController {
    //生成后端日志
    private static final Logger LOG = LoggerFactory.getLogger(UserwikiController.class);
    @Resource
    private UserwikiService userwikiService;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    public CommonResp list(@Valid UserwikiQueryReq userwikiQueryReq){
        CommonResp<PageResp<UserwikiQueryResp>> resp = new CommonResp<>();
        PageResp<UserwikiQueryResp> list=userwikiService.list(userwikiQueryReq);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save")
    //content-type=json数据需要加上@RequerstBody才能接收到
    public CommonResp save(@Valid @RequestBody UserwikiSaveReq userwikiSaveReq){
       //密码加密
        userwikiSaveReq.setPassword(DigestUtils.md5DigestAsHex(userwikiSaveReq.getPassword().getBytes()));

        CommonResp resp = new CommonResp<>();
        userwikiService.save(userwikiSaveReq);
        return resp;
    }
    @DeleteMapping("/delete/{id}")
    //content-type=json数据需要加上@RequerstBody才能接收到
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        userwikiService.delete(id);
        return resp;
    }

    @PostMapping("/reset-passwprd")
    //content-type=json数据需要加上@RequerstBody才能接收到
    public CommonResp resetPasswprd(@Valid @RequestBody UserwikiResetPasswordReq userwikiResetPasswordReq)
    {
        //密码加密
        userwikiResetPasswordReq.setPassword(DigestUtils.md5DigestAsHex(userwikiResetPasswordReq.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userwikiService.resetPassword(userwikiResetPasswordReq);
        return resp;
    }


    @PostMapping("/login")
    //content-type=json数据需要加上@RequerstBody才能接收到
    public CommonResp login(@Valid @RequestBody UserwikiLoginReq req)
    {
        //密码加密
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp <UserwikiLoginResp> resp = new CommonResp<>();
        UserwikiLoginResp userwikiLoginResp= userwikiService.login(req);

        //生成单点登录token，并放入redis中
        Long token=snowFlake.nextId();
        LOG.info("生成单点登录token，并放入redis中",token);
        userwikiLoginResp.setToken(token.toString());//将token也返还给前端，userwikiLoginResp需要序列化
        redisTemplate.opsForValue().set(token.toString(), JSONObject.toJSONString(userwikiLoginResp),3600*24, TimeUnit.SECONDS);

        resp.setContent(userwikiLoginResp);
        return resp;
    }
    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable String token) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(token);
        LOG.info("从redis中删除token: {}", token);
        return resp;
    }

}
