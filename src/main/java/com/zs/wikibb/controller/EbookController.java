package com.zs.wikibb.controller;

import com.zs.wikibb.req.EbookQueryReq;
import com.zs.wikibb.req.EbookSaveReq;
import com.zs.wikibb.resp.CommonResp;
import com.zs.wikibb.resp.EbookQueryResp;
import com.zs.wikibb.resp.PageResp;
import com.zs.wikibb.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/ebook")
public class EbookController {

@Resource
private EbookService ebookService;

    @GetMapping("/list")
    //@Valid是开启校验规则
    public CommonResp list(@Valid EbookQueryReq ebookQueryReq){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list=ebookService.list(ebookQueryReq);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save")
    //content-type=json数据需要加上@RequerstBody才能接收到
    public CommonResp save(@Valid @RequestBody EbookSaveReq ebookSaveReq){
        CommonResp resp = new CommonResp<>();
        ebookService.save(ebookSaveReq);
        return resp;
    }
    @DeleteMapping("/delete/{id}")//Resful风格

    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }

}
