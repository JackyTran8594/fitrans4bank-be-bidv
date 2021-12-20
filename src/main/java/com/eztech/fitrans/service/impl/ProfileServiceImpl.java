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
      dto.setStatus(item.getStatus());
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
      return mapper.toDtoBean(optional.get());
    }
    return null;
  }

  @Override
  public List<ProfileDTO> findAll() {
    List<Profile> listData = repository.findAll();
    return mapper.toDtoBean(listData);
  }

  @Override
  public List<ProfileDTO> search(Map<String, Object> mapParam) {
    List<Profile> listData = repository.search(mapParam, Profile.class);
    return mapper.toDtoBean(listData);

  }

  @Override
  public Long count(Map<String, Object> mapParam) {
    return repository.count(mapParam);
  }
}
