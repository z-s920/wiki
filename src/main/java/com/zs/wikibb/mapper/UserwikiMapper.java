package com.zs.wikibb.mapper;

import com.zs.wikibb.domain.Userwiki;
import com.zs.wikibb.domain.UserwikiExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserwikiMapper {
    long countByExample(UserwikiExample example);

    int deleteByExample(UserwikiExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Userwiki record);

    int insertSelective(Userwiki record);

    List<Userwiki> selectByExample(UserwikiExample example);

    Userwiki selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Userwiki record, @Param("example") UserwikiExample example);

    int updateByExample(@Param("record") Userwiki record, @Param("example") UserwikiExample example);

    int updateByPrimaryKeySelective(Userwiki record);

    int updateByPrimaryKey(Userwiki record);
}