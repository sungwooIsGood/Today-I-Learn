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
public class Lecture {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long lectureId;
    private Long limitPeople;
    private Long subjectId;

    public static Lecture createLecture(Long subjectId, Long limitPeople){
        return Lecture.builder()
                .subjectId(subjectId)
                .limitPeople(limitPeople)
                .build();
    }
}
