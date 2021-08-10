package com.eztech.fitrans.ecommerce.DTO;

public class TrafficAggregationDTO {
    private String period;
    private int value;
    private double trend;
    private TrafficComparisonDTO comparison;

    public TrafficAggregationDTO(String period, int value, double trend, TrafficComparisonDTO comparison) {
        this.period = period;
        this.value = value;
        this.trend = trend;
        this.comparison = comparison;
    }

    public TrafficAggregationDTO() { }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public double getTrend() {
        return trend;
    }

    public void setTrend(double trend) {
        this.trend = trend;
    }

    public TrafficComparisonDTO getComparison() {
        return comparison;
    }

    public void setComparison(TrafficComparisonDTO comparison) {
        this.comparison = comparison;
    }
}
