package com.jobportal.job.repository;

import org.springframework.data.jpa.domain.Specification;

import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;

/**
 * JPA Specifications for building dynamic Job queries.
 * Each method returns a composable specification for optional filtering.
 */
public final class JobSpecification {

    private JobSpecification() {
        // utility class
    }

    public static Specification<Job> titleContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Job> locationContains(String location) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<Job> salaryMinGreaterThanOrEqual(Integer minSalary) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("salaryMin"), minSalary);
    }

    public static Specification<Job> salaryMaxLessThanOrEqual(Integer maxSalary) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("salaryMax"), maxSalary);
    }

    public static Specification<Job> statusEquals(JobStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }
}
