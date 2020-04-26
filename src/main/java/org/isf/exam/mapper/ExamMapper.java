package org.isf.exam.mapper;

import org.isf.exa.model.Exam;
import org.isf.exam.dto.ExamDTO;
import org.isf.shared.mapper.OHGenericMapper;

public class ExamMapper extends OHGenericMapper<Exam, ExamDTO> {

    public ExamMapper(Class<Exam> sourceClass, Class<ExamDTO> destClass) {
        super(sourceClass, destClass);
    }

}
