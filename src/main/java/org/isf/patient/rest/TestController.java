package org.isf.patient.rest;

import org.isf.shared.controller.dto.OHSuccessDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @GetMapping(value = "/test/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OHSuccessDTO getPatient(@PathVariable Integer code)  {

        return new OHSuccessDTO();
    }
}
