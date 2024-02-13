package com.example.redisDistributionLock.repository;

import com.example.redisDistributionLock.data.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Long> {
}
