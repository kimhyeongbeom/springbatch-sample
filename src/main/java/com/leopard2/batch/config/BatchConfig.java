package com.leopard2.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.leopard2.batch.student.Student;
import com.leopard2.batch.student.StudentProcessor;
import com.leopard2.batch.student.StudentRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final StudentRepository studentRepository;

    /**
     * importStudent 작업 생성
     * 내부에는 작업단계 (step)이 하나로 되어 있음.
     *  => importStep
     * @return 생성된 작업 return
     */
    @Bean
    public Job runJob() {
        return new JobBuilder("importStudent", jobRepository)
        .start(importStep())
        .build();
    }


    /**
     * csvImport스텝으로 
     * chunkSize단위로 일을 처리한다.
     * itemReader, processor, writer 를 단계적으로 처리한다.
     * @return
     */
    @Bean
    public Step importStep() {
        return new StepBuilder("csvImport", jobRepository)
            .<Student, Student>chunk(1000, platformTransactionManager)  // 1000개 단위 처리
            .reader(itemReader())    // 파일에서 정보를 읽기
            .processor(processor())  // 처리
            .writer(writer())        // 처리된 정보를 저장
            .taskExecutor(taskExecutor()) // 
            .build();
    }

    /**
     * file에서 한줄씩 읽어 Student객체로 변환하여 반환한다.
     * 1줄은 제목줄이라 skip처리
     * 2번째줄 부터 lineMapper함수를 이용해 처리한다.
     * @return
     */
    @Bean
    public FlatFileItemReader<Student> itemReader() {
        FlatFileItemReader<Student> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/students.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    /**
     * 구분자 콤마(,)로 구분하여 순서대로 지정된 이름으로 Student 객체로 변환할 수 있도록
     * mapper를 만들어 준다.
     * @return
     */
    private LineMapper<Student> lineMapper() {
        DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","firstname", "lastname", "age");

        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Student.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    /** 
     * Reader에서 읽은 객체정보를 가져다 처리하고 결과로 처리된 객체를 반환한다.
     */
    @Bean
    public StudentProcessor processor() {
        return new StudentProcessor();
    }

    /**
     * 앞전에서 처리한 student정보를 가져와 studentRepository의 save함수를 호출하여 처리한다. 
     * @return
     */
    @Bean
    public RepositoryItemWriter<Student> writer() {
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(studentRepository);
        writer.setMethodName("save");
        return writer;
    }

    // 10개 프로세스 병렬 처리
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }
}
