package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.repo.BaseRepositoryCustom;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseRepositoryCustomImpl<T> extends BaseCustomRepository<T> implements BaseRepositoryCustom<T> {
    @Override
    public Integer updateStatus(Long id, String status, String lastUpdatedBy, LocalDateTime lastUpdateDate) {
        return null;
    }

    @Override
    @SuppressWarnings("java:S2447")
    public Boolean checkExits(Long id, String code) {
        return null;
    }

    @Override
    public List<T> search(Map<String, Object> searchDTO, Class<T> tClass) {
        Map<String, Object> parameters = new HashMap<>();
        String sql = buildQuery(searchDTO, parameters, false);
        return getResultList(sql, tClass, parameters);
    }

    @Override
    public Long count(Map<String, Object> paramSearch) {
        Map<String, Object> parameters = new HashMap<>();
        String sql = buildQuery(paramSearch, parameters, true);
        return getCountResult(sql, parameters);
    }

    @Override
    public String buildQuery(Map<String, Object> paramSearch, Map<String, Object> parameters, boolean count) {
        return null;
    }
}
