package com.eztech.fitrans.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.eztech.fitrans.dto.response.ErrorCodeEnum;
import com.eztech.fitrans.dto.response.TransactionTypeDTO;
import com.eztech.fitrans.exception.BusinessException;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.TransactionType;
import com.eztech.fitrans.repo.TransactionTypeRepository;
import com.eztech.fitrans.service.TransactionTypeService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

import static com.eztech.fitrans.constants.Constants.ACTIVE;

@Service
@Slf4j
public class TransactionTypeServiceImpl implements TransactionTypeService {

    private static final BaseMapper<TransactionType, TransactionTypeDTO> mapper = new BaseMapper<>(TransactionType.class,
            TransactionTypeDTO.class);

    @Autowired
    private TransactionTypeRepository repository;

    @Override
    @Transactional
    public TransactionTypeDTO save(TransactionTypeDTO item) {
        TransactionType entity;
        if (!DataUtils.nullOrZero(item.getId())) {
            TransactionTypeDTO dto = findById(item.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("TransactionType" + item.getId() + " not found");
            }
            dto.setName(item.getName());
            dto.setType(item.getType());
            dto.setTransactionId(item.getTransactionId());
            dto.setTransactionDetail(item.getTransactionDetail());
            dto.setTransactionDetail2(item.getTransactionDetail2());
            dto.setStandardTimeCM(item.getStandardTimeCM());
            dto.setStandardTimeCT(item.getStandardTimeCT());
            dto.setStandardTimeChecker(item.getStandardTimeChecker());
            dto.setNote(item.getNote());
            entity = mapper.toPersistenceBean(dto);
        } else {
            entity = mapper.toPersistenceBean(item);
            entity.setStatus(ACTIVE);
        }
        return mapper.toDtoBean(repository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        TransactionTypeDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("TransactionType " + id + " not found");
        }
        validateDelete(DataUtils.parseToInt(id));
        repository.deleteById(id);
    }

    @Override
    public TransactionTypeDTO findById(Long id) {
        Optional<TransactionType> optional = repository.findById(id);
        if (optional.isPresent()) {
            return mapper.toDtoBean(optional.get());
        }
        return null;
    }

    @Override
    public List<TransactionTypeDTO> findAll() {
        List<TransactionType> listData = repository.findAll();
        return mapper.toDtoBean(listData);
    }

    @Override
    public List<TransactionTypeDTO> search(Map<String, Object> mapParam) {
        List<TransactionType> listData = repository.search(mapParam, TransactionType.class);
        return mapper.toDtoBean(listData);
    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        return repository.count(mapParam);
    }

    private void validateDelete(Integer type) {
        Long count = repository.countProfileByTransType(type);
        if (0L < count) {
            throw new BusinessException(ErrorCodeEnum.ER9999, "Loại giao dịch đang được sử dụng!");
        }
    }

}
