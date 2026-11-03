package com.hospital.treatment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Raw string sent from the UI")
public record TreatmentRequest(
        @Schema(example = "1001-BLOOD:C,1002-XRAY:P,1003-SCAN:C,1001-BLOOD:P")
        String input) { }
