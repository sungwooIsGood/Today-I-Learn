package com.example.redisDistributionLock.repository;

import com.example.redisDistributionLock.data.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture,Long> {
}
