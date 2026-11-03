package com.hospital.treatment.service;

import com.hospital.treatment.dto.SaveSummary;
import com.hospital.treatment.dto.TreatmentResponse;
import com.hospital.treatment.entity.PatientTreatment;
import com.hospital.treatment.exception.InvalidInputException;
import com.hospital.treatment.exception.TreatmentNotFoundException;
import com.hospital.treatment.repository.PatientTreatmentRepository;
import com.hospital.treatment.util.TreatmentInputParser;
import com.hospital.treatment.util.TreatmentInputParser.RawRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientTreatmentService {

    private final PatientTreatmentRepository repository;

    public PatientTreatmentService(PatientTreatmentRepository repository) {
        this.repository = repository;
    }

    /** C -> COMPLETED, P -> PENDING. Anything else is rejected. */
    static String convertStatus(String code) {
        return switch (code) {
            case "C" -> "COMPLETED";
            case "P" -> "PENDING";
            default -> throw new InvalidInputException(
                    "Invalid status '" + code + "'. Allowed: C (COMPLETED) or P (PENDING)");
        };
    }

    /**
     * Parse -> validate -> convert -> de-duplicate (last occurrence wins) -> save/update.
     * Everything is validated before anything is written, and the whole call is one
     * transaction, so a bad record means NOTHING is saved.
     */
    @Transactional
    public SaveSummary saveFromString(String input) {
        List<RawRecord> parsed = TreatmentInputParser.parse(input);

        // key = "id|TEST"; re-inserting replaces the value => last occurrence wins
        Map<String, PatientTreatment> latest = new LinkedHashMap<>();
        for (RawRecord r : parsed) {
            String key = r.id() + "|" + r.test();
            latest.remove(key);
            latest.put(key, new PatientTreatment(r.id(), r.test(), convertStatus(r.statusCode())));
        }

        List<PatientTreatment> saved = repository.saveAll(latest.values());   // insert or update

        List<TreatmentResponse> body = saved.stream().map(PatientTreatmentService::toResponse).toList();
        return new SaveSummary(parsed.size(), saved.size(), parsed.size() - saved.size(), body);
    }

    @Transactional(readOnly = true)
    public TreatmentResponse getByIdAndTest(Long id, String test) {
        String t = test == null ? "" : test.trim().toUpperCase();
        return repository.findByIdAndTest(id, t)
                .map(PatientTreatmentService::toResponse)
                .orElseThrow(() -> new TreatmentNotFoundException(
                        "No treatment found for Patient ID " + id + " and Test " + t));
    }

    @Transactional(readOnly = true)
    public List<TreatmentResponse> getByPatient(Long id) {
        List<TreatmentResponse> list = repository.findByIdOrderByTestAsc(id).stream()
                .map(PatientTreatmentService::toResponse).toList();
        if (list.isEmpty()) throw new TreatmentNotFoundException("No treatments found for Patient ID " + id);
        return list;
    }

    @Transactional(readOnly = true)
    public List<TreatmentResponse> getAll() {
        return repository.findAllByOrderByIdAscTestAsc().stream()
                .map(PatientTreatmentService::toResponse).toList();
    }

    private static TreatmentResponse toResponse(PatientTreatment p) {
        return new TreatmentResponse(p.getId(), p.getTest(), p.getStatus());
    }
}
