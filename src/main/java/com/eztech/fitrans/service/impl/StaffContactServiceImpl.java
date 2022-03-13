package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.StaffContactDTO;
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

@Service
@Slf4j
public class StaffContactServiceImpl implements StaffContactService {

    private static final BaseMapper<StaffContact, StaffContactDTO> mapper = new BaseMapper<>(StaffContact.class,
            StaffContactDTO.class);

    @Autowired
    private StaffContactRepository repository;

    @Override
    public StaffContactDTO save(StaffContactDTO item) {
        // TODO Auto-generated method stub
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

}
