package com.lawzone.market.batch.job;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lawzone.market.admin.service.GarakAdminService;
import com.lawzone.market.batch.service.BatchService;
import com.lawzone.market.batch.tasklet.Task;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class Config {
	private final BatchService batchService;
    private final JobBuilderFactory jobBuilderFactory; // Job 빌더 생성용
    private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성용

    // JobBuilderFactory를 통해서 tutorialJob을 생성
    @Bean
    public Job tutorialJob() throws ClientProtocolException, IOException {
        return jobBuilderFactory.get("tutorialJob")
                .start(tutorialStep())  // Step 설정
                .build();
    }

    // StepBuilderFactory를 통해서 tutorialStep을 생성
    @Bean
    public Step tutorialStep() throws ClientProtocolException, IOException {
        return stepBuilderFactory.get("tutorialStep")
                .tasklet((contribution, chunkContext) -> {
                	this.batchService.getPush();
                    return RepeatStatus.FINISHED;
                }) // Tasklet 설정
                .build();
    }
}
