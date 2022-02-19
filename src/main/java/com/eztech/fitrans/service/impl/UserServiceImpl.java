package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.UserEntity;
import com.eztech.fitrans.repo.UserRepository;
import com.eztech.fitrans.service.UserService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private static final BaseMapper<UserEntity, UserDTO> mapper = new BaseMapper<>(UserEntity.class, UserDTO.class);
    @Autowired
    private UserRepository repository;

    @Override
    public UserDTO save(UserDTO entity) {
        UserEntity oldEntity;
        if(!DataUtils.nullOrZero(entity.getId())) {
            UserDTO dto = findById(entity.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("User " + entity.getId() + " not found");
            }
            dto.setFullName(entity.getFullName());
            dto.setEmail(entity.getEmail());
            dto.setPosition(entity.getPosition());
            dto.setDepartmentId(entity.getDepartmentId());
            dto.setStatus(entity.getStatus());
            oldEntity = mapper.toPersistenceBean(dto);
        }else{
            oldEntity = mapper.toPersistenceBean(entity);
        }

        return mapper.toDtoBean(repository.save(oldEntity));
    }

    @Override
    public void deleteById(Long id) {
        UserDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("User " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Override
    public UserDTO findById(Long id) {
        Optional<UserEntity> optionalDepartmentDTO = repository.findById(id);
        if(optionalDepartmentDTO.isPresent()){
            return mapper.toDtoBean(optionalDepartmentDTO.get());
        }
        return null;
    }

    @Override
    public List<UserDTO> findAll() {
        List<UserEntity> listData = repository.findAll();
        return mapper.toDtoBean(listData);
    }

    @Override
    public List<UserDTO> search(Map<String, Object> mapParam) {
        return repository.search(mapParam,UserDTO.class);
    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        return repository.count(mapParam);
    }

    @Override
    public UserDTO findByUsername(String username) {
        UserEntity entity =  repository.findByUsername(username);
        UserDTO user = mapper.toDtoBean(entity);
        return user;
    }
}
