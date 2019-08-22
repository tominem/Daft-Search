package com.danielbyrne.daftsearch.repositories;

import com.danielbyrne.daftsearch.domain.PropertyForRent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PropertyForSharingRepository extends MongoRepository<PropertyForRent, Long> {
}
