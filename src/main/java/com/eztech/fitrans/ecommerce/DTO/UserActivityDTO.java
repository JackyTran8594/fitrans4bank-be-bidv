package com.eztech.fitrans.ecommerce.DTO;

public class UserActivityDTO {
    private String label;
    private int pagesVisit;
    private double trend;

    public UserActivityDTO(String label, int pagesVisit, double trend) {
        this.label = label;
        this.pagesVisit = pagesVisit;
        this.trend = trend;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPagesVisit() {
        return pagesVisit;
    }

    public void setPagesVisit(int pagesVisit) {
        this.pagesVisit = pagesVisit;
    }

    public double getTrend() {
        return trend;
    }

    public void setTrend(double trend) {
        this.trend = trend;
    }
}
