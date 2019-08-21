package com.danielbyrne.daftsearch.repositories;

import com.danielbyrne.daftsearch.domain.Property;
import org.springframework.data.repository.CrudRepository;

public interface PropertyRepository extends CrudRepository<Property, Long> {
}
