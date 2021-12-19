package com.eztech.fitrans.repo;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BaseRepositoryCustom<T> {
    List<T> search(Map<String, Object> searchDTO, Class<T> tClass);

    Long count(Map<String, Object> searchDTO);

    Integer updateStatus(Long id, String status, String lastUpdatedBy, LocalDateTime lastUpdateDate);

    Boolean checkExits(Long id, String code);
}
