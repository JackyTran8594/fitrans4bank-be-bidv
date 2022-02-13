package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import com.eztech.fitrans.event.ScheduledTasks;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.Profile;
import com.eztech.fitrans.repo.ProfileHistoryRepository;
import com.eztech.fitrans.repo.ProfileRepository;
import com.eztech.fitrans.service.ProfileHistoryService;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import com.eztech.fitrans.util.ReadAndWriteDoc;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
// import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.print.DocFlavor.URL;

@Service
@Slf4j
public class ProfileHistoryServiceImpl implements ProfileHistoryService {

    private static final BaseMapper<Profile, ProfileHistoryDTO> mapper = new BaseMapper<>(Profile.class,
            ProfileHistoryDTO.class);

    @Autowired
    private ProfileHistoryRepository repository;

    @Override
    public ProfileHistoryDTO save(ProfileHistoryDTO item) {
        Profile entity;
        if (!DataUtils.nullOrZero(item.getId())) {
            ProfileHistoryDTO dto = findById(item.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("Profile " + item.getId() + " not found");
            }
            dto.setStaffId(item.getStaffId());
            dto.setStatus(item.getStatus());
            dto.setState(item.getState());
            entity = mapper.toPersistenceBean(dto);
        } else {
            entity = mapper.toPersistenceBean(item);
        }
        entity = repository.save(entity);
        return mapper.toDtoBean(entity);
    }

    @Override
    public void deleteById(Long id) {
        ProfileHistoryDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("Profile " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Override
    public ProfileHistoryDTO findById(Long id) {
        Optional<Profile> optional = repository.findById(id);
        if (optional.isPresent()) {
            ProfileHistoryDTO dto = mapper.toDtoBean(optional.get());
            dto.fillTransient();
            return dto;
        }
        return null;
    }

    @Override
    public ProfileHistoryDTO detailById(Long id) {
        ProfileHistoryDTO dto = repository.detailById(id);
        if (dto != null) {
            dto.fillTransient();
        }
        return dto;
    }

    @Override
    public List<ProfileHistoryDTO> findAll() {
        List<Profile> listData = repository.findAll();
        List<ProfileHistoryDTO> list = mapper.toDtoBean(listData);
        list.stream()
                .forEach(item -> item.fillTransient());
        return list;
    }

    @Override
    public List<ProfileHistoryDTO> search(Map<String, Object> mapParam) {
        return repository.search(mapParam, Profile.class);

    }

    @Override
    public Long count(Map<String, Object> mapParam) {
        return repository.count(mapParam);
    }

}