package com.eztech.fitrans.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.eztech.fitrans.dto.response.TransactionTypeDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.TransactionType;
import com.eztech.fitrans.repo.TransactionTypeRepository;
import com.eztech.fitrans.service.TransactionTypeService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionTypeServiceImpl implements TransactionTypeService {

    private static final BaseMapper<TransactionType, TransactionTypeDTO> mapper = new BaseMapper<>(TransactionType.class,
            TransactionTypeDTO.class);

    @Autowired
    private TransactionTypeRepository repository;

    @Override
    public TransactionTypeDTO save(TransactionTypeDTO item) {
        // TODO Auto-generated method stub
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
            dto.setNote(item.getNote());
            entity = mapper.toPersistenceBean(dto);

        } else {
            entity = mapper.toPersistenceBean(item);
        }
        return mapper.toDtoBean(repository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        // TODO Auto-generated method stub
        TransactionTypeDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("TransactionType " + id + "not found");
        }
        repository.deleteById(id);
    }

    @Override
    public TransactionTypeDTO findById(Long id) {
        // TODO Auto-generated method stub
        Optional<TransactionType> optional = repository.findById(id);
        if (optional.isPresent()) {
            return mapper.toDtoBean(optional.get());
        }
        return null;
    }

    @Override
    public List<TransactionTypeDTO> findAll() {
        // TODO Auto-generated method stub
        List<TransactionType> listData = repository.findAll();
        return mapper.toDtoBean(listData);
    }

    @Override
    public List<TransactionTypeDTO> search(Map<String, Object> mapParam) {
        // TODO Auto-generated method stub
        List<TransactionType> listData = repository.search(mapParam, TransactionType.class);
        return mapper.toDtoBean(listData);
    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        // TODO Auto-generated method stub
        return repository.count(mapParam);
    }

}
