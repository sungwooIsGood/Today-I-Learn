package com.study.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@SpringBootApplication
public class SchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerApplication.class, args);
	}
}

@Slf4j
@Service
class SchedulerService{

	String mainThread = Thread.currentThread().getName();

	@Async
	@Scheduled(cron = "0/1 * * * * *")
	public void run1() throws InterruptedException {
		String schedulerThreadName = Thread.currentThread().getName();
		log.info("main Thread name: {}, scheduler Thread name: {} run1에서 10초 마다 실행 합니다.",mainThread,schedulerThreadName);
		Thread.sleep(100000);
	}

	@Async
	@Scheduled(cron = "0/10 * * * * *")
	public void run2(){
		String schedulerThreadName = Thread.currentThread().getName();
		log.info("main Thread name: {}, scheduler Thread name: {} run2에서 10초 마다 실행 합니다.",mainThread,schedulerThreadName);
	}

	@Async
	@Scheduled(cron = "0/10 * * * * *")
	public void run3(){
		String schedulerThreadName = Thread.currentThread().getName();
		log.info("main Thread name: {}, scheduler Thread name: {} run3에서 10초 마다 실행 합니다.",mainThread,schedulerThreadName);
	}
}
