package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.ErrorCodeEnum;
import com.eztech.fitrans.dto.response.StaffContactDTO;
import com.eztech.fitrans.exception.BusinessException;
import com.eztech.fitrans.exception.InputInvalidException;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.StaffContact;
import com.eztech.fitrans.repo.StaffContactRepository;
import com.eztech.fitrans.service.StaffContactService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.eztech.fitrans.constants.Constants.ACTIVE;

@Service
@Slf4j
public class StaffContactServiceImpl implements StaffContactService {

    private static final BaseMapper<StaffContact, StaffContactDTO> mapper = new BaseMapper<>(StaffContact.class,
            StaffContactDTO.class);

    @Autowired
    private StaffContactRepository repository;

    @Override
    public StaffContactDTO save(StaffContactDTO item) {
        validate(item);
        StaffContact entity;
        if (!DataUtils.nullOrZero(item.getId())) {
            StaffContactDTO dto = findById(item.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("StaffContact" + item.getId() + " not found");
            }
            dto.setCif(item.getCif());
            dto.setCustomerId(item.getCustomerId());
            dto.setStaffIdCM(item.getStaffIdCM());
            dto.setStaffIdCT(item.getStaffIdCT());
            dto.setStaffIdCustomer(item.getStaffIdCustomer());
            entity = mapper.toPersistenceBean(dto);

        } else {
            entity = mapper.toPersistenceBean(item);
            entity.setStatus(ACTIVE);
        }
        return mapper.toDtoBean(repository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        // TODO Auto-generated method stub
        StaffContactDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("StaffContact " + id + "not found");
        }
        validateDelete(id);
        repository.deleteById(id);
    }

    @Override
    public StaffContactDTO findById(Long id) {
        // TODO Auto-generated method stub
        Optional<StaffContact> optional = repository.findById(id);
        if (optional.isPresent()) {
            return mapper.toDtoBean(optional.get());
        }
        return null;
    }

    @Override
    public List<StaffContactDTO> findAll() {
        // TODO Auto-generated method stub
        List<StaffContact> listData = repository.findAll();
        return mapper.toDtoBean(listData);
    }

    @Override
    public List<StaffContactDTO> search(Map<String, Object> mapParam) {
        // TODO Auto-generated method stub
        List<StaffContact> listData = repository.search(mapParam, StaffContact.class);
        return mapper.toDtoBean(listData);
    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        // TODO Auto-generated method stub
        return repository.count(mapParam);
    }

    @Override
    public Boolean findByCif(Long id, String cif) {
        return repository.checkExits(id, cif);
    }

    @Override
    public StaffContactDTO findByCustomerId(Long id) {
        StaffContact optional = repository.findByCustomerId(id);
        if (optional == null) {
            return null;
        }
        return mapper.toDtoBean(optional);
    }

    public void validate(StaffContactDTO item) {
        if (DataUtils.isNullOrEmpty(item.getCif())) {
            throw new InputInvalidException(ErrorCodeEnum.ER0003, "CIF");
        }

        if (DataUtils.notNullOrEmpty(item.getCif()) && item.getCif().length() > 50) {
            throw new InputInvalidException(ErrorCodeEnum.ER0010, "CIF", 50);
        }

        boolean checkExit = repository.checkExits(item.getId(), item.getCif());
        if (checkExit) {
            throw new InputInvalidException(ErrorCodeEnum.ER0009, "Mã CIF");
        }
    }

    private void validateDelete(Long id) {
        Long count = repository.countProfile(id);
        if (0L < count) {
            throw new BusinessException(ErrorCodeEnum.ER9999, "Đầu mối đang được sử dụng!");
        }
    }

}
