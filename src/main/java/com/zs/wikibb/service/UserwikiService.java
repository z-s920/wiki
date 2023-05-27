package com.zs.wikibb.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zs.wikibb.domain.Userwiki;
import com.zs.wikibb.domain.UserwikiExample;
import com.zs.wikibb.exception.BusinessException;
import com.zs.wikibb.exception.BusinessExceptionCode;
import com.zs.wikibb.mapper.UserwikiMapper;
import com.zs.wikibb.req.UserwikiLoginReq;
import com.zs.wikibb.req.UserwikiQueryReq;
import com.zs.wikibb.req.UserwikiResetPasswordReq;
import com.zs.wikibb.req.UserwikiSaveReq;
import com.zs.wikibb.resp.UserwikiLoginResp;
import com.zs.wikibb.resp.UserwikiQueryResp;
import com.zs.wikibb.resp.PageResp;
import com.zs.wikibb.utils.CopyUtil;
import com.zs.wikibb.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserwikiService {
    private static final Logger LOG = LoggerFactory.getLogger(UserwikiService.class);
    @Resource
    private UserwikiMapper userwikiMapper;
    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserwikiQueryResp> list(UserwikiQueryReq userwikiQueryReq) {

        //模糊查询

        UserwikiExample userwikiExample = new UserwikiExample();
        UserwikiExample.Criteria criteria = userwikiExample.createCriteria();
        if (!ObjectUtils.isEmpty(userwikiQueryReq.getLoginName())) {
            criteria.andLoginNameEqualTo(userwikiQueryReq.getLoginName());
        }

        PageHelper.startPage(userwikiQueryReq.getPage(), userwikiQueryReq.getSize());
        List<Userwiki> userwikiList = userwikiMapper.selectByExample(userwikiExample);
        PageInfo<Userwiki> pageInfo = new PageInfo<>(userwikiList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        //实体类转换 不想让其他人看到自己的内部实体类全部信息。
//        List<UserwikiResp> respList=new ArrayList<>();
//        for (Userwiki userwiki : userwikiList) {
//            //对象赋值
//            UserwikiResp userwikiResp = CopyUtil.cop y(userwiki, UserwikiResp.class);
//            respList.add(userwikiResp);
//        }
//        return respList;

        //列表复制

        List<UserwikiQueryResp> list = CopyUtil.copyList(userwikiList, UserwikiQueryResp.class);
        PageResp<UserwikiQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;

    }
    /*
    保存
    * */

    public void save(UserwikiSaveReq userwikiSaveReq) {
        Userwiki userwiki = CopyUtil.copy(userwikiSaveReq, Userwiki.class);
        if (ObjectUtils.isEmpty(userwikiSaveReq.getId())) {

            if (ObjectUtils.isEmpty(selectByLoginName(userwikiSaveReq.getLoginName()))) {
                //新增
                userwiki.setId(snowFlake.nextId());
                userwikiMapper.insert(userwiki);
            }
            else {
                //用户名已存在
                    throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        } else {
            //更新，先设置成null，防止黑客通过前端看到我们的登录信息，updateByPrimaryKeySelective意思有值才会修改，没有值的话不起作用。
            userwiki.setPassword(null);
            userwiki.setLoginName(null);//修改的时候用户名不允许修改。
            userwikiMapper.updateByPrimaryKeySelective(userwiki);
        }

    }

    /*
     * 删除
     * */
    public void delete(long id) {
        userwikiMapper.deleteByPrimaryKey(id);
    }

    public Userwiki selectByLoginName(String LoginName) {
        UserwikiExample userwikiExample = new UserwikiExample();
        UserwikiExample.Criteria criteria = userwikiExample.createCriteria();
        criteria.andLoginNameEqualTo(LoginName);
        List<Userwiki> userwikiList = userwikiMapper.selectByExample(userwikiExample);
        if (CollectionUtils.isEmpty(userwikiList)){
            return null;
        }
        else {
            return userwikiList.get(0);
        }
    }

/*
* 修改密码
* */
    public void resetPassword(UserwikiResetPasswordReq req) {
        Userwiki userwiki = CopyUtil.copy(req, Userwiki.class);
        userwikiMapper.updateByPrimaryKeySelective(userwiki);
    }

    /*
     * 登录
     * */
    public UserwikiLoginResp login(UserwikiLoginReq req) {
        Userwiki userwiki=selectByLoginName(req.getLoginName());
        if (ObjectUtils.isEmpty(userwiki)){
            LOG.info("用户名不存在,{}",req.getLoginName());
            //用户名不存在
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        }
        else{
            if (userwiki.getPassword().equals(req.getPassword())){
                //登录成功

                UserwikiLoginResp userwikiLoginResp=CopyUtil.copy(userwiki,UserwikiLoginResp.class);
                return userwikiLoginResp;
            }
            else {
                //密码错误
                LOG.info("密码不对,输入密码：{}，数据库密码：{}",req.getPassword(),userwiki.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }
}

