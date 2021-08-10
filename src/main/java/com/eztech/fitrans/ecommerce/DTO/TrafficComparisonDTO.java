package com.eztech.fitrans.ecommerce.DTO;

public class TrafficComparisonDTO {
    private String previousPeriod;
    private int previousValue;
    private String currentPeriod;
    private int currentValue;

    public TrafficComparisonDTO(String previousPeriod, int previousValue, String currentPeriod, int currentValue) {
        this.previousPeriod = previousPeriod;
        this.previousValue = previousValue;
        this.currentPeriod = currentPeriod;
        this.currentValue = currentValue;
    }

    public String getPreviousPeriod() {
        return previousPeriod;
    }

    public void setPreviousPeriod(String previousPeriod) {
        this.previousPeriod = previousPeriod;
    }

    public int getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(int previousValue) {
        this.previousValue = previousValue;
    }

    public String getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(String currentPeriod) {
        this.currentPeriod = currentPeriod;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }
}
