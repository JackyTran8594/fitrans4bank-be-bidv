package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.ActionLogDTO;
import com.eztech.fitrans.model.ActionLog;
import com.eztech.fitrans.repo.ActionLogRepository;
import com.eztech.fitrans.service.LogService;
import com.eztech.fitrans.util.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LogServiceImpl implements LogService {
    private static final BaseMapper<ActionLog, ActionLogDTO> mapper = new BaseMapper<>(ActionLog.class, ActionLogDTO.class);
    @Autowired
    private ActionLogRepository repository;

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<ActionLogDTO> search(Map<String, Object> mapParam) {
        List<ActionLog> data = repository.search(mapParam, ActionLog.class);
        return mapper.toDtoBean(data);
    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        return repository.count(mapParam);
    }
}
