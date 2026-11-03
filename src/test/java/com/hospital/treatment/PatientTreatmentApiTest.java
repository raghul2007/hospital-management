package com.hospital.treatment;

import com.hospital.treatment.repository.PatientTreatmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class PatientTreatmentApiTest {

    @Autowired MockMvc mvc;
    @Autowired PatientTreatmentRepository repository;

    @BeforeEach
    void clean() { repository.deleteAll(); }

    private String body(String input) {
        return input == null ? "{}" : "{\"input\":\"" + input + "\"}";
    }

    @Test
    void savesAndConvertsStatuses_lastOccurrenceWins() throws Exception {
        mvc.perform(post("/api/treatments").contentType(MediaType.APPLICATION_JSON)
                        .content(body("1001-BLOOD:C,1002-XRAY:P,1003-SCAN:C,1001-BLOOD:P")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recordsReceived").value(4))
                .andExpect(jsonPath("$.recordsSaved").value(3))
                .andExpect(jsonPath("$.duplicatesOverwritten").value(1));

        // 1001-BLOOD was C first, then P -> must be PENDING
        mvc.perform(get("/api/treatments/1001/BLOOD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
        mvc.perform(get("/api/treatments/1003/SCAN"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void updatesExistingRowOnSecondPost() throws Exception {
        mvc.perform(post("/api/treatments").contentType(MediaType.APPLICATION_JSON).content(body("2001-MRI:P")));
        mvc.perform(post("/api/treatments").contentType(MediaType.APPLICATION_JSON).content(body("2001-MRI:C")));
        mvc.perform(get("/api/treatments/2001/mri"))                      // case-insensitive lookup
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void rejectsInvalidInput_andSavesNothing() throws Exception {
        for (String bad : new String[]{null, "", "1001-:C", "-BLOOD:C", "1001-BLOOD:", "x-BLOOD:C", "1001-BLOOD:Z"}) {
            mvc.perform(post("/api/treatments").contentType(MediaType.APPLICATION_JSON).content(body(bad)))
                    .andExpect(status().isBadRequest());
        }
        // valid record followed by an invalid one: whole request rejected, nothing persisted
        mvc.perform(post("/api/treatments").contentType(MediaType.APPLICATION_JSON)
                        .content(body("3001-BLOOD:C,3002-:P")))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/treatments/3001/BLOOD")).andExpect(status().isNotFound());
    }

    @Test
    void unknownPatientReturns404() throws Exception {
        mvc.perform(get("/api/treatments/9999/XRAY"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
