package com.danielbyrne.daftsearch.domain.forms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SharingForm extends LettingForm {

    private String roomType;
    private boolean male;

    private boolean hasSingle;
    private boolean hasDouble;
    private boolean hasShared;

    public void setRoomPreferences() {
        hasDouble = roomType.equals("double") || roomType.equals("non-shared") || roomType.equals("any");
        hasSingle = roomType.equals("single") || roomType.equals("non-shared") || roomType.equals("any");
        hasShared = roomType.equals("shared") || roomType.equals("any");
    }
}