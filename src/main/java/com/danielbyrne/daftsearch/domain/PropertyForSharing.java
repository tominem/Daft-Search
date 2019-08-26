package com.danielbyrne.daftsearch.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class PropertyForSharing extends PropertyForRent {

    private boolean malesOnly;
    private boolean femalesOnly;
    private boolean ownerOccupied;
    private int currentOccupants;
    private boolean hasSingle;
    private boolean hasDouble;
    private String malesOrFemales;
    private boolean hasSharedRoom;

    public void setMalesOrFemales() {

        if (malesOnly) {
            malesOrFemales = "Males Only";
        } else if (femalesOnly) {
            malesOrFemales = "Females Only";
        } else {
            malesOrFemales = "Males or Females";
        }
    }
}