package com.cn.xmf.job.admin.config;

import com.cn.xmf.job.admin.core.schedule.XxlJobDynamicScheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

/**
 * 初始化bean
 * @author airufei
 * @date 2018-8-23 17:30
 * <p>Title: com.xxl.job.admin.config</p>
 * <p></p>
 */
@Configuration
public class BeanConfig {

    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;
    @Value("${xmf.job.accessToken}")
    private String token;

    /*
     * quartz初始化监听器
     */
    @Bean
    public QuartzInitializerListener executorListener() {
        return new QuartzInitializerListener();
    }

    /*
     * 通过SchedulerFactoryBean获取Scheduler的实例
     */
    @Bean(name = "quartzScheduler")
    public SchedulerFactoryBean quartzScheduler() {
        SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();
        quartzScheduler.setDataSource(dataSource);
        quartzScheduler.setAutoStartup(true);
        quartzScheduler.setStartupDelay(20);
        quartzScheduler.setOverwriteExistingJobs(true);
        quartzScheduler.setApplicationContextSchedulerContextKey("applicationContextKey");
        Resource resource = new ClassPathResource("quartz.properties");
        quartzScheduler.setConfigLocation(resource);
        return quartzScheduler;
    }

    @Bean(initMethod = "init",destroyMethod = "destroy")
    public XxlJobDynamicScheduler xxlJobDynamicScheduler() {
        XxlJobDynamicScheduler xxlJobDynamicScheduler = new XxlJobDynamicScheduler();
        try {
            xxlJobDynamicScheduler.setAccessToken(token);
            xxlJobDynamicScheduler.setScheduler(quartzScheduler().getScheduler());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xxlJobDynamicScheduler;
    }
}