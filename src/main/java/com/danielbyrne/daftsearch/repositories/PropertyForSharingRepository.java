package com.danielbyrne.daftsearch.repositories;

import com.danielbyrne.daftsearch.domain.PropertyForSharing;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PropertyForSharingRepository extends MongoRepository<PropertyForSharing, Long> {
}
