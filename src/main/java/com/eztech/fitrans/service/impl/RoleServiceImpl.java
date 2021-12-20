package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.RoleDTO;
import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.Role;
import com.eztech.fitrans.model.UserEntity;
import com.eztech.fitrans.repo.RoleRepository;
import com.eztech.fitrans.repo.UserRepository;
import com.eztech.fitrans.service.RoleService;
import com.eztech.fitrans.service.UserService;
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
public class RoleServiceImpl implements RoleService {
    private static final BaseMapper<Role, RoleDTO> mapper = new BaseMapper<>(Role.class, RoleDTO.class);
    @Autowired
    private RoleRepository repository;

    @Override
    public RoleDTO save(RoleDTO entity) {
        Role oldEntity;
        if(!DataUtils.nullOrZero(entity.getId())) {
            RoleDTO dto = findById(entity.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("Role " + entity.getId() + " not found");
            }
            dto.setName(entity.getName());
            dto.setDescription(entity.getDescription());
            oldEntity = mapper.toPersistenceBean(dto);
        }else{
            oldEntity = mapper.toPersistenceBean(entity);
        }

        return mapper.toDtoBean(repository.save(oldEntity));
    }

    @Override
    public void deleteById(Long id) {
        RoleDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("Role " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Override
    public RoleDTO findById(Long id) {
        Optional<Role> optional = repository.findById(id);
        if(optional.isPresent()){
            return mapper.toDtoBean(optional.get());
        }
        return null;
    }

    @Override
    public List<RoleDTO> findAll() {
        List<Role> listData = repository.findAll();
        return mapper.toDtoBean(listData);
    }

    @Override
    public List<RoleDTO> search(Map<String, Object> mapParam) {
        return repository.search(mapParam,UserDTO.class);
    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        return repository.count(mapParam);
    }
}
