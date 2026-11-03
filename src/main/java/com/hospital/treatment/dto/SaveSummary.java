package com.hospital.treatment.dto;

import java.util.List;

public record SaveSummary(int recordsReceived, int recordsSaved, int duplicatesOverwritten,
                          List<TreatmentResponse> saved) { }
