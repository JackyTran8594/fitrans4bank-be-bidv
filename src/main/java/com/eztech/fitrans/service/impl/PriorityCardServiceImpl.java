package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.*;
import com.eztech.fitrans.exception.BusinessException;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.locale.Translator;
import com.eztech.fitrans.model.PriorityCard;
import com.eztech.fitrans.repo.PriorityCardRepository;
import com.eztech.fitrans.service.PriorityCardService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.eztech.fitrans.constants.Constants.ACTIVE;
import static com.eztech.fitrans.constants.Constants.MsgKey.MS0001;

@Service
@Slf4j
@CacheConfig(cacheNames = {"PriorityCardServiceImpl"}, cacheManager = "localCacheManager")
public class PriorityCardServiceImpl implements PriorityCardService {
    private static final BaseMapper<PriorityCard, PriorityCardDTO> mapper = new BaseMapper<>(PriorityCard.class, PriorityCardDTO.class);
    @Autowired
    private PriorityCardRepository repository;

 

    @Override
    @Transactional
    public PriorityCardDTO save(PriorityCardDTO entity) {
        PriorityCardDTO rtn;
        PriorityCard oldEntity;
        if (!DataUtils.nullOrZero(entity.getId())) {
            PriorityCardDTO dto = findById(entity.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("PriorityCard " + entity.getId() + " not found");
            }
          
            oldEntity = mapper.toPersistenceBean(entity);
        } else {
            oldEntity = mapper.toPersistenceBean(entity);
            entity.setStatus(ACTIVE);
        }
        rtn = mapper.toDtoBean(repository.save(oldEntity));
        return rtn;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        PriorityCardDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("PriorityCard " + id + " not found");
        }
        // validateDelete(id);
        repository.deleteById(id);
    }

    // private void validateDelete(Long id) {
    //     Long count = repository.countUserByPriorityCard(id);
    //     if (0L < count) {
    //         throw new BusinessException(ErrorCodeEnum.ER9999, "Nhóm quyền đang được sử dụng!");
    //     }
    // }

    @Override
    @Transactional
    public void deleteById(List<Long> ids) {
        if (DataUtils.notNullOrEmpty(ids)) {
            // for(Long id: ids){
            //     validateDelete(id);
            // }
            repository.delete(ids);
            // PriorityCardMapRepository.deletePriorityCardMap(ids);
        }
    }

    @Override
    public PriorityCardDTO findById(Long id) {
        Optional<PriorityCard> optional = repository.findById(id);
        if (optional.isPresent()) {
            PriorityCardDTO dto = mapper.toDtoBean(optional.get());
            //Get list PriorityCard map
            // List<String> listPriorityCardMap = PriorityCardMapRepository.getPriorityCardMap(dto.getId());
            // dto.setPriorityCards(listPriorityCardMap);
            return dto;
        }
        return null;
    }

    @Override
    public List<PriorityCardDTO> findAll() {
        List<PriorityCard> listData = repository.findAll();
        return mapper.toDtoBean(listData);
    }

    @Override
    public List<PriorityCardDTO> search(Map<String, Object> mapParam) {
        return repository.search(mapParam, PriorityCard.class);
    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        return repository.count(mapParam);
    }


    
}
