package com.danielbyrne.daftsearch.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class PropertyForSharing extends Property {

    private boolean malesOnly;
    private boolean femalesOnly;
    private boolean ownerOccupied;
    private int currentOccupants;
    private boolean hasSingle;
    private boolean hasDouble;
    private String malesOrFemales;

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