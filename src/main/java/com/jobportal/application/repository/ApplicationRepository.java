package com.jobportal.application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.application.entity.JobApplication;

public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {

    Page<JobApplication> findByCandidateId(Long candidateId, Pageable pageable);

    Page<JobApplication> findByJobId(Long jobId, Pageable pageable);

    Page<JobApplication> findByJobEmployerId(Long employerId, Pageable pageable);

    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);
}
