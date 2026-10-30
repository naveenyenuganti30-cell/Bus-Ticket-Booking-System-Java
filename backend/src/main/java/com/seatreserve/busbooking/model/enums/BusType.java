package com.seatreserve.busbooking.model.enums;

public enum BusType {
    AC_SLEEPER_2_PLUS_1("AC Sleeper (2+1)"),
    AC_SEATER_2_PLUS_2("AC Seater (2+2)"),
    LUXURY_MULTI_AXLE("Volvo Multi-Axle AC Sleeper"),
    EXECUTIVE_VOLVO("Scania Executive AC");

    private final String displayName;

    BusType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
