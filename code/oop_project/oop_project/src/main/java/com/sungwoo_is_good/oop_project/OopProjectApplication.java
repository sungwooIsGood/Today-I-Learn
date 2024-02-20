package com.sungwoo_is_good.oop_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OopProjectApplication {

    public static void main(String[] args) {

        HighCouplingClass highCouplingClass = new HighCouplingClass();
        AnotherHighCouplingClass anotherHighCouplingClass = new AnotherHighCouplingClass(highCouplingClass.data);
    }

}

class HighCouplingClass {

    int data = 10;
}

class AnotherHighCouplingClass{

    int data = 10;

    public AnotherHighCouplingClass(int data) {
        this.data = data;
    }
}