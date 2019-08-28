package com.danielbyrne.daftsearch.repositories;

import com.danielbyrne.daftsearch.domain.PropertyForSharing;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PropertyForSharingRepository extends MongoRepository<PropertyForSharing, Long> {

    /**
     * To be used when refreshing the database. Any document date prior to the current refresh
     * must no longer be a valid property.
     * @param localDateTime
     * @return List of Properties that can be removed from the repository
     */
    @Query("{'localDateTime' : {$lte:?0}}")
    List<PropertyForSharing> getPropertiesWithDateBefore(LocalDateTime localDateTime);
}
