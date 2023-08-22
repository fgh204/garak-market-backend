package com.lawzone.market.batch;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lawzone.market.admin.service.GarakAdminService;
import com.lawzone.market.batch.service.BatchService;
import com.lawzone.market.common.service.CommonService;
import com.lawzone.market.user.service.UserInfoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    //private final Job job;  // tutorialJob
    //private final JobLauncher jobLauncher;
    private final BatchService batchService;
    private final GarakAdminService garakAdminService;
    private final CommonService commonService;
    private final UserInfoService userInfoService;
    
    // 5초마다 실행
//    @Scheduled(fixedDelay = 60 * 1000L)
//    public void executeJob () {
//        try {
//            jobLauncher.run(
//                    job,
//                    new JobParametersBuilder()
//                            .addString("datetime", LocalDateTime.now().toString())
//                    .toJobParameters()  // job parameter 설정
//            );
//        } catch (JobExecutionException ex) {
//            System.out.println(ex.getMessage());
//            ex.printStackTrace();
//        }
//    }
    
    //@Scheduled(cron = "0 0 15 ? * MON-FRI")
    @Scheduled(fixedDelay = 7200000)
    public void setDeliveryStaus() {
        try {
        	this.garakAdminService.getDeliveryStaus();
        } catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    @Scheduled(fixedDelay = 3600000)
    public void setsearchWord() {
        try {
        	this.userInfoService.setSearchWord();
        } catch (Exception e) {
			// TODO: handle exception
		}
    }
    
//    @Scheduled(cron = "0 0 18 ? * MON-FRI")
//    public void setDeliveryStaus2() {
//        try {
//        	this.garakAdminService.getDeliveryStaus();
//        } catch (Exception e) {
//			// TODO: handle exception
//		}
//    }
    
    @Scheduled(fixedDelay = 82800000)
    public void setTodayToken() {
        try {
        	this.commonService.setTodayToken();
        } catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    @Scheduled(fixedDelay = 43200000)
    public void setBiztalkToken() {
        try {
        	this.commonService.setBiztalkToken();
        } catch (Exception e) {
			// TODO: handle exception
		}
    }
    
//    @Scheduled(cron = "0 33 14 ? * MON-FRI")
//    public void setDeliveryStaus3() {
//        try {
//        	this.garakAdminService.getDeliveryStaus();
//        } catch (Exception e) {
//			// TODO: handle exception
//		}
//    }
    
//    @Scheduled(cron = "0 0/1 * 1/1 * ?")
//    public void setDeliveryStaus3() {
//        try {
//        	log.error("setDeliveryStaus3 start");
//        	this.garakAdminService.getDeliveryStaus();
//        	log.error("setDeliveryStaus3 end");
//        } catch (Exception e) {
//			// TODO: handle exception
//		}
//    }
    
    @Scheduled(cron = "0 30 8 ? * MON,THU")
    public void getPush() {
        try {
        	this.batchService.getPush();
        } catch (Exception e) {
			// TODO: handle exception
		}
    }
}
