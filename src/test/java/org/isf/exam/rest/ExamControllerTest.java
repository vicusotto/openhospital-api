package org.isf.exam.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.isf.exa.manager.ExamBrowsingManager;
import org.isf.exa.model.Exam;
import org.isf.exam.dto.ExamDTO;
import org.isf.exam.mapper.ExamMapper;
import org.isf.examtype.dto.ExamTypeDTO;
import org.isf.exatype.model.ExamType;
import org.isf.shared.controller.dto.OHFailureDTO;
import org.isf.shared.controller.dto.OHResponseCode;
import org.isf.shared.controller.dto.OHSuccessDTO;
import org.isf.shared.controller.dto.OHSuccessWrapperDTO;
import org.isf.shared.exceptions.*;
import org.isf.utils.exception.OHServiceException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExamControllerTest {

    @Mock
    private ExamBrowsingManager examBrowsingManager;

    private MockMvc mockMvc;

    ExamMapper examMapper = new ExamMapper(Exam.class, ExamDTO.class);
    ModelMapper modelMapper = new ModelMapper();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new ExamContoller(examBrowsingManager, examMapper))
                .setControllerAdvice(new OHResponseEntityExceptionHandler())
                .build();
        ReflectionTestUtils.setField(examMapper, "modelMapper", modelMapper);
    }


    @Test
    public void when_post_exam_success_then_create() throws Exception {

        OHSuccessDTO successDTO = new OHSuccessDTO();
        ExamDTO exam = new ExamDTO("EXa_DESC", "EXA_DESC", 1, "", new ExamTypeDTO("AA", "AA Desc"), 0);

        when(examBrowsingManager.newExam(any())).thenReturn(true);
        this.mockMvc.perform(post("/exams").content(asJsonString(exam)).contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isCreated()).andExpect(content().json(asJsonString(successDTO)));
    }

    @Test
    @WithMockUser(value = "admin", password = "admin", authorities = {"admin"})
    public void when_post_exam_create_then_ohServiceException_internalServerError() throws Exception {

        OHFailureDTO failureDTO = new OHFailureDTO(OHResponseCode.INTERNAL_SERVER_ERROR, OHResponseCode.INTERNAL_SERVER_ERROR.getValue());
        ExamDTO exam = new ExamDTO("EXa_DESC", "EXA_DESC", 1, "", new ExamTypeDTO("AA", "AA Desc"), 0);

        when(examBrowsingManager.newExam(any())).thenThrow(OHServiceException.class);
        MvcResult result = this.mockMvc.perform(post("/exams").content(asJsonString(exam)).contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().json(asJsonString(failureDTO))).andReturn();
        Optional<OHServiceException> ohServiceException = Optional.ofNullable((OHServiceException) result.getResolvedException());

        ohServiceException.ifPresent((se) -> assertThat(se, notNullValue()));
        ohServiceException.ifPresent((se) -> assertThat(se, instanceOf(OHServiceException.class)));
    }

    @Test
    @WithMockUser(value = "admin", password = "admin", authorities = {"admin"})
    public void when_post_exam_create_then_ohCreateException_internalServerError() throws Exception {

        OHFailureDTO failureDTO = new OHFailureDTO(OHResponseCode.INTERNAL_SERVER_ERROR, OHResponseCode.INTERNAL_SERVER_ERROR.getValue());
        ExamDTO exam = new ExamDTO("EXa_DESC", "EXA_DESC", 1, "", new ExamTypeDTO("AA", "AA Desc"), 0);

        when(examBrowsingManager.newExam(any())).thenReturn(false);
        MvcResult result = this.mockMvc.perform(post("/exams").content(asJsonString(exam)).contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().json(asJsonString(failureDTO))).andReturn();
        Optional<OHCreateAPIException> ohException = Optional.ofNullable((OHCreateAPIException) result.getResolvedException());

        ohException.ifPresent((se) -> assertThat(se, notNullValue()));
        ohException.ifPresent((se) -> assertThat(se, instanceOf(OHCreateAPIException.class)));
    }

    @Test
    @WithMockUser(value = "admin", password = "admin", authorities = {"admin"})
    public void when_put_exam_update_then_OK() throws Exception {

        OHSuccessDTO successDTO = new OHSuccessDTO();
        ExamDTO exam = new ExamDTO("1", "EXA_DESC", 1, "", new ExamTypeDTO("AA", "AA Desc"), 0);

        ArrayList<Exam> examList = new ArrayList<>();
        Exam exam1 = new Exam("1", "EXA_DESC", new ExamType("AA", "AA Desc"), 1, "");
        examList.add(exam1);

        when(examBrowsingManager.getExams()).thenReturn(examList);
        when(examBrowsingManager.updateExam(any(Exam.class))).thenReturn(true);
        this.mockMvc.perform(put("/exams/{code}", 1).content(asJsonString(exam)).contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk()).andExpect(content().json(asJsonString(successDTO)));
    }

    @Test
    @WithMockUser(value = "admin", password = "admin", authorities = {"admin"})
    public void when_put_exam_update_then_notFoundException_and_NotFound() throws Exception {

        OHFailureDTO failureDTO = new OHFailureDTO(OHResponseCode.NOT_FOUND, "Exam not Found!");
        ExamDTO exam = new ExamDTO("2", "EXA_DESC", 1, "", new ExamTypeDTO("AA", "AA Desc"), 0);

        ArrayList<Exam> examList = new ArrayList<>();
        Exam exam1 = new Exam("1", "EXA_DESC", new ExamType("AA", "AA Desc"), 1, "");
        examList.add(exam1);

        when(examBrowsingManager.getExams()).thenReturn(examList);
        MvcResult result = this.mockMvc.perform(put("/exams/{code}", 2).content(asJsonString(exam)).contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound()).andExpect(content().json(asJsonString(failureDTO))).andReturn();
        Optional<OHNotFoundApiException> ohException = Optional.ofNullable((OHNotFoundApiException) result.getResolvedException());

        ohException.ifPresent((se) -> assertThat(se, notNullValue()));
        ohException.ifPresent((se) -> assertThat(se, instanceOf(OHNotFoundApiException.class)));
    }

    @Test
    @WithMockUser(value = "admin", password = "admin", authorities = {"admin"})
    public void when_put_exam_update_then_ohUpdateException_and_internalServerError() throws Exception {

        OHFailureDTO failureDTO = new OHFailureDTO(OHResponseCode.INTERNAL_SERVER_ERROR, OHResponseCode.INTERNAL_SERVER_ERROR.getValue());
        ExamDTO exam = new ExamDTO("1", "EXA_DESC", 1, "", new ExamTypeDTO("AA", "AA Desc"), 0);

        ArrayList<Exam> examList = new ArrayList<>();
        Exam exam1 = new Exam("1", "EXA_DESC", new ExamType("AA", "AA Desc"), 1, "");
        examList.add(exam1);

        when(examBrowsingManager.getExams()).thenReturn(examList);
        when(examBrowsingManager.updateExam(any(Exam.class))).thenReturn(false);
        MvcResult result = this.mockMvc.perform(put("/exams/{code}", 1).content(asJsonString(exam)).contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().json(asJsonString(failureDTO))).andReturn();
        Optional<OHUpdateAPIException> ohException = Optional.ofNullable((OHUpdateAPIException) result.getResolvedException());

        ohException.ifPresent((se) -> assertThat(se, notNullValue()));
        ohException.ifPresent((se) -> assertThat(se, instanceOf(OHUpdateAPIException.class)));
    }

    @Test
    @WithMockUser(value = "admin", password = "admin", authorities = {"admin"})
    public void when_put_exam_update_then_ohServiceException_internalServerError() throws Exception {

        OHFailureDTO failureDTO = new OHFailureDTO(OHResponseCode.INTERNAL_SERVER_ERROR, OHResponseCode.INTERNAL_SERVER_ERROR.getValue());
        ExamDTO exam = new ExamDTO("2", "EXA_DESC", 1, "", new ExamTypeDTO("AA", "AA Desc"), 0);

        ArrayList<Exam> examList = new ArrayList<>();
        Exam exam1 = new Exam("2", "EXA_DESC", new ExamType("AA", "AA Desc"), 1, "");
        examList.add(exam1);

        when(examBrowsingManager.getExams()).thenReturn(examList);
        when(examBrowsingManager.updateExam(any(Exam.class))).thenThrow(OHServiceException.class);
        MvcResult result = this.mockMvc.perform(put("/exams/{code}", 2).content(asJsonString(exam)).contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().json(asJsonString(failureDTO))).andReturn();
        Optional<OHServiceException> ohServiceException = Optional.ofNullable((OHServiceException) result.getResolvedException());

        ohServiceException.ifPresent((se) -> assertThat(se, notNullValue()));
        ohServiceException.ifPresent((se) -> assertThat(se, instanceOf(OHServiceException.class)));
    }

    @Test
    @WithMockUser(value = "admin", password = "admin", authorities = {"admin"})
    public void when_get_exam_by_description_then_OK() throws Exception {

        ExamDTO examDTO1 = new ExamDTO("1", "EXA_DESC", 1, "", new ExamTypeDTO("AA", "AA Desc"), null);
        ExamDTO examDTO2 = new ExamDTO("2", "EXA_DESC", 1, "", new ExamTypeDTO("BB", "BB Desc"), null);
        OHSuccessWrapperDTO<List<ExamDTO>> successDTO = new OHSuccessWrapperDTO<List<ExamDTO>>(Arrays.asList(examDTO1, examDTO2));

        ArrayList<Exam> examList = new ArrayList<>();
        Exam exam1 = new Exam("1", "EXA_DESC", new ExamType("AA", "AA Desc"), 1, "");
        Exam exam2 = new Exam("2", "EXA_DESC", new ExamType("BB", "BB Desc"), 1, "");
        examList.add(exam1);
        examList.add(exam2);

        when(examBrowsingManager.getExams(any())).thenReturn(examList);
        this.mockMvc.perform(get("/exams/{description}", "mockdescription")).andDo(print()).andExpect(status().isOk()).andExpect(content().json(asJsonString(successDTO)));
    }

    @Test
    @WithMockUser(value = "admin", password = "admin", authorities = {"admin"})
    public void when_get_exam_by_description_then_ohServiceExceptionThen_internalServerError() throws Exception {

        OHFailureDTO failureDTO = new OHFailureDTO(OHResponseCode.INTERNAL_SERVER_ERROR, OHResponseCode.INTERNAL_SERVER_ERROR.getValue());

        ArrayList<Exam> examList = new ArrayList<>();
        Exam exam1 = new Exam("1", "EXA_DESC", new ExamType("AA", "AA Desc"), 1, "");
        Exam exam2 = new Exam("2", "EXA_DESC", new ExamType("BB", "BB Desc"), 1, "");
        examList.add(exam1);
        examList.add(exam2);

        when(examBrowsingManager.getExams(any())).thenThrow(OHServiceException.class);
        MvcResult result = this.mockMvc.perform(get("/exams/{description}", "mockdescription")).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().json(asJsonString(failureDTO))).andReturn();
        Optional<OHServiceException> ohServiceException = Optional.ofNullable((OHServiceException) result.getResolvedException());

        ohServiceException.ifPresent((se) -> assertThat(se, notNullValue()));
        ohServiceException.ifPresent((se) -> assertThat(se, instanceOf(OHServiceException.class)));
    }

    @Test
    @WithMockUser(value = "admin", password = "admin", authorities = {"admin"})
    public void when_delete_exam_then_OK() throws Exception {

        OHSuccessDTO successDTO = new OHSuccessDTO();
        ArrayList<Exam> examList = new ArrayList<>();
        Exam exam1 = new Exam("1", "EXA_DESC", new ExamType("AA", "AA Desc"), 1, "");
        examList.add(exam1);

        when(examBrowsingManager.getExams()).thenReturn(examList);
        when(examBrowsingManager.deleteExam(any())).thenReturn(true);
        this.mockMvc.perform(delete("/exams/{code}", 1)).andDo(print()).andExpect(status().isOk()).andExpect(content().json(asJsonString(successDTO)));
    }

    @Test
    @WithMockUser(value = "admin", password = "admin", authorities = {"admin"})
    public void when_delete_exam_then_ohServiceException_internalServerError() throws Exception {

        OHFailureDTO failureDTO = new OHFailureDTO(OHResponseCode.INTERNAL_SERVER_ERROR, OHResponseCode.INTERNAL_SERVER_ERROR.getValue());
        ArrayList<Exam> examList = new ArrayList<>();
        Exam exam1 = new Exam("1", "EXA_DESC", new ExamType("AA", "AA Desc"), 1, "");
        examList.add(exam1);

        when(examBrowsingManager.getExams()).thenReturn(examList);
        when(examBrowsingManager.deleteExam(any())).thenThrow(OHServiceException.class);
        MvcResult result = this.mockMvc.perform(delete("/exams/{code}", 1)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().json(asJsonString(failureDTO))).andReturn();
        Optional<OHServiceException> ohServiceException = Optional.ofNullable((OHServiceException) result.getResolvedException());

        ohServiceException.ifPresent((se) -> assertThat(se, notNullValue()));
        ohServiceException.ifPresent((se) -> assertThat(se, instanceOf(OHServiceException.class)));
    }

    @Test
    @WithMockUser(value = "admin", password = "admin", authorities = {"admin"})
    public void when_delete_exam_then_ohDeleteException_internalServerError() throws Exception {

        OHFailureDTO failureDTO = new OHFailureDTO(OHResponseCode.INTERNAL_SERVER_ERROR, OHResponseCode.INTERNAL_SERVER_ERROR.getValue());
        ArrayList<Exam> examList = new ArrayList<>();
        Exam exam1 = new Exam("1", "EXA_DESC", new ExamType("AA", "AA Desc"), 1, "");
        examList.add(exam1);

        when(examBrowsingManager.getExams()).thenReturn(examList);
        when(examBrowsingManager.deleteExam(any())).thenReturn(false);
        MvcResult result = this.mockMvc.perform(delete("/exams/{code}", 1)).andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().json(asJsonString(failureDTO))).andReturn();
        Optional<OHDeleteAPIException> ohException = Optional.ofNullable((OHDeleteAPIException) result.getResolvedException());

        ohException.ifPresent((se) -> assertThat(se, notNullValue()));
        ohException.ifPresent((se) -> assertThat(se, instanceOf(OHDeleteAPIException.class)));
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}