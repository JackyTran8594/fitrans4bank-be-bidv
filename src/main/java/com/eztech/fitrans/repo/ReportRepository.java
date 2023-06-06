package com.eztech.fitrans.repo;

import org.springframework.stereotype.Component;

import com.eztech.fitrans.model.ReportProfileView;

import java.util.List;
import java.util.Map;


@Component("ReportRepository")
public interface ReportRepository extends ReadOnlyRepository<ReportProfileView, Long>, ReportRepositoryCustom {
    List<ReportProfileView> getProfilesWithParams(List<Integer> transactionType, String code, Map<String, Object> parameters);

    List<ReportProfileView> search(Map<String, Object> searchDTO, Boolean isCount);

}
