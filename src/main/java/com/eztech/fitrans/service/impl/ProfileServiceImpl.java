package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.Profile;
import com.eztech.fitrans.repo.ProfileRepository;
import com.eztech.fitrans.service.ProfileService;
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
public class ProfileServiceImpl implements ProfileService {

  private static final BaseMapper<Profile, ProfileDTO> mapper = new BaseMapper<>(Profile.class,
      ProfileDTO.class);
  @Autowired
  private ProfileRepository repository;

  @Override
  public ProfileDTO save(ProfileDTO item) {
    Profile entity;
    if (!DataUtils.nullOrZero(item.getId())) {
      ProfileDTO dto = findById(item.getId());
      if (dto == null) {
        throw new ResourceNotFoundException("Profile " + item.getId() + " not found");
      }
      dto.setCustomerid(item.getCustomerid());
      dto.setStaffId(item.getStaffId());
      dto.setStatus(item.getStatus());
      dto.setState(item.getState());
      dto.setPriority(item.getPriority());
      dto.setPriorityValue(item.getPriorityValue());
      entity = mapper.toPersistenceBean(dto);
    } else {
      entity = mapper.toPersistenceBean(item);
    }

    return mapper.toDtoBean(repository.save(entity));
  }

  @Override
  public void deleteById(Long id) {
    ProfileDTO dto = findById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("Profile " + id + " not found");
    }
    repository.deleteById(id);
  }

  @Override
  public ProfileDTO findById(Long id) {
    Optional<Profile> optional = repository.findById(id);
    if (optional.isPresent()) {
      ProfileDTO dto = mapper.toDtoBean(optional.get());
      dto.fillTransient();
      return dto;
    }
    return null;
  }

  @Override
  public ProfileDTO detailById(Long id) {
    ProfileDTO dto = repository.detailById(id);
    if(dto != null){
      dto.fillTransient();
    }
    return dto;
  }

  @Override
  public List<ProfileDTO> findAll() {
    List<Profile> listData = repository.findAll();
    List<ProfileDTO> list = mapper.toDtoBean(listData);
    list.stream()
            .forEach(item -> item.fillTransient());
    return list;
  }

  @Override
  public List<ProfileDTO> search(Map<String, Object> mapParam) {
    return repository.search(mapParam, Profile.class);

  }

  @Override
  public Long count(Map<String, Object> mapParam) {
    return repository.count(mapParam);
  }
}
