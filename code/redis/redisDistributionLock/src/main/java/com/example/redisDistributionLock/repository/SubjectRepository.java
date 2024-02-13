package com.example.redisDistributionLock.repository;

import com.example.redisDistributionLock.data.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject,Long> {
}
