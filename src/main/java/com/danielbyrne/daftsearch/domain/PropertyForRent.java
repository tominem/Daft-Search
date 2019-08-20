package com.danielbyrne.daftsearch.domain;

import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class PropertyForRent extends Property {

    private String moveInDate;
    private String leaseLength;

    public String getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(String moveInDate) {
        this.moveInDate = moveInDate;
    }

    public String getLeaseLength() {
        return leaseLength;
    }

    public void setLeaseLength(String leaseLength) {
        this.leaseLength = leaseLength;
    }
}
