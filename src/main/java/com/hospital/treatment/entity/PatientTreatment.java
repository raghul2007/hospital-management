package com.hospital.treatment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/** Table PATIENT_TREATMENT (ID LONG, TEST VARCHAR, STATUS VARCHAR). */
@Entity
@Table(name = "PATIENT_TREATMENT")
@IdClass(PatientTreatmentId.class)
public class PatientTreatment {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Id
    @Column(name = "TEST", nullable = false, length = 100)
    private String test;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;

    public PatientTreatment() { }

    public PatientTreatment(Long id, String test, String status) {
        this.id = id;
        this.test = test;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTest() { return test; }
    public void setTest(String test) { this.test = test; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
