package com.eztech.fitrans.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.eztech.fitrans.dto.response.ProfileListDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.ProfileList;
import com.eztech.fitrans.repo.ProfileListRepository;
import com.eztech.fitrans.service.ProfileListService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import static com.eztech.fitrans.constants.Constants.ACTIVE;

@Service
@Slf4j
public class ProfileListServiceImpl implements ProfileListService {

    private static final BaseMapper<ProfileList, ProfileListDTO> mapper = new BaseMapper<>(ProfileList.class,
            ProfileListDTO.class);

    @Autowired
    private ProfileListRepository repository;

    @Override
    public ProfileListDTO save(ProfileListDTO item) {
        // TODO Auto-generated method stub
        ProfileList entity;
        if (!DataUtils.nullOrZero(item.getId())) {
            ProfileListDTO dto = findById(item.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("ProfileList" + item.getId() + " not found");
            }
            dto.setProfileListId(item.getProfileListId());
            dto.setType(item.getType());
            dto.setAmount(item.getAmount());
            dto.setProfileListId(item.getProfileListId());
            dto.setNote(item.getNote());
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
        ProfileListDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("ProfileList " + id + "not found");
        }
        repository.deleteById(id);
    }

    @Override
    public ProfileListDTO findById(Long id) {
        // TODO Auto-generated method stub
        Optional<ProfileList> optional = repository.findById(id);
        if (optional.isPresent()) {
            return mapper.toDtoBean(optional.get());
        }
        return null;
    }

    @Override
    public List<ProfileListDTO> findAll() {
        // TODO Auto-generated method stub
        List<ProfileList> listData = repository.findAll();
        return mapper.toDtoBean(listData);
    }

    @Override
    public List<ProfileListDTO> search(Map<String, Object> mapParam) {
        // TODO Auto-generated method stub
        List<ProfileList> listData = repository.search(mapParam, ProfileList.class);
        return mapper.toDtoBean(listData);
    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        // TODO Auto-generated method stub
        return repository.count(mapParam);
    }

    @Override
    public List<ProfileListDTO> findListById(List<Long> profileListId) {
        // TODO Auto-generated method stub
        List<ProfileList> listData = repository.findListById(profileListId);
        return mapper.toDtoBean(listData);
    }

}
