package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.event.ScheduledTasks;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.Profile;
import com.eztech.fitrans.repo.ProfileRepository;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.print.DocFlavor.URL;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private static final BaseMapper<Profile, ProfileDTO> mapper = new BaseMapper<>(Profile.class,
            ProfileDTO.class);

    private static ReadAndWriteDoc  readandwrite;
    @Autowired
    private ProfileRepository repository;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Override
    public ProfileDTO save(ProfileDTO item) {
        Profile entity;
        if (!DataUtils.nullOrZero(item.getId())) {
            ProfileDTO dto = findById(item.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("Profile " + item.getId() + " not found");
            }
            dto.setCif(item.getCif());
            dto.setCustomerid(item.getCustomerid());
            dto.setStaffId(item.getStaffId());
            dto.setStaffId_CM(item.getStaffId_CM());
            dto.setStaffId_CT(item.getStaffId_CT());
            dto.setStatus(item.getStatus());
            dto.setReview(item.getReview());
            dto.setReviewNote(item.getReviewNote());
            dto.setLastUpdatedDate(LocalDateTime.now());
            dto.setState(item.getState());
            dto.setPriority(item.getPriority());
            dto.setPriorityValue(item.getPriorityValue());
            entity = mapper.toPersistenceBean(dto);
        } else {
            entity = mapper.toPersistenceBean(item);
        }
        entity = repository.save(entity);
        scheduledTasks.fireGreeting();
        return mapper.toDtoBean(entity);
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
        if (dto != null) {
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

    @Override
    public List<ProfileDTO> dashboard() {
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("pageNumber", 0);
        mapParam.put("pageSize", 10);
        return repository.search(mapParam, Profile.class);
    }

    @Override
    public String exportDocument() {
        String strDoc= "";
        // readandwrite.ExportDocFile();
       
        return strDoc;
    }
}
