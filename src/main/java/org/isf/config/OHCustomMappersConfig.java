package org.isf.config;

import org.isf.exa.model.Exam;
import org.isf.exam.dto.ExamDTO;
import org.isf.exam.mapper.ExamMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OHCustomMappersConfig {

    @Bean
    public ExamMapper getExamMapper() {
        return new ExamMapper(Exam.class, ExamDTO.class);
    }

}
