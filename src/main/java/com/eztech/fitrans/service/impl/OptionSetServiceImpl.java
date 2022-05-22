package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.ErrorCodeEnum;
import com.eztech.fitrans.dto.response.OptionSetDTO;
import com.eztech.fitrans.dto.response.OptionSetMasterData;
import com.eztech.fitrans.dto.response.OptionSetValueDTO;
import com.eztech.fitrans.exception.InputInvalidException;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.locale.Translator;
import com.eztech.fitrans.model.OptionSet;
import com.eztech.fitrans.model.OptionSetValue;
import com.eztech.fitrans.repo.OptionSetRepository;
import com.eztech.fitrans.repo.OptionSetValueRepository;
import com.eztech.fitrans.service.OptionSetService;
import com.eztech.fitrans.service.OptionSetValueService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static com.eztech.fitrans.constants.Constants.ACTIVE;

@Service
@Slf4j
public class OptionSetServiceImpl implements OptionSetService {

    private static final BaseMapper<OptionSet, OptionSetDTO> mapper = new BaseMapper<>(OptionSet.class, OptionSetDTO.class);
    private static final BaseMapper<OptionSetValue, OptionSetValueDTO> mapperValue = new BaseMapper<>(OptionSetValue.class, OptionSetValueDTO.class);
    // private static final BaseMapper<OptionSetValue, OptionSetMasterData> mapperMaster = new BaseMapper<>(OptionSetValue.class, OptionSetMasterData.class);

    @Autowired
    private OptionSetRepository repository;

    @Autowired
    private OptionSetValueRepository optionSetValueRepository;

    @Autowired
    private OptionSetValueService optionSetValueService;

    public void validate(OptionSetDTO item) {
        if (DataUtils.isNullOrEmpty(item.getCode())) {
            throw new InputInvalidException(ErrorCodeEnum.ER0003, Translator.toMessage(Constants.MessageParam.OPTIONSET_CODE));
        }

        if (DataUtils.notNullOrEmpty(item.getCode()) && item.getCode().length() > 100) {
            throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.OPTIONSET_CODE), 100);
        }

        if (DataUtils.isNullOrEmpty(item.getName())) {
            throw new InputInvalidException(ErrorCodeEnum.ER0003, Translator.toMessage(Constants.MessageParam.OPTIONSET_NAME));
        }

        if (DataUtils.notNullOrEmpty(item.getName()) && item.getName().length() > 512) {
            throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.OPTIONSET_NAME), 512);
        }

        if (DataUtils.notNullOrEmpty(item.getDescription()) && item.getDescription().length() > 512) {
            throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.OPTIONSET_DESC), 512);
        }

        boolean checkExit = repository.checkExits(item.getId(), item.getCode());
        if (checkExit) {
            throw new InputInvalidException(ErrorCodeEnum.ER0009, Translator.toMessage(Constants.MessageParam.OPTIONSET_CODE));
        }
    }

    @Override
    @Transactional
    public OptionSetDTO save(OptionSetDTO item) {
        validate(item);
        OptionSet entity;
        if (!DataUtils.nullOrZero(item.getId())) {
            OptionSetDTO dto = findById(item.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("OptionSet " + item.getId() + " not found");
            }
            dto.setCode(item.getCode());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setStatus(item.getStatus());
            entity = mapper.toPersistenceBean(dto);
        } else {
            entity = mapper.toPersistenceBean(item);
            entity.setStatus(ACTIVE);
        }

        entity = repository.save(entity);

        OptionSetDTO dto = mapper.toDtoBean(entity);
        dto.setOptionSetValueDTOList(item.getOptionSetValueDTOList());
        //Save option-set-value
        saveOptionSetValue(dto);
        return dto;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        OptionSetDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("OptionSet " + id + " not found");
        }
        optionSetValueService.deleteByOptionSet(id);
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
    public Map getMapValueByCode(String code, boolean revertPr) {
        Map<String, String> rtn = new HashMap<>();
        try {
            OptionSetDTO optionSetDTO = detailByCode(code);
            List<OptionSetValueDTO> optionSetValueList = optionSetDTO.getOptionSetValueDTOList();
            if (DataUtils.notNullOrEmpty(optionSetValueList)) {
                if (revertPr) {
                    for (OptionSetValueDTO valueDTO : optionSetValueList) {
                        rtn.put(valueDTO.getValue(), valueDTO.getName());
                    }
                } else {
                    for (OptionSetValueDTO valueDTO : optionSetValueList) {
                        rtn.put(valueDTO.getName(), valueDTO.getValue());
                    }
                }
            }
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        return rtn;
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
        List<OptionSet> listData = repository.search(mapParam, OptionSet.class);
        return mapper.toDtoBean(listData);

    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        return repository.count(mapParam);
    }

    private void saveOptionSetValue(OptionSetDTO dto) {
        List<OptionSetValueDTO> optionSetValueDTOList = dto.getOptionSetValueDTOList();
        optionSetValueRepository.updateStatusTmp(dto.getId(), Constants.INACTIVE);
        if (DataUtils.notNullOrEmpty(optionSetValueDTOList)) {
            for (OptionSetValueDTO valueDTO : optionSetValueDTOList) {
                valueDTO.setOptionSetId(dto.getId());
                optionSetValueService.save(valueDTO);
            }
        }
    }

    public List<OptionSetMasterData> getOptionSetMasterData() {
        List<OptionSetMasterData> result = new ArrayList<OptionSetMasterData>();
        List<OptionSet> optionSets = repository.findAll();
        List<OptionSetValue> optionSetValues = optionSetValueRepository.findAll();
        for (OptionSet opt : optionSets) {
            for (OptionSetValue value : optionSetValues) {
                OptionSetMasterData ms = new OptionSetMasterData();
                if (opt.getId() == value.getOptionSetId()) {
                    ms.setCode(opt.getCode());
                    ms.setName(value.getName());
                    ms.setDescription(value.getDescription());
                    ms.setValue(value.getValue());
                    ms.setOptionSetId(value.getOptionSetId());
                    result.add(ms);
                }
            }
        }
        return result;
    }
}
