package com.leopard2.batch.config;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.leopard2.batch.student.Student;

public class StudentProcessor implements ItemProcessor<Student, Student>{

    @Override
    @Nullable
    public Student process(@NonNull Student student) throws Exception {
        student.setId(null); // student의 id를 null처리하여 table에 등록시 자동 번호생성되도록 함.
        return student;
    }
    
}
