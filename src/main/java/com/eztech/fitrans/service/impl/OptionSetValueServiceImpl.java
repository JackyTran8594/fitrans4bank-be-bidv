package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.ErrorCodeEnum;
import com.eztech.fitrans.dto.response.OptionSetDTO;
import com.eztech.fitrans.dto.response.OptionSetValueDTO;
import com.eztech.fitrans.exception.InputInvalidException;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.locale.Translator;
import com.eztech.fitrans.model.OptionSetValue;
import com.eztech.fitrans.repo.OptionSetValueRepository;
import com.eztech.fitrans.service.OptionSetValueService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class OptionSetValueServiceImpl implements OptionSetValueService {
    private static final BaseMapper<OptionSetValue, OptionSetValueDTO> mapper = new BaseMapper<>(OptionSetValue.class, OptionSetValueDTO.class);

    @Autowired
    private OptionSetValueRepository repository;

    @Override
    public OptionSetValueDTO save(OptionSetValueDTO item) {
        validate(item);
        OptionSetValue entity;
        if (!DataUtils.nullOrZero(item.getId())) {
            OptionSetValueDTO dto = findById(item.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("OptionSetValue " + item.getId() + " not found");
            }
            dto.setValue(item.getValue());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setStatus(item.getStatus());
            entity = mapper.toPersistenceBean(dto);
        } else {
            entity = mapper.toPersistenceBean(item);
        }

        entity = repository.save(entity);
        return mapper.toDtoBean(entity);
    }

    @Override
    public void deleteById(Long id) {
        OptionSetValueDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("OptionSetValue " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Override
    public void deleteByOptionSet(Long optionSetId) {
        repository.deleteByOptionSetId(optionSetId);
    }

    @Override
    public OptionSetValueDTO findById(Long id) {
        Optional<OptionSetValue> optional = repository.findById(id);
        if (optional.isPresent()) {
            return mapper.toDtoBean(optional.get());
        }
        return null;
    }

    public void validate(OptionSetValueDTO item) {
        if (DataUtils.isNullOrEmpty(item.getName())) {
            throw new InputInvalidException(ErrorCodeEnum.ER0003, Translator.toMessage(Constants.MessageParam.OPTIONSET_NAME));
        }

        if (DataUtils.notNullOrEmpty(item.getName()) && item.getName().length() > 512) {
            throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.OPTIONSET_NAME), 512);
        }

        if (DataUtils.isNullOrEmpty(item.getValue())) {
            throw new InputInvalidException(ErrorCodeEnum.ER0003, Translator.toMessage(Constants.MessageParam.OPTIONSET_VALUE));
        }

        if (DataUtils.notNullOrEmpty(item.getValue()) && item.getValue().length() > 512) {
            throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.OPTIONSET_VALUE), 512);
        }

        if (DataUtils.notNullOrEmpty(item.getDescription()) && item.getDescription().length() > 512) {
            throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.OPTIONSET_DESC), 512);
        }

        OptionSetValue checkExit = null;
        if(item.getId() != null && 0 < item.getId()) {
            checkExit = repository.checkExits(item.getId(), item.getOptionSetId(), item.getName());
        }else{
            checkExit = repository.checkExits(item.getOptionSetId(), item.getName());
        }
        if (checkExit != null) {
            throw new InputInvalidException(ErrorCodeEnum.ER0009, Translator.toMessage(Constants.MessageParam.OPTIONSET_NAME));
        }
    }
}
