package com.hospital.treatment.entity;

import java.io.Serializable;
import java.util.Objects;

/** Composite primary key: one row per (Patient ID, Test). */
public class PatientTreatmentId implements Serializable {

    private Long id;
    private String test;

    public PatientTreatmentId() { }

    public PatientTreatmentId(Long id, String test) {
        this.id = id;
        this.test = test;
    }

    public Long getId() { return id; }
    public String getTest() { return test; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientTreatmentId that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(test, that.test);
    }

    @Override
    public int hashCode() { return Objects.hash(id, test); }
}
