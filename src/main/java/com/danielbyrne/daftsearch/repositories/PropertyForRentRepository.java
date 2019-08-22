package com.danielbyrne.daftsearch.repositories;

import com.danielbyrne.daftsearch.domain.PropertyForRent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PropertyForRentRepository extends MongoRepository<PropertyForRent, Long> {
}
