package com.leopard2.batch.student;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@EnableScheduling               // 스케줄 작업 (가동)
@EnableBatchProcessing          // 배치 작업   (가동)
@Slf4j
public class StudentController {
    private final JobLauncher jobLauncher;
    private final Job importStudent;

    @PostMapping
    public void importCSVtoDBJob() {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("startAt", System.currentTimeMillis())
            .toJobParameters();
        try {
            jobLauncher.run(importStudent, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }

	
    // 매일 13시 30분 58초에 실행하도록 작업 시간 등록
    @Scheduled(cron = "58 30 13 * * *")
	public void testJob() {
        log.info("--------- TEST Cron Job --------");   
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("startAt", System.currentTimeMillis())
            .toJobParameters();     
        try {
            jobLauncher.run(importStudent, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
    
}
