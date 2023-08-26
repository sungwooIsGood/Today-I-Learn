package com.example.redisDistributionLock.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class LectureStudent {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long lectureStudentId;
    private Long studentId;
    private Long lectureId;

    public static LectureStudent createLectureStudent(Long studentId, Long lectureId){
        return LectureStudent.builder()
                .studentId(studentId)
                .lectureId(lectureId)
                .build();
    }
}
