package org.isf.patient.rest;

import io.swagger.annotations.*;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.controller.OHResponse;
import org.isf.shared.controller.dto.OHSuccessDTO;
import org.isf.shared.controller.dto.OHSuccessWrapperDTO;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@Api(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class PatientController {

    private static final String DEFAULT_PAGE_SIZE = "80";

    @Autowired
    protected PatientBrowserManager patientManager;

    @Autowired
    private ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(PatientController.class);

    /**
     * Create new Patient
     *
     * @param newPatient
     * @return
     * @throws OHServiceException
     */
    @PostMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Insert new Patient")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Succes, Patient created"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    @ResponseStatus(HttpStatus.CREATED)
    public OHSuccessDTO newPatient(@RequestBody PatientDTO newPatient) throws OHServiceException {
        String name = StringUtils.isEmpty(newPatient.getName()) ? newPatient.getFirstName() + " " + newPatient.getSecondName() : newPatient.getName();
        logger.info("Create patient " + name);
        boolean isCreated = patientManager.newPatient(modelMapper.map(newPatient, Patient.class));
        Patient patient = patientManager.getPatient(name);
        if (!isCreated || patient == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not created!", OHSeverityLevel.ERROR));
        }
        return OHResponse.success();
    }

    @ApiOperation(value = "Update Patient")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Succes, Patient updated"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    @PutMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OHSuccessDTO updatePatient(@PathVariable int code, @RequestBody PatientDTO updatePatient) throws OHServiceException {
        logger.info("Update patient code:" + code);
        Patient patient = modelMapper.map(updatePatient, Patient.class);
        patient.setCode(code);
        boolean isUpdated = patientManager.updatePatient(patient);
        if (!isUpdated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not updated!", OHSeverityLevel.ERROR));
        }
        return OHResponse.success();
    }

    @GetMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Patiens")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Succes"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    @ResponseStatus(HttpStatus.OK)
    public OHSuccessWrapperDTO<List<PatientDTO>> getPatients(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) throws OHServiceException {
        logger.info("Get patients page:" + page + " size:" + size);
        ArrayList<Patient> patients = patientManager.getPatient(page, size);
        List<PatientDTO> patientDTOS = patients.stream().map(it -> modelMapper.map(it, PatientDTO.class)).collect(Collectors.toList());
        return OHResponse.success(patientDTOS);
    }

    @GetMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Patient by code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Succes"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    @ResponseStatus(HttpStatus.OK)
    public OHSuccessWrapperDTO<PatientDTO> getPatient(@PathVariable Integer code) throws OHServiceException {
        logger.info("Get patient code:" + code);
        Patient patient = patientManager.getPatient(code);
        return OHResponse.success(modelMapper.map(patient, PatientDTO.class));
    }


    @GetMapping(value = "/patients/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Search Patient by code or name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Succes"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    @ResponseStatus(HttpStatus.OK)
    public OHSuccessWrapperDTO<PatientDTO> searchPatient(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "code", required = false) Integer code) throws OHServiceException {
        logger.info("Search patient name:" + name + " code:" + code);
        Patient patient = null;
        if (code != null) {
            patient = patientManager.getPatient(code);
        } else if (!name.equals("")) {
            patient = patientManager.getPatient(name);
        }
        return OHResponse.success(modelMapper.map(patient, PatientDTO.class));
    }

    @DeleteMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Delete Patient by code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Succes"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    @ResponseStatus(HttpStatus.OK)
    public OHSuccessWrapperDTO<HttpStatus> deletePatient(@PathVariable int code) throws OHServiceException {
        logger.info("Delete patient code:" + code);
        Patient patient = patientManager.getPatient(code);
        boolean isDeleted = false;
        if (patient != null) {
            isDeleted = patientManager.deletePatient(patient);
        }
        if (!isDeleted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not deleted!", OHSeverityLevel.ERROR));
        }
        return OHResponse.success(HttpStatus.OK);
    }
}
