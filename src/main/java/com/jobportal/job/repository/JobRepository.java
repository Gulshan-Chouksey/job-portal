package com.jobportal.job.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    Page<Job> findByEmployerId(Long employerId, Pageable pageable);

    long countByEmployerId(Long employerId);

    long countByEmployerIdAndStatus(Long employerId, JobStatus status);
}
