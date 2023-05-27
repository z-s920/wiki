package com.zs.wikibb.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zs.wikibb.domain.Content;
import com.zs.wikibb.domain.Doc;
import com.zs.wikibb.domain.DocExample;
import com.zs.wikibb.exception.BusinessException;
import com.zs.wikibb.exception.BusinessExceptionCode;
import com.zs.wikibb.mapper.ContentMapper;
import com.zs.wikibb.mapper.DocMapper;
import com.zs.wikibb.mapper.DocMapperCust;
import com.zs.wikibb.req.DocQueryReq;
import com.zs.wikibb.req.DocSaveReq;
import com.zs.wikibb.resp.DocQueryResp;
import com.zs.wikibb.resp.PageResp;
import com.zs.wikibb.utils.CopyUtil;
import com.zs.wikibb.utils.RedisUtil;
import com.zs.wikibb.utils.RequestContext;
import com.zs.wikibb.utils.SnowFlake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);
    @Resource
    private DocMapper docMapper;

    @Resource
    private DocMapperCust docMapperCust;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    public RedisUtil redisUtil;

    @Resource
    public WsService wsService;

//    @Resource
//    private RocketMQTemplate rocketMQTemplate;
    public List <DocQueryResp> all(Long ebookId){

        //模糊查询
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);//根据id查文档信息
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);
        //列表复制

        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;

    }

    public PageResp<DocQueryResp> list(DocQueryReq docQueryReq){

        //模糊查询

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();//where条件
        if(!ObjectUtils.isEmpty(docQueryReq.getName())){
            criteria.andNameLike("%"+ docQueryReq.getName()+"%");
        }
        PageHelper.startPage(docQueryReq.getPage(), docQueryReq.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);
        PageInfo<Doc> pageInfo=new PageInfo<>(docList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());

        //实体类转换 不想让其他人看到自己的内部实体类全部信息。
//        List<DocResp> respList=new ArrayList<>();
//        for (Doc doc : docList) {
//            //对象赋值
//            DocResp docResp = CopyUtil.cop y(doc, DocResp.class);
//            respList.add(docResp);
//        }
//        return respList;

        //列表复制

        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        PageResp<DocQueryResp> pageResp=new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;

    }
    /*
    保存
    * */

    @Transactional  //加入事务注解，使其doc表添加和内容表添加  要么一块成功 要么一块失败，防止只有一方成功，使数据库调用数据出现异常
                   // 同样的和异步化一样，如果在一个类中定义事务A 然后方法B调用也是不生效的，而save函数是在Controller层调用 所以在此层加注解生效
    public void save(DocSaveReq docSaveReq) {
        Doc doc = CopyUtil.copy(docSaveReq,Doc.class);
        Content content = CopyUtil.copy(docSaveReq,Content.class);//content:表名
        if (ObjectUtils.isEmpty(docSaveReq.getId())){
            //新增
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            //content.setId(snowFlake.nextId());为什么不这么写是因为 setid会获取两遍每次id不一样
            content.setId(doc.getId());
            contentMapper.insert(content);
        }
        else {
            //更新
            docMapper.updateByPrimaryKey(doc);
            int count=contentMapper.updateByPrimaryKeyWithBLOBs(content);//blob代表富文本字段
            if (count==0)
            {
                contentMapper.insert(content);
            }
        }

    }
    /*
    * 删除
    * */
    public void delete(long id){
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids){

        DocExample docExample = new DocExample();

        DocExample.Criteria criteria = docExample.createCriteria();//where条件

        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);//根据某一个条件删除
    }


    public String findContent(long id){
        Content content=contentMapper.selectByPrimaryKey(id);
        //文档阅读数+1
        docMapperCust.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content))
        {
            return "";
        }
        else {
            return content.getContent();
        }
    }

    /*点赞*/
    public void vote(Long id)
    {
//        docMapperCust.increaseVoteCount(id);
        //远程IP+doc.id作为key，24小时内不能重复
        String ip= RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" +ip,3600*24))
        {
            docMapperCust.increaseVoteCount(id);
        }
        else
        {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
            //推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        String  logId = MDC.get("LOG_ID");
        wsService.sendInfo("【"+docDb.getName()+"】被点赞",logId);
        //rocketMQTemplate.convertAndSend("VOTE_TOPIC","【"+docDb.getName()+"】被点赞!");
    }



    public void  updateEbookInfo(){
        docMapperCust.updateEbookInfo();
    };
}
