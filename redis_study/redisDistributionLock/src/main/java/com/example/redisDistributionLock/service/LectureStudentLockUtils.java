package com.example.redisDistributionLock.service;

import com.example.redisDistributionLock.data.entity.LectureStudent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LectureStudentLockUtils {

    private final RedissonClient redissonClient;
    private final LectureStudentService lectureStudentService;

    public Long saveLock(Long studentId, Long lectureId,Long limitPeople){

        RLock rLock = redissonClient.getLock("lectureStudent");

        try{
            boolean successLectureStudent = rLock.tryLock(5000,1000, TimeUnit.MILLISECONDS);

            if (!successLectureStudent) {
                log.info("락 획득 실패");
                throw new IllegalStateException();
            }

            return lectureStudentService.saveLectureStudent(studentId,lectureId,limitPeople);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rLock.unlock();
        }

    }
}
