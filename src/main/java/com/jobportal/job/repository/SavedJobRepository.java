package com.jobportal.job.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.job.entity.SavedJob;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    Page<SavedJob> findByCandidateId(Long candidateId, Pageable pageable);

    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);

    void deleteByCandidateIdAndJobId(Long candidateId, Long jobId);

    long countByCandidateId(Long candidateId);
}
