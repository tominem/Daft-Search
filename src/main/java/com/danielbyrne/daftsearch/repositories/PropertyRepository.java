package com.danielbyrne.daftsearch.repositories;

import com.danielbyrne.daftsearch.domain.Property;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PropertyRepository extends MongoRepository<Property, Long> {
}
