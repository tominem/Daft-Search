package com.danielbyrne.daftsearch.domain;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class PropertyForSharing extends Property {

    private boolean malesOnly;
    private boolean femalesOnly;
    private boolean ownerOccupied;
    private int currentOccupants;
    private boolean hasSingle;
    private boolean hasDouble;
}
