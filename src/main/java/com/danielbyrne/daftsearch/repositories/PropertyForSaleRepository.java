package com.danielbyrne.daftsearch.repositories;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.PropertyForSale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PropertyForSaleRepository extends MongoRepository<PropertyForSale, Long> {

    /**
     * To be used when refreshing the database. Any document date prior to the current refresh
     * must no longer be a valid property.
     * @param localDateTime
     * @return List of Properties that can be removed from the repository
     */
    @Query("{'localDateTime' : {$lte:?0}}")
    List<PropertyForSale> getPropertiesWithDateBefore(LocalDateTime localDateTime);

    @Query(value = "{'county' : '?0'}", count = true)
    public int getCountByCounty(County county);
}
