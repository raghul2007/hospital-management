package com.hospital.treatment.repository;

import com.hospital.treatment.entity.PatientTreatment;
import com.hospital.treatment.entity.PatientTreatmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientTreatmentRepository extends JpaRepository<PatientTreatment, PatientTreatmentId> {

    Optional<PatientTreatment> findByIdAndTest(Long id, String test);

    List<PatientTreatment> findByIdOrderByTestAsc(Long id);

    List<PatientTreatment> findAllByOrderByIdAscTestAsc();
}
