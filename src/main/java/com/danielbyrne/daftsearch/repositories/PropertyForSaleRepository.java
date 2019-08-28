package com.danielbyrne.daftsearch.repositories;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.PropertyForSale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PropertyForSaleRepository extends MongoRepository<PropertyForSale, Long> {

    @Query(value = "{'county' : '?0'}", count = true)
    public int getCountByCounty(County county);
}
