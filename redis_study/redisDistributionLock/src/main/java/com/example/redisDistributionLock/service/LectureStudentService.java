package com.example.redisDistributionLock.service;

import com.example.redisDistributionLock.data.entity.Lecture;
import com.example.redisDistributionLock.data.entity.LectureStudent;
import com.example.redisDistributionLock.data.entity.Student;
import com.example.redisDistributionLock.repository.LectureRepository;
import com.example.redisDistributionLock.repository.LectureStudentRepository;
import com.example.redisDistributionLock.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureStudentService {

    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;
    private final LectureStudentRepository lectureStudentRepository;

    public void saveLectureStudent(Long studentId, Long lectureId,Long limitPeople){

        // 수강 신청 인원 검증
        isExceedThNumberOfStudent(limitPeople,lectureId);

        LectureStudent lectureStudent = LectureStudent.createLectureStudent(studentId, lectureId);
        lectureStudentRepository.save(lectureStudent);
    }

    private void isExceedThNumberOfStudent(Long limitPeople,Long lectureId) {

        int lectureStudentCount = lectureStudentRepository.findByLectureId(lectureId).size();
        log.info("현재 수강 신청 인원: {}",lectureStudentCount);

        if(limitPeople < lectureStudentCount + 1){
            throw new IllegalStateException("수강 신청 인원이 모두 꽉 찾습니다.");
        }

    }
}
