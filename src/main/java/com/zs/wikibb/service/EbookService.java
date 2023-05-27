package com.zs.wikibb.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zs.wikibb.domain.Ebook;
import com.zs.wikibb.domain.EbookExample;
import com.zs.wikibb.mapper.EbookMapper;
import com.zs.wikibb.req.EbookQueryReq;
import com.zs.wikibb.req.EbookSaveReq;
import com.zs.wikibb.resp.EbookQueryResp;
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
public class EbookService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);
    @Resource
    private EbookMapper ebookMapper;
    @Resource
    private SnowFlake snowFlake;
    public PageResp<EbookQueryResp> list(EbookQueryReq ebookQueryReq){

        //模糊查询

        EbookExample ebookExample = new EbookExample();
        //类似where条件

        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(ebookQueryReq.getName())){
            criteria.andNameLike("%"+ ebookQueryReq.getName()+"%");
        }
        if(!ObjectUtils.isEmpty(ebookQueryReq.getCategoryId2())){
            criteria.andCategory2IdEqualTo(ebookQueryReq.getCategoryId2());
        }
        PageHelper.startPage(ebookQueryReq.getPage(), ebookQueryReq.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        PageInfo<Ebook> pageInfo=new PageInfo<>(ebookList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());

        //实体类转换 不想让其他人看到自己的内部实体类全部信息。Controller不要出现实体类

//        list赋值，需要遍历
//        List<EbookResp> respList=new ArrayList<>();
//        for (Ebook ebook : ebookList) {
//            //对象赋值
//                把ebook实体源中的值赋值到EbookResp类中
//            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
//            respList.add(ebookResp);
//        }
//        return respList;

        //列表复制

        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp=new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;

    }
    /*
    保存
    * */

    public void save(EbookSaveReq ebookSaveReq) {
        Ebook ebook = CopyUtil.copy(ebookSaveReq,Ebook.class);
        if (ObjectUtils.isEmpty(ebookSaveReq.getId())){
            //新增
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        }
        else {
            //更新
            ebookMapper.updateByPrimaryKey(ebook);
        }

    }
    /*
    * 删除
    * */
    public void delete(long id){
        ebookMapper.deleteByPrimaryKey(id);
    }
}
