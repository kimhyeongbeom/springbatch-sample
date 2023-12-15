package com.leopard2.batch.student;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * <input객체, output객체>
 */
public class StudentProcessor implements ItemProcessor<Student, Student>{

    /**
     * 입력받은 객체 student의 id는 중복이 있어서 지워서 return (출력용 객체)
     */
    @Override
    @Nullable
    public Student process(@NonNull Student student) throws Exception {
        student.setId(null); // student의 id를 null처리하여 table에 등록시 자동 번호생성되도록 함.
        return student;
    }
    
}
