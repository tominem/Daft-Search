package com.danielbyrne.daftsearch.repositories;

import com.danielbyrne.daftsearch.domain.PropertyForSale;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PropertyForSaleRepository extends MongoRepository<PropertyForSale, Long> {
}
