package com.eztech.fitrans.ecommerce.DTO;

import java.util.List;

public class BaseChartDTO<T> {
    private List<ChartDataDTO<T>> lines;
    private List<String> axisXLabels;
    private String chartLabel;

    public BaseChartDTO(List<ChartDataDTO<T>> lines, List<String> axisXLabels, String chartLabel) {
        this.lines = lines;
        this.axisXLabels = axisXLabels;
        this.chartLabel = chartLabel;
    }

    public BaseChartDTO() { }

    public List<ChartDataDTO<T>> getLines() {
        return lines;
    }

    public void setLines(List<ChartDataDTO<T>> lines) {
        this.lines = lines;
    }

    public List<String> getAxisXLabels() {
        return axisXLabels;
    }

    public void setAxisXLabels(List<String> axisXLabels) {
        this.axisXLabels = axisXLabels;
    }

    public String getChartLabel() {
        return chartLabel;
    }

    public void setChartLabel(String chartLabel) {
        this.chartLabel = chartLabel;
    }
}
