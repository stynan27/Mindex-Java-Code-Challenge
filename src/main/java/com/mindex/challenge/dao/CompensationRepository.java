package com.mindex.challenge.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mindex.challenge.data.Compensation;

public interface CompensationRepository extends MongoRepository<Compensation, String> {

}
