package com.zs.wikibb.controller;

import com.zs.wikibb.req.CategoryQueryReq;
import com.zs.wikibb.req.CategorySaveReq;
import com.zs.wikibb.resp.CategoryQueryResp;
import com.zs.wikibb.resp.CommonResp;
import com.zs.wikibb.resp.PageResp;
import com.zs.wikibb.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

@Resource
private CategoryService categoryService;


    @GetMapping("/all")
    public CommonResp all(){
        CommonResp<List<CategoryQueryResp>> resp = new CommonResp<>();
        List<CategoryQueryResp> list=categoryService.all();
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq categoryQueryReq){
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list=categoryService.list(categoryQueryReq);
        resp.setContent(list);
        return resp;
    }




    @PostMapping("/save")
    //content-type=json数据需要加上@RequerstBody才能接收到
    public CommonResp save(@Valid @RequestBody CategorySaveReq categorySaveReq){
        CommonResp resp = new CommonResp<>();
        categoryService.save(categorySaveReq);
        return resp;
    }
    @DeleteMapping("/delete/{id}")
    //content-type=json数据需要加上@RequerstBody才能接收到
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }

}
