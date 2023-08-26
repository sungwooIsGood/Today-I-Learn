package com.example.redisDistributionLock.service;

import com.example.redisDistributionLock.data.entity.Lecture;
import com.example.redisDistributionLock.data.entity.LectureStudent;
import com.example.redisDistributionLock.data.entity.Student;
import com.example.redisDistributionLock.data.entity.Subject;
import com.example.redisDistributionLock.repository.LectureRepository;
import com.example.redisDistributionLock.repository.StudentRepository;
import com.example.redisDistributionLock.repository.SubjectRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class LectureStudentServiceTest {

    @Autowired
    private LectureStudentService lectureStudentService;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private LectureRepository lectureRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("수강신청 테스트 동시성 이슈 해결x")
    void 수간신청_테스트() throws InterruptedException {

        // given
        ExecutorService executorService = Executors.newFixedThreadPool(100); // 스레드 100개 생성
        CountDownLatch countDownLatch = new CountDownLatch(100);

        Long subjectId = subjectRepository.save(Subject.createSubject("레디스 기초"))
                .getSubjectId();
        Long lectureId = lectureRepository.save(Lecture.createLecture(subjectId, 11L))
                .getLectureId();

        List<Student> studentList = saveManyStudent();
        List<Long> lectureStudentList = new ArrayList<>();

        // when
        studentList.forEach(student -> {
            executorService.submit(() ->{
                try{
                    Long lectureStudentId = lectureStudentService
                            .saveLectureStudent(student.getStudentId(), lectureId, 11L);
                    lectureStudentList.add(lectureStudentId);
                } finally {
                    countDownLatch.countDown();
                }
            });
        });
        countDownLatch.await();
        Assertions.assertThat(lectureStudentList.size()).isEqualTo(11);
    }

    private List<Student> saveManyStudent() {
        List<Student> studentsList = new ArrayList<>();

        for(int i = 1; i < 101; i++){
            Student student = Student.createStudent("학생" + i);
            studentsList.add(student);
        }
        return studentRepository.saveAll(studentsList);
    }

}