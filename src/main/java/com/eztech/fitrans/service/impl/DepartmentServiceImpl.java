package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.DepartmentDTO;
import com.eztech.fitrans.dto.response.ErrorCodeEnum;
import com.eztech.fitrans.exception.BusinessException;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.Department;
import com.eztech.fitrans.repo.DepartmentRepository;
import com.eztech.fitrans.service.DepartmentService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {
    private static final BaseMapper<Department, DepartmentDTO> mapper = new BaseMapper<>(Department.class, DepartmentDTO.class);
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public DepartmentDTO save(DepartmentDTO department) {
        Department entity;
        if(!DataUtils.nullOrZero(department.getId())) {
            DepartmentDTO dto = findById(department.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("Department " + department.getId() + " not found");
            }
            dto.setName(department.getName());
            dto.setDescription(department.getDescription());
            dto.setStatus(department.getStatus());
            entity = mapper.toPersistenceBean(dto);
        }else{
            entity = mapper.toPersistenceBean(department);
        }

        return mapper.toDtoBean(departmentRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        DepartmentDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("Department " + id + " not found");
        }
        validateDelete(id);
        departmentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteById(List<Long> ids) {
        if(DataUtils.notNullOrEmpty(ids)){
            for(Long id: ids){
                validateDelete(id);
            }
            departmentRepository.delete(ids);
        }
    }

    @Override
    public DepartmentDTO findById(Long id) {
        Optional<Department> optionalDepartmentDTO = departmentRepository.findById(id);
        if(optionalDepartmentDTO.isPresent()){
            return mapper.toDtoBean(optionalDepartmentDTO.get());
        }
        return null;
    }

    @Override
    public List<DepartmentDTO> findAll() {
        List<Department> listData = departmentRepository.findAll();
        return mapper.toDtoBean(listData);
    }

    @Override
    public List<DepartmentDTO> search(Map<String, Object> mapParam) {
        List<Department> listData = departmentRepository.search(mapParam,Department.class);
        return mapper.toDtoBean(listData);

    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        return departmentRepository.count(mapParam);
    }

    @Override
    public DepartmentDTO findByCode(String code) {
        Department data = departmentRepository.findByCode(code);
        return mapper.toDtoBean(data);
    }

    private void validateDelete(Long id) {
        Long count = departmentRepository.countUserByDep(id);
        if (0L < count) {
            throw new BusinessException(ErrorCodeEnum.ER9999, "Phòng ban đang được sử dụng!");
        }
    }
}
