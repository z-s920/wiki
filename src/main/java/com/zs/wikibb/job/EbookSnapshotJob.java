package com.zs.wikibb.job;// package com.jiawa.wiki.job;

import com.zs.wikibb.service.EbookSnapshotService;
import com.zs.wikibb.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EbookSnapshotJob {


    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotJob.class);
    @Resource
    private EbookSnapshotService ebookSnapshotService;

    @Resource
    private SnowFlake snowFlake;
    /**
     * 自定义cron表达式跑批
     * 只有等上一次执行完成，下一次才会在下一个时间点执行，错过就错过
     * 每三十秒更新电子书信息
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void cron() {
        //增加日志流水号
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("生成今日电子书快照开始");
        long start=System.currentTimeMillis();
        ebookSnapshotService.genSnapshot();
        LOG.info("生成今日电子书快照结束，耗时：{} 毫秒", System.currentTimeMillis()-start);
    }

}
