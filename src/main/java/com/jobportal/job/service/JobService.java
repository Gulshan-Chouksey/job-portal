package com.jobportal.job.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobportal.job.entity.Job;
import com.jobportal.job.repository.JobRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    public List<Job> getAllJobs(){
        return jobRepository.findAll();
    }
}
