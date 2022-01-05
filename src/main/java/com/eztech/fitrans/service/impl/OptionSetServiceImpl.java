package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.OptionSetDTO;
import com.eztech.fitrans.dto.response.OptionSetValueDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.OptionSet;
import com.eztech.fitrans.model.OptionSetValue;
import com.eztech.fitrans.model.Staff;
import com.eztech.fitrans.repo.OptionSetRepository;
import com.eztech.fitrans.repo.OptionSetValueRepository;
import com.eztech.fitrans.service.OptionSetService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class OptionSetServiceImpl implements OptionSetService {

    private static final BaseMapper<OptionSet, OptionSetDTO> mapper = new BaseMapper<>(OptionSet.class, OptionSetDTO.class);
    private static final BaseMapper<OptionSetValue, OptionSetValueDTO> mapperValue = new BaseMapper<>(OptionSetValue.class, OptionSetValueDTO.class);

    @Autowired
    private OptionSetRepository repository;

    @Autowired
    private OptionSetValueRepository optionSetValueRepository;

    @Override
    public OptionSetDTO save(OptionSetDTO item) {
        OptionSet entity;
        if (!DataUtils.nullOrZero(item.getId())) {
            OptionSetDTO dto = findById(item.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("OptionSet " + item.getId() + " not found");
            }
            dto.setName(item.getName());
            dto.setStatus(item.getStatus());
            entity = mapper.toPersistenceBean(dto);
        } else {
            entity = mapper.toPersistenceBean(item);
        }

        return mapper.toDtoBean(repository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        OptionSetDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("OptionSet " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Override
    public OptionSetDTO findById(Long id) {
        Optional<OptionSet> optional = repository.findById(id);
        if (optional.isPresent()) {
            return mapper.toDtoBean(optional.get());
        }
        return null;
    }

    @Override
    public OptionSetDTO detailById(Long id) {
        OptionSetDTO dto = findById(id);
        List<OptionSetValue> optionSetValueList = optionSetValueRepository.findByOptionSetIdAndStatus(id, Constants.ACTIVE);
        if (DataUtils.notNullOrEmpty(optionSetValueList)) {
            List<OptionSetValueDTO> optionSetValueDTOList = mapperValue.toDtoBean(optionSetValueList);
            dto.setOptionSetValueDTOList(optionSetValueDTOList);
        }
        return dto;
    }

    @Override
    public OptionSetDTO detailByCode(String code) {
        List<OptionSet> list = repository.findByCodeAndStatus(code, Constants.ACTIVE);
        if (DataUtils.isNullOrEmpty(list)) {
            throw new ResourceNotFoundException("OptionSet " + code + " not found");
        }
        OptionSet optionSet = list.get(0);
        OptionSetDTO dto = mapper.toDtoBean(optionSet);
        List<OptionSetValue> optionSetValueList = optionSetValueRepository.findByOptionSetIdAndStatus(dto.getId(), Constants.ACTIVE);
        if (DataUtils.notNullOrEmpty(optionSetValueList)) {
            List<OptionSetValueDTO> optionSetValueDTOList = mapperValue.toDtoBean(optionSetValueList);
            dto.setOptionSetValueDTOList(optionSetValueDTOList);
        }
        return dto;
    }

    @Override
    public List<OptionSetValueDTO> listByCode(String code) {
        List<OptionSet> list = repository.findByCodeAndStatus(code, Constants.ACTIVE);
        if (DataUtils.isNullOrEmpty(list)) {
            throw new ResourceNotFoundException("OptionSet " + code + " not found");
        }
        OptionSet optionSet = list.get(0);
        OptionSetDTO dto = mapper.toDtoBean(optionSet);
        List<OptionSetValue> optionSetValueList = optionSetValueRepository.findByOptionSetIdAndStatus(dto.getId(), Constants.ACTIVE);
        if (DataUtils.notNullOrEmpty(optionSetValueList)) {
            return mapperValue.toDtoBean(optionSetValueList);
        }
        return new ArrayList<>();
    }

    @Override
    public List<OptionSetDTO> findAll() {
        List<OptionSet> listData = repository.findAll();
        return mapper.toDtoBean(listData);
    }

    @Override
    public List<OptionSetDTO> search(Map<String, Object> mapParam) {
        List<OptionSet> listData = repository.search(mapParam, Staff.class);
        return mapper.toDtoBean(listData);

    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        return repository.count(mapParam);
    }
}
