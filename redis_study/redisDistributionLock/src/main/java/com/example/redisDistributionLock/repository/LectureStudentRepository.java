package com.example.redisDistributionLock.repository;

import com.example.redisDistributionLock.data.entity.LectureStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureStudentRepository extends JpaRepository<LectureStudent, Long> {

    List<LectureStudent> findByLectureId(Long lectureId);
}
