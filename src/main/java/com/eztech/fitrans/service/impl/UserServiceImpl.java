package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.OptionSetDTO;
import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.UserEntity;
import com.eztech.fitrans.repo.UserRepository;
import com.eztech.fitrans.service.OptionSetService;
import com.eztech.fitrans.service.UserService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.eztech.fitrans.constants.Constants.ACTIVE;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private static final BaseMapper<UserEntity, UserDTO> mapper = new BaseMapper<>(UserEntity.class, UserDTO.class);
    @Autowired
    private UserRepository repository;
    @Autowired
    private OptionSetService optionSetService;

    @Override
    @Transactional
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
            dto.setPhoneNumber(entity.getPhoneNumber());
            dto.setLastUpdatedDate(LocalDateTime.now());
            dto.setRoleId(entity.getRoleId());
            
            oldEntity = mapper.toPersistenceBean(dto);
            
            if(DataUtils.isNullOrEmpty(oldEntity.getPassword())){
                if(oldEntity.getUsername().trim().toLowerCase()== "admin") {
                    oldEntity.setPassword("$2a$10$xgMeNxDvGTeI2u/MwPqKV.oIq8O1OeDEhcy8k19V.dTvLpWe88xRS");
                } else {
                    oldEntity.setPassword("$2a$10$m2S.Cvn2xKFAOVwlevqTSurIYY7EvdidVkrXT8lwFqm56cEKYlK5G");
                }
            }
            repository.save(oldEntity);
            if(!DataUtils.isNullOrEmpty(dto.getRoleId())) {
                repository.deleteByRoleUser(dto.getId(), dto.getRoleId());
                Integer updatedRole = repository.updateUserRole(dto.getId(), dto.getRoleId());
                if(0 >= updatedRole){
                    repository.createUserRole(dto.getId(), entity.getRoleId());
                }
            } else {
                // repository.deleteByRoleUser(dto.getId(), dto.getRoleId());
                repository.createUserRole(dto.getId(), entity.getRoleId());
            }

        }else{
            entity.setLastUpdatedDate(LocalDateTime.now());
            entity.setStatus(ACTIVE);
            oldEntity = mapper.toPersistenceBean(entity);
            oldEntity = repository.save(oldEntity);
            if(!DataUtils.isNullOrEmpty(entity.getRoleId())) {
                repository.createUserRole(oldEntity.getId(), entity.getRoleId());
            }

        }

        return mapper.toDtoBean(oldEntity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        UserDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("User " + id + " not found");
        }
        repository.deleteById(id);
        repository.deleteByUserId(id);
    }

    @Override
    @Transactional
    public void deleteById(List<Long> ids) {
        if(DataUtils.notNullOrEmpty(ids)){
            repository.delete(ids);
        }
    }

    @Override
    public UserDTO findById(Long id) {
        Optional<UserEntity> optionalDepartmentDTO = repository.findById(id);

        if(optionalDepartmentDTO.isPresent()){
            UserDTO dto = mapper.toDtoBean(optionalDepartmentDTO.get());
            Long roleId = repository.findRoleIdByUserId(id);
            if(!DataUtils.isNullOrEmpty(roleId)) {
                dto.setRoleId(roleId);
            } 
            return dto;
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
        List<UserDTO> list = repository.search(mapParam,UserDTO.class);
        if(DataUtils.notNullOrEmpty(list)){
            Map<String,String> mapVitri = optionSetService.getMapValueByCode(Constants.OptionSetKey.VI_TRI, true);
            for(UserDTO dto: list){
                List<String> listRole = repository.listRole(dto.getId());
                dto.setListRole(listRole);
                if(DataUtils.notNullOrEmpty(listRole)){
                    dto.setRoles(StringUtils.collectionToDelimitedString(listRole , " - "));
                }

                //Fill Chức vụ
                if(mapVitri.containsKey(dto.getPosition())){
                    dto.setPosition(mapVitri.get(dto.getPosition()));
                }
            }
        }
        return list;
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

    @Override
    public List<UserDTO> findByDepartmentid(Long departmentId) {
        // TODO Auto-generated method stub
          List<UserEntity> entities =  repository.findByDepartmentid(departmentId);
          List<UserDTO> users = mapper.toDtoBean(entities);
          return users;

    }

    @Override
    public List<UserDTO> findByCode(String departmentCode) {
        // TODO Auto-generated method stub
        List<UserEntity> entities =  repository.findByDepartmentCode(departmentCode);
        List<UserDTO> users = mapper.toDtoBean(entities);
        return users;
    }


}
