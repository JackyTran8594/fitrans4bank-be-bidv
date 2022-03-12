package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.*;
import com.eztech.fitrans.exception.BusinessException;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.locale.Translator;
import com.eztech.fitrans.model.Role;
import com.eztech.fitrans.model.RoleList;
import com.eztech.fitrans.model.RoleMap;
import com.eztech.fitrans.repo.RoleListRepository;
import com.eztech.fitrans.repo.RoleMapRepository;
import com.eztech.fitrans.repo.RoleRepository;
import com.eztech.fitrans.service.RoleService;
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

import static com.eztech.fitrans.constants.Constants.MsgKey.MS0001;

@Service
@Slf4j
@CacheConfig(cacheNames = {"RoleServiceImpl"}, cacheManager = "localCacheManager")
public class RoleServiceImpl implements RoleService {
    private static final BaseMapper<Role, RoleDTO> mapper = new BaseMapper<>(Role.class, RoleDTO.class);
    @Autowired
    private RoleRepository repository;

    @Autowired
    private RoleListRepository roleListRepository;
    @Autowired
    private RoleMapRepository roleMapRepository;

    @Override
    @Transactional
    public RoleDTO save(RoleDTO entity) {
        RoleDTO rtn;
        Role oldEntity;
        if (!DataUtils.nullOrZero(entity.getId())) {
            RoleDTO dto = findById(entity.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("Role " + entity.getId() + " not found");
            }
            dto.setName(entity.getName());
            dto.setDescription(entity.getDescription());
            oldEntity = mapper.toPersistenceBean(dto);
        } else {
            oldEntity = mapper.toPersistenceBean(entity);
        }

        rtn = mapper.toDtoBean(repository.save(oldEntity));
        rtn.setRoles(entity.getRoles());
        saveRoleMap(rtn);

        return rtn;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        RoleDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("Role " + id + " not found");
        }
        validateDelete(id);
        repository.deleteById(id);
    }

    private void validateDelete(Long id) {
        Long count = repository.countUserByRole(id);
        if (0L < count) {
            throw new BusinessException(ErrorCodeEnum.ER9999, "Nhóm quyền đang được sử dụng!");
        }
    }

    @Override
    @Transactional
    public void deleteById(List<Long> ids) {
        if (DataUtils.notNullOrEmpty(ids)) {
            for(Long id: ids){
                validateDelete(id);
            }
            repository.delete(ids);
            roleMapRepository.deleteRoleMap(ids);
        }
    }

    @Override
    public RoleDTO findById(Long id) {
        Optional<Role> optional = repository.findById(id);
        if (optional.isPresent()) {
            RoleDTO dto = mapper.toDtoBean(optional.get());
            //Get list role map
            List<String> listRoleMap = roleMapRepository.getRoleMap(dto.getId());
            dto.setRoles(listRoleMap);
            return dto;
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
        return repository.search(mapParam, UserDTO.class);
    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        return repository.count(mapParam);
    }

    @Override
    public List<RoleTreeDTO> treeRole() {
        return repository.mapRoleList();
    }

    @Override
    @Cacheable(key = "#code", cacheManager = "localCacheManager")
    public RoleList findByCode(String code) {
        return roleListRepository.findByCode(code);
    }


    private void saveRoleMap(RoleDTO dto) {
        List<String> newRoles = dto.getRoles();
        if (DataUtils.notNull(newRoles) && newRoles.isEmpty()) {
            //chi viec delete
            roleMapRepository.deleteRoleMap(dto.getId());
            return;
        }

        List<String> listRoleMap = roleMapRepository.getRoleMap(dto.getId());

        if (DataUtils.isNullOrEmpty(listRoleMap) && DataUtils.notNullOrEmpty(newRoles)) {
            //chi viec insert
            List<RoleMap> roleMapList = new ArrayList<>(newRoles.size());
            for (String roleList : newRoles) {
                RoleMap roleMap = new RoleMap();
                roleMap.setRoleId(dto.getId());
                roleMap.setRoleListCode(roleList);
                roleMapList.add(roleMap);
            }
            roleMapRepository.saveAll(roleMapList);
            return;
        }

        List<String> listDelete = new ArrayList<>(listRoleMap.size());
        listDelete.addAll(listRoleMap);
        listDelete.removeIf(newRoles::contains);
        if (DataUtils.notNullOrEmpty(listDelete)) {
            roleMapRepository.deleteRoleMap(dto.getId(), listDelete);
        }

        //Insert role moi
        newRoles.removeIf(listRoleMap::contains);
        if (DataUtils.notNullOrEmpty(newRoles)) {
            List<RoleMap> roleMapList = new ArrayList<>(newRoles.size());
            for (String roleList : newRoles) {
                RoleMap roleMap = new RoleMap();
                roleMap.setRoleId(dto.getId());
                roleMap.setRoleListCode(roleList);
                roleMapList.add(roleMap);
            }
            roleMapRepository.saveAll(roleMapList);
        }
    }

    @Override
    public List<MenuRoleTreeDTO> menuRole() {
        // TODO Auto-generated method stub
        List<MenuRoleTreeDTO> menuRoles = repository.mapMenuRole();
        return menuRoles;
    }
}
