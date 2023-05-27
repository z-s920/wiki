package com.zs.wikibb.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zs.wikibb.domain.Category;
import com.zs.wikibb.domain.CategoryExample;
import com.zs.wikibb.mapper.CategoryMapper;
import com.zs.wikibb.req.CategoryQueryReq;
import com.zs.wikibb.req.CategorySaveReq;
import com.zs.wikibb.resp.CategoryQueryResp;
import com.zs.wikibb.resp.PageResp;
import com.zs.wikibb.utils.CopyUtil;
import com.zs.wikibb.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryService {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private SnowFlake snowFlake;

    public List <CategoryQueryResp> all( ){

        //模糊查询

        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        //列表复制

        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        return list;

    }

    public PageResp<CategoryQueryResp> list(CategoryQueryReq categoryQueryReq){

        //模糊查询

        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        CategoryExample.Criteria criteria = categoryExample.createCriteria();//where条件
        if(!ObjectUtils.isEmpty(categoryQueryReq.getName())){
            criteria.andNameLike("%"+ categoryQueryReq.getName()+"%");
        }
        PageHelper.startPage(categoryQueryReq.getPage(), categoryQueryReq.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        PageInfo<Category> pageInfo=new PageInfo<>(categoryList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());

        //实体类转换 不想让其他人看到自己的内部实体类全部信息。
//        List<CategoryResp> respList=new ArrayList<>();
//        for (Category category : categoryList) {
//            //对象赋值
//            CategoryResp categoryResp = CopyUtil.cop y(category, CategoryResp.class);
//            respList.add(categoryResp);
//        }
//        return respList;

        //列表复制

        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);
        PageResp<CategoryQueryResp> pageResp=new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;

    }
    /*
    保存
    * */

    public void save(CategorySaveReq categorySaveReq) {
        Category category = CopyUtil.copy(categorySaveReq,Category.class);
        if (ObjectUtils.isEmpty(categorySaveReq.getId())){
            //新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }
        else {
            //更新
            categoryMapper.updateByPrimaryKey(category);
        }

    }
    /*
    * 删除
    * */
    public void delete(long id){
        categoryMapper.deleteByPrimaryKey(id);
    }
}
