package com.hospital.treatment.controller;

import com.hospital.treatment.dto.SaveSummary;
import com.hospital.treatment.dto.TreatmentRequest;
import com.hospital.treatment.dto.TreatmentResponse;
import com.hospital.treatment.service.PatientTreatmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/treatments")
@Tag(name = "Patient Treatment", description = "Save and fetch hospital test/treatment status")
public class PatientTreatmentController {

    private final PatientTreatmentService service;

    public PatientTreatmentController(PatientTreatmentService service) {
        this.service = service;
    }

    @Operation(summary = "Save/update treatment status from the UI string",
            description = "Input like 1001-BLOOD:C,1002-XRAY:P,1003-SCAN:C,1001-BLOOD:P. "
                    + "C=COMPLETED, P=PENDING. Last occurrence of the same patient+test wins.")
    @PostMapping
    public ResponseEntity<SaveSummary> save(@RequestBody TreatmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveFromString(request.input()));
    }

    @Operation(summary = "Fetch treatment status by Patient ID and Test")
    @GetMapping("/{id}/{test}")
    public TreatmentResponse get(@PathVariable Long id, @PathVariable String test) {
        return service.getByIdAndTest(id, test);
    }

    @Operation(summary = "Fetch all tests of one patient")
    @GetMapping("/patient/{id}")
    public List<TreatmentResponse> getByPatient(@PathVariable Long id) {
        return service.getByPatient(id);
    }

    @Operation(summary = "List every record (used by the dashboard)")
    @GetMapping
    public List<TreatmentResponse> getAll() {
        return service.getAll();
    }
}
