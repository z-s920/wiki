package com.zs.wikibb.controller;

import com.zs.wikibb.req.DocQueryReq;
import com.zs.wikibb.req.DocSaveReq;
import com.zs.wikibb.resp.DocQueryResp;
import com.zs.wikibb.resp.CommonResp;
import com.zs.wikibb.resp.PageResp;
import com.zs.wikibb.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocController {

@Resource
private DocService docService;


    @GetMapping("/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list=docService.all(ebookId);
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq docQueryReq){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list=docService.list(docQueryReq);
        resp.setContent(list);
        return resp;
    }




    @PostMapping("/save")
    //content-type=json数据需要加上@RequerstBody才能接收到
    public CommonResp save(@Valid @RequestBody DocSaveReq docSaveReq){
        CommonResp resp = new CommonResp<>();
        docService.save(docSaveReq);
        return resp;
    }
    @DeleteMapping("/delete/{idsStr}")
    //content-type=json数据需要加上@RequerstBody才能接收到
    public CommonResp delete(@PathVariable String idsStr){
        CommonResp resp = new CommonResp<>();
        //先把前端获取到的比如123转换成集合也就是1,2,3然后在转成数组
        List<String> list=Arrays.asList(idsStr.split(","));
        docService.delete(list);
        return resp;
    }


    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id){
        CommonResp<String> resp = new CommonResp<>();
        String content=docService.findContent(id);
        resp.setContent(content);
        return resp;
    }

    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id)
    {
        CommonResp commonResp=new CommonResp();
        docService.vote(id);
        return commonResp;
    }


}
