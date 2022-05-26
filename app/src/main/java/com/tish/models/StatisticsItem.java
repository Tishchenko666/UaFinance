package com.tish.models;

public class StatisticsItem {

    private String typeName;
    private double amount;
    private double percent;

    public StatisticsItem() {
    }

    public StatisticsItem(String typeName, double amount, double percent) {
        this.typeName = typeName;
        this.amount = amount;
        this.percent = percent;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
