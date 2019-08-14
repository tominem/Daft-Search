package com.danielbyrne.daftsearch.repositories;

import com.danielbyrne.daftsearch.domain.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
