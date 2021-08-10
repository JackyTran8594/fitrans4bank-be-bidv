package com.eztech.fitrans.ecommerce.DTO;

import java.util.List;

public class ProfitForTwoMonthChartDTO extends BaseChartDTO<Integer> {
    private List<ChartAdditionalInfoDTO<Integer>> aggregatedData;

    public ProfitForTwoMonthChartDTO(List<ChartDataDTO<Integer>> lines, List<String> axisXLabels,
                                     String chartLabel, List<ChartAdditionalInfoDTO<Integer>> aggregatedData) {
        super(lines, axisXLabels, chartLabel);
        this.aggregatedData = aggregatedData;
    }

    public ProfitForTwoMonthChartDTO() {
    }

    public List<ChartAdditionalInfoDTO<Integer>> getAggregatedData() {
        return aggregatedData;
    }

    public void setAggregatedData(List<ChartAdditionalInfoDTO<Integer>> aggregatedData) {
        this.aggregatedData = aggregatedData;
    }
}
