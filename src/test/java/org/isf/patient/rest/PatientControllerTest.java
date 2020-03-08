package org.isf.patient.rest;

import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    protected PatientBrowserManager patientManager;

    @Test
    public void getPatientByCode() throws Exception {

        Patient patient = new Patient();
        patient.setFirstName("Vito Romano");

        when(patientManager.getPatient(any(Integer.class))).thenReturn(patient);

        this.mockMvc.perform(get("/patients/1")).andDo(print()).andExpect(status().isOk());
    }


}
