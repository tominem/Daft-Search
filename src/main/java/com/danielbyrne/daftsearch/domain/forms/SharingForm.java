package com.danielbyrne.daftsearch.domain.forms;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.ModeOfTransport;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SharingForm {

    @NotNull
    @Min(1)
    private float maxPrice;

    @NotNull
    @Min(1)
    private int distanceInKms;

    private int commuteInMinutes;

    @NotNull
    private String location;

    @NotNull
    @Size(min = 1, max = 5)
    private County[] counties;

    private String roomType;

    private ModeOfTransport modeOfTransport;
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