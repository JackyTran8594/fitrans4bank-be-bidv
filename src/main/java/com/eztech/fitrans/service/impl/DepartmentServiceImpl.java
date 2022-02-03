package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.DepartmentDTO;
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

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {
    private static final BaseMapper<Department, DepartmentDTO> mapper = new BaseMapper<>(Department.class, DepartmentDTO.class);
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
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
    public void deleteById(Long id) {
        DepartmentDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("Department " + id + " not found");
        }
        departmentRepository.deleteById(id);
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
}
