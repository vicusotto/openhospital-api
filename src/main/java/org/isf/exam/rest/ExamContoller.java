package org.isf.exam.rest;

import io.swagger.annotations.*;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.exam.dto.ExamDTO;
import org.isf.exam.mapper.ExamMapper;
import org.isf.shared.controller.OHResponses;
import org.isf.shared.controller.dto.OHSuccessDTO;
import org.isf.shared.controller.dto.OHSuccessWrapperDTO;
import org.isf.shared.exceptions.*;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Api(value = "/exams", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class ExamContoller {

    private final Logger logger = LoggerFactory.getLogger(ExamContoller.class);

    protected ExamBrowsingManager examManager;

    private ExamMapper examMapper;

    @Autowired
    public ExamContoller(ExamBrowsingManager examManager, ExamMapper examMapper) {
        this.examManager = examManager;
        this.examMapper = examMapper;
    }

    @PostMapping(value = "/exams", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Insert new Exam")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Exam Created"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    @ResponseStatus(HttpStatus.CREATED)
    public OHSuccessDTO newExam(@RequestBody ExamDTO newExam) throws OHServiceException {
        logger.info("Create exams " + newExam.getDescription());
        boolean isCreated = examManager.newExam(examMapper.map2Model(newExam));
        if (!isCreated) {
            throw new OHCreateAPIException(new OHExceptionMessage(null, "Exam is not created!", OHSeverityLevel.ERROR));
        }
        return OHResponses.success();
    }

    @ApiOperation(value = "Update Exam")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Exam updated"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Exam not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    @PutMapping(value = "/exams/{code:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OHSuccessDTO updateExams(@PathVariable String code, @RequestBody ExamDTO updateExam) throws OHServiceException {
        logger.info("Update exams code:" + code);

        if (!updateExam.getCode().equals(code)) {
            throw new OHAPIException(new OHExceptionMessage(null, "Exam code mismatch", OHSeverityLevel.ERROR));
        }
        if (examManager.getExams().stream().noneMatch(e -> e.getCode().equals(code))) {
            throw new OHNotFoundApiException(new OHExceptionMessage(null, "Exam not Found!", OHSeverityLevel.WARNING));
        }
        if (!examManager.updateExam(examMapper.map2Model(updateExam))) {
            throw new OHUpdateAPIException(new OHExceptionMessage(null, "Exam is not updated!", OHSeverityLevel.ERROR));
        }

        return OHResponses.success();
    }


    @GetMapping(value = "/exams/{description:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Exams by description")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    @ResponseStatus(HttpStatus.OK)
    public OHSuccessWrapperDTO<List<ExamDTO>> getExams(@PathVariable String description) throws OHServiceException {
        logger.info("Get exam description:" + description);
        List<ExamDTO> exams = examMapper.map2DTOList(examManager.getExams(description));
        return OHResponses.success(exams);
    }

    @DeleteMapping(value = "/exams/{code:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Delete exam by code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Patient Deleted"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    @ResponseStatus(HttpStatus.OK)
    public OHSuccessDTO deleteExam(@PathVariable String code) throws OHServiceException {
        logger.info("Delete exams code:" + code);
        Optional<Exam> exam = examManager.getExams().stream().filter(e -> e.getCode().equals(code)).findFirst();
        if (!exam.isPresent()) {
            throw new OHNotFoundApiException(new OHExceptionMessage(null, "Exam not Found!", OHSeverityLevel.WARNING));
        }
        if (!examManager.deleteExam(exam.get())) {
            throw new OHDeleteAPIException(new OHExceptionMessage(null, "Exam is not deleted!", OHSeverityLevel.ERROR));
        }
        return OHResponses.success();
    }
}
