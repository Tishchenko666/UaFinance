package com.tish.db.bases;

public enum UkrainianMonth {
    JANUARY("Січень"),
    FEBRUARY("Лютий"),
    MARCH("Березень"),
    APRIL("Квітень"),
    MAY("Травень"),
    JUNE("Червень"),
    JULY("Липень"),
    AUGUST("Серпень"),
    SEPTEMBER("Вересень"),
    OCTOBER("Жовтень"),
    NOVEMBER("Листопад"),
    DECEMBER("Грудень");

    String urkMonth;

    UkrainianMonth(String urkMonth) {
        this.urkMonth = urkMonth;
    }

    public String getUrkMonth() {
        return urkMonth;
    }
}
