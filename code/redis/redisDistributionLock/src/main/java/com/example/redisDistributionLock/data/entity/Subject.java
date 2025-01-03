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
public class Subject {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long subjectId;
    private String name;

    public static Subject createSubject(String name){
        return Subject.builder()
                .name(name)
                .build();
    }
}
