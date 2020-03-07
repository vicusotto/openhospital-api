package org.isf.patient.rest;

import io.swagger.annotations.*;
import org.isf.patient.dto.PatientDTO;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.shared.controller.OHResponse;
import org.isf.shared.controller.OHResponseDTO;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@RestController
@Api(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {@Authorization(value = "basicAuth")})
public class PatientController {

    private static final String DEFAULT_PAGE_SIZE = "80";

    @Autowired
    protected PatientBrowserManager patientManager;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageSource messageSource;

    private final Logger logger = LoggerFactory.getLogger(PatientController.class);

    public PatientController(PatientBrowserManager patientManager) {
        this.patientManager = patientManager;
    }

    /**
     * Create new Patient
     *
     * @param newPatient
     * @return
     * @throws OHServiceException
     */
    @PostMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Insert new Patient", response = OHResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Succes, Patient created"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    OHResponse newPatient(@RequestBody PatientDTO newPatient) throws OHServiceException {
        String name = StringUtils.isEmpty(newPatient.getName()) ? newPatient.getFirstName() + " " + newPatient.getSecondName() : newPatient.getName();
        logger.info("Create patient " + name);
        boolean isCreated = patientManager.newPatient(modelMapper.map(newPatient, Patient.class));
        Patient patient = patientManager.getPatient(name);
        if (!isCreated || patient == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not created!", OHSeverityLevel.ERROR));
        }
        return OHResponse.success(HttpStatus.CREATED).withData(patient.getCode());
    }

    @ApiOperation(value = "Update Patient", response = OHResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Succes, Patient updated"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    @PutMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    OHResponse updatePatient(@PathVariable int code, @RequestBody PatientDTO updatePatient) throws OHServiceException {
        logger.info("Update patient code:" + code);
        Patient patient = modelMapper.map(updatePatient, Patient.class);
        patient.setCode(code);
        boolean isUpdated = patientManager.updatePatient(patient);
        if (!isUpdated) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not updated!", OHSeverityLevel.ERROR));
        }
        return OHResponse.success(HttpStatus.OK).withData(patient.getCode());
    }

    @GetMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Patiens", response = OHResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Succes"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    public OHResponse getPatients(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size) throws OHServiceException {
        logger.info("Get patients page:" + page + " size:" + size);
        ArrayList<Patient> patients = patientManager.getPatient(page, size);
        List<PatientDTO> patientDTOS = patients.stream().map(it -> modelMapper.map(it, PatientDTO.class)).collect(Collectors.toList());
        if (patientDTOS.size() == 0) {
            return OHResponse.success(HttpStatus.NO_CONTENT);
        } else {
            return OHResponse.success(HttpStatus.OK).withData(patientDTOS);
        }
    }

    @GetMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Patient by code", response = OHResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Succes"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    public OHResponse getPatient(@PathVariable Integer code) throws OHServiceException {
        logger.info("Get patient code:" + code);
        Patient patient = patientManager.getPatient(code);
        if (patient == null) {
            return OHResponse.success(HttpStatus.NO_CONTENT).withMesage("Patient Not Found");
        }
        return OHResponse.success(HttpStatus.OK).withData(modelMapper.map(patient, PatientDTO.class));
    }


    @GetMapping(value = "/patients/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Search Patient by code or name", response = OHResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Succes"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    public OHResponse searchPatient(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "code", required = false) Integer code) throws OHServiceException {
        logger.info("Search patient name:" + name + " code:" + code);
        Patient patient = null;
        if (code != null) {
            patient = patientManager.getPatient(code);
        } else if (!name.equals("")) {
            patient = patientManager.getPatient(name);
        }
        if (patient == null) {
            return OHResponse.success(HttpStatus.NO_CONTENT);
        }
        return OHResponse.success(HttpStatus.OK).withData(modelMapper.map(patient, PatientDTO.class));
    }

    @DeleteMapping(value = "/patients/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Delete Patient by code", response = OHResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Succes"),
            @ApiResponse(code = 401, message = "Not Authorized")})
    public OHResponse deletePatient(@PathVariable int code) throws OHServiceException {
        logger.info("Delete patient code:" + code);
        Patient patient = patientManager.getPatient(code);
        boolean isDeleted = false;
        if (patient != null) {
            isDeleted = patientManager.deletePatient(patient);
        } else {
            return OHResponse.success(HttpStatus.NOT_FOUND).withData(null);
        }
        if (!isDeleted) {
            throw new OHAPIException(new OHExceptionMessage(null, "Patient is not deleted!", OHSeverityLevel.ERROR));
        }
        return OHResponse.success(HttpStatus.OK);
    }
}
