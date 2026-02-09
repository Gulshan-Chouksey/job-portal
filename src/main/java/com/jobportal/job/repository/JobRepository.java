package com.jobportal.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.job.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

}
