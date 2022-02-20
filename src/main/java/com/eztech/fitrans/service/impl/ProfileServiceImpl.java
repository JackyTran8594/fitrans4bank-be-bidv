package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import com.eztech.fitrans.event.ScheduledTasks;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.Profile;
import com.eztech.fitrans.model.ProfileHistory;
import com.eztech.fitrans.repo.ProfileHistoryRepository;
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
import java.time.LocalDate;
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

    private static final BaseMapper<ProfileHistory, ProfileHistoryDTO> mapperHistory = new BaseMapper<>(
            ProfileHistory.class, ProfileHistoryDTO.class);

    private static ReadAndWriteDoc readandwrite;
    @Autowired
    private ProfileRepository repository;

    @Autowired
    private ProfileHistoryRepository profileHistoryRepo;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Override
    public ProfileDTO save(ProfileDTO profile) {
        Profile entity;
        if (!DataUtils.nullOrZero(profile.getId())) {
            ProfileDTO dto = findById(profile.getId());
            if (dto == null) {
                throw new ResourceNotFoundException("Profile " + profile.getId() + " not found");
            }
            dto.setCif(profile.getCif());
            dto.setCustomerid(profile.getCustomerid());
            dto.setStaffId(profile.getStaffId());
            dto.setStaffId_CM(profile.getStaffId_CM());
            dto.setStaffId_CT(profile.getStaffId_CT());
            dto.setStatus(profile.getStatus());
            dto.setReview(profile.getReview());
            dto.setReviewNote(profile.getReviewNote());
            dto.setLastUpdatedDate(LocalDateTime.now());
            dto.setState(profile.getState());
            dto.setPriority(profile.getPriority());
            dto.setPriorityValue(profile.getPriorityValue());
            entity = mapper.toPersistenceBean(dto);
        } else {
            profile.setState(0);
            entity = mapper.toPersistenceBean(profile);
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
    public Boolean confirmProfile(ConfirmRequest dto) {
        // // TODO Auto-generated method stub
        // Profile entity;
        // ProfileHistory profileHistory = new ProfileHistory();
        // ProfileDTO result = findById(dto.profile.getId());
        // try {
        //     if (result == null) {
        //         throw new ResourceNotFoundException("Profile" + dto.profile.getId() + "not found");
        //     }
        //     result.setState(dto.profile.getState());
        //     entity = mapper.toPersistenceBean(result);
        //     entity = repository.save(entity);

        //     profileHistory.setProfileId(dto.profile.getId());
        //     profileHistory.setTimeReceived(LocalDateTime.now());
        //     profileHistory.setState(dto.profile.getState());
        //     if (dto.isCM) {
        //         profileHistory.setStaffId(dto.profile.getStaffId_CM());
        //     }
        //     if (dto.isCT) {
        //         profileHistory.setStaffId(dto.profile.getStaffId_CT());
        //     }
        //     profileHistoryRepo.save(profileHistory);
        //     return true;

        // } catch (Exception e) {
        //     // TODO: handle exception
        //     System.out.println(e.getMessage());
        //     return false;
        // }
        return null;
    }

}
