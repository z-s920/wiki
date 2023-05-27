package com.zs.wikibb.mapper;

import com.zs.wikibb.resp.StatisticResp;

import java.util.List;


public interface EbookSnapshotMapperCust {

    public void genSnapshot();

    List<StatisticResp> getStatistic();

    List<StatisticResp> get30Statistic();
}