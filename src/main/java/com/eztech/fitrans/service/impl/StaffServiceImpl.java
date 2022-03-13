package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.StaffDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.Staff;
import com.eztech.fitrans.repo.StaffRepository;
import com.eztech.fitrans.service.StaffService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StaffServiceImpl implements StaffService {

  private static final BaseMapper<Staff, StaffDTO> mapper = new BaseMapper<>(Staff.class,
      StaffDTO.class);
  @Autowired
  private StaffRepository repository;

  @Override
  public StaffDTO save(StaffDTO item) {
    Staff entity;
    if (!DataUtils.nullOrZero(item.getId())) {
      StaffDTO dto = findById(item.getId());
      if (dto == null) {
        throw new ResourceNotFoundException("Staff " + item.getId() + " not found");
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
    StaffDTO dto = findById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("Staff " + id + " not found");
    }
    repository.deleteById(id);
  }

  @Override
  public StaffDTO findById(Long id) {
    Optional<Staff> optional = repository.findById(id);
    if (optional.isPresent()) {
      return mapper.toDtoBean(optional.get());
    }
    return null;
  }

 

  @Override
  public List<StaffDTO> findAll() {
    List<Staff> listData = repository.findAll();
    return mapper.toDtoBean(listData);
  }

  @Override
  public List<StaffDTO> search(Map<String, Object> mapParam) {
    List<Staff> listData = repository.search(mapParam, Staff.class);
    return mapper.toDtoBean(listData);

  }

  @Override
  public Long count(Map<String, Object> mapParam) {
    return repository.count(mapParam);
  }
}
