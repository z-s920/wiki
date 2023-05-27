package com.zs.wikibb.job;// package com.jiawa.wiki.job;

import com.zs.wikibb.service.DocService;
import com.zs.wikibb.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DocJob {


    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);
    @Resource
    private DocService docService;

    @Resource
    private SnowFlake snowFlake;
    /**
     * 自定义cron表达式跑批
     * 只有等上一次执行完成，下一次才会在下一个时间点执行，错过就错过
     * 每三十秒更新电子书信息，第五秒开始
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() {
        //增加日志流水号
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("更新电子书下的文档数据开始");
        long start=System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("更新电子书下的文档数据结束，耗时：{} 毫秒", System.currentTimeMillis()-start);
    }

}
