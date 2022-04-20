package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.DepartmentDTO;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import com.eztech.fitrans.dto.response.TransactionTypeDTO;
import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.event.ScheduledTasks;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.Profile;
import com.eztech.fitrans.model.ProfileHistory;
import com.eztech.fitrans.repo.ProfileHistoryRepository;
import com.eztech.fitrans.repo.ProfileRepository;
import com.eztech.fitrans.service.DepartmentService;
import com.eztech.fitrans.service.ProfileHistoryService;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.service.TransactionTypeService;
import com.eztech.fitrans.service.UserDetailsServiceImpl;
import com.eztech.fitrans.service.UserService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import com.eztech.fitrans.util.ReadAndWriteDoc;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
// import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.print.DocFlavor.URL;

import static com.eztech.fitrans.constants.Constants.ACTIVE;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private static final BaseMapper<Profile, ProfileDTO> mapper = new BaseMapper<>(Profile.class,
            ProfileDTO.class);

    private static final BaseMapper<ProfileHistory, ProfileHistoryDTO> mapperHistory = new BaseMapper<>(
            ProfileHistory.class, ProfileHistoryDTO.class);

    private static Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private static ReadAndWriteDoc readandwrite;
    @Autowired
    private ProfileRepository repository;

    // @Autowired
    // private ProfileHistoryRepository profileHistoryRepo;

    // @Autowired
    // private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private ProfileHistoryService profileHistoryService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTypeService transactionTypeService;

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
            profile.setLastUpdatedDate(LocalDateTime.now());
            entity = mapper.toPersistenceBean(profile);
        } else {
            entity = mapper.toPersistenceBean(profile);
            entity.setStatus(ACTIVE);
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
        // List<ProfileHistoryDTO> profileHis =
        // profileHistoryService.findByProfileId(id);
        // if (profileHis != null) {
        profileHistoryService.deleteByProfileId(id);
        // }
        // profileHistoryService.deleteByProfileId(id);
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
    public ProfileDTO detailById(Long id, Integer state) {
        ProfileDTO dto = repository.detailById(id, state);
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
        return repository.listDashboard();
    }

    @Override
    public ProfileDTO confirmProfile(ConfirmRequest item) {
        // // TODO Auto-generated method stub\
        ProfileDTO profile = item.getProfile();

        UserDTO user = userService.findByUsername(item.getUsername());
        if (DataUtils.isNullObject(user)) {
            throw new ResourceNotFoundException("User " + item.getUsername() + " not found");
        }
        DepartmentDTO department = departmentService.findByCode(item.getCode());
        if (DataUtils.isNullObject(department)) {
            throw new ResourceNotFoundException("department " + item.getCode() + " not found");
        }
        ProfileHistoryDTO profileHistory = new ProfileHistoryDTO();

        TransactionTypeDTO transactionType = transactionTypeService
                .findById(Long.parseLong(profile.getType().toString()));
        if (DataUtils.isNullObject(transactionType)) {
            throw new ResourceNotFoundException("transaction Type " + profile.getType().toString() + " not found");
        }

        // check profile is create or update - not save profile then tranfer
        if (DataUtils.isNullObject(item.profile.getCreatedDate())) {
            profile.setCreatedDate(LocalDateTime.now());
        }
        profileHistory.setTimeReceived(LocalDateTime.now());


        try {
            // check account admin or not
            if (item.username.toLowerCase().contains("admin")) {

                profile.setState(ProfileStateEnum.RECEIVED.getValue());
                profileHistory.setState(ProfileStateEnum.RECEIVED.getValue());

                // check department
                if (item.getCode() == "QTTD") {

                    if (profile.timeReceived_CM == null) {
                        profile.setTimeReceived_CM(LocalDateTime.now());
                    }
                } else if (item.getCode() == "GDKH") {
                    if (profile.timeReceived_CT == null) {
                        profile.setTimeReceived_CT(LocalDateTime.now());
                    }
                }

            } else {
                Map<String, Object> params = new HashMap<>();
                Long count = null;
                params.put("state", ProfileStateEnum.PROCESSING.getValue());
                // checking: transaction is finished
                if (item.getIsFinished()) {
                    profile.setState(ProfileStateEnum.FINISHED.getValue());
                    profileHistory.setState(ProfileStateEnum.FINISHED.getValue());
                    profile.setEndTime(LocalDateTime.now());
                    // update first row is processing
                    params.put("state", ProfileStateEnum.WAITING.getValue());
                    switch (item.getCode()) {
                        // case "QLKH":
                        // params.put("staffId", user.getId());
                        // break;
                        case "QTTD":
                            params.put("staffIdCM", user.getId());
                            break;
                        case "GDKH":
                            params.put("staffIdCT", user.getId());
                            break;
                    }

                    List<ProfileDTO> listData = repository.getProfileWithParams(params);

                    if (listData.size() == 1) {
                        ProfileDTO dto = listData.get(0);
                        dto.setState(ProfileStateEnum.PROCESSING.getValue());
                        save(dto);
                    }

                } else {

                    if (item.getCode().equals("QTTD")) {
                        params.put("staffId_CM", user.getId());
                        List<ProfileDTO> listData = repository.getProfileWithParams(params);
                        // params.put("state", ProfileStateEnum.ASSIGNED.getValue());
                        // check if profile is processing
                        // calculating time for processing time for
                        LocalDateTime processTime = LocalDateTime.now();
                        Integer additionalTime = 0;
                        // checking transaction type and plusing additional time
                        switch (profile.getTransactionType()) {
                            case 1:
                                if(!DataUtils.isNullOrEmpty(profile.getNumberOfPO()))
                                {
                                    if (profile.getNumberOfPO() >= 2) {
                                        additionalTime = additionalTime + 5 * profile.getNumberOfPO();
                                    }
                                }

                                if(!DataUtils.isNullOrEmpty(profile.getNumberOfBill()))
                                {
                                    if (profile.getNumberOfBill() >= 2) {
                                        additionalTime = additionalTime + 1 * profile.getNumberOfBill();
                                    }
                                }
                               
                                break;
                            case 2:
                                if (profile.getAdditionalTime() != null) {
                                    additionalTime = profile.getAdditionalTime();
                                }
                                break;
                            case 3:
                                break;
                        }

                        // checking process of profile
                        if (listData.size() == 1) {
                            ProfileDTO profile_first = new ProfileDTO();

                            params.put("state", ProfileStateEnum.WAITING.getValue());
                            List<ProfileDTO> listDataWaiting = repository.getProfileWithParams(params);
                            LocalDateTime endDate = LocalDateTime.now();
                            if (listDataWaiting.size() == 1) {
                                // check time received
                                profile_first = listDataWaiting.get(0);

                            } else {
                                profile_first = listData.get(0);
                                // endDate = profile_first.getProcessDate();
                            }

                            endDate = profile_first.getProcessDate();

                            boolean isAfter = profileHistory.getTimeReceived()
                                    .isAfter(endDate);
                            if (isAfter) {
                                processTime = profileHistory.timeReceived
                                        .plusMinutes(
                                                transactionType.getStandardTimeCM()
                                                        + transactionType.getStandardTimeChecker());

                                processTime = processTime.plusMinutes(additionalTime);

                            } else {
                                processTime = profile_first.endTime
                                        .plusMinutes(
                                                transactionType.getStandardTimeCM()
                                                        + transactionType.getStandardTimeChecker());

                                processTime = processTime.plusMinutes(additionalTime);
                            }

                            profile.setState(ProfileStateEnum.WAITING.getValue());
                            profileHistory.setState(ProfileStateEnum.WAITING.getValue());
                            // processTime = processTime.plus(listData.get(0).getEndTime());

                        } else if (listData.size() == 0) {

                            profile.setState(ProfileStateEnum.PROCESSING.getValue());
                            profileHistory.setState(ProfileStateEnum.PROCESSING.getValue());

                            // int totalTime = 0;
                            if (transactionType.getStandardTimeCM() != null) {
                                processTime = profileHistory.timeReceived
                                        .plusMinutes(transactionType.getStandardTimeCM());
                            }
                            if (transactionType.getStandardTimeChecker() != null) {
                                processTime = profileHistory.timeReceived
                                        .plusMinutes(transactionType.getStandardTimeChecker());
                            }

                            if (additionalTime != 0) {
                                processTime = processTime.plusMinutes(additionalTime);
                            }
                        }

                        if (processTime.getHour() > 17
                                || (processTime.getHour() == 17 && processTime.getMinute() > 0)) {
                            LocalDate tomorrow = LocalDate.now().plusDays(1);
                            int year = tomorrow.getYear();
                            int month = tomorrow.getMonthValue();
                            int day = tomorrow.getDayOfMonth();
                            int minutes = transactionType.getStandardTimeCM()
                                    + transactionType.getStandardTimeChecker();
                            int hour = 0;
                            if (minutes > 60) {
                                hour = 8 + minutes / 60;
                                minutes = minutes % 60;
                            } else {
                                hour = 8;
                            }

                            // set proces time = tomorrow
                            processTime = LocalDateTime.of(year, month, day, hour, minutes);
                            // processTime = LocalDateTime.
                        }
                        profile.setProcessDate(processTime);

                    } else if (item.getCode().equals("GDKH")) {
                        params.put("staffId_CT", user.getId());
                        List<ProfileDTO> listData = repository.getProfileWithParams(params);
                        if (listData.size() == 1) {
                            profile.setState(ProfileStateEnum.WAITING.getValue());
                            profileHistory.setState(ProfileStateEnum.WAITING.getValue());
                        } else if (listData.size() == 0) {
                            profile.setState(ProfileStateEnum.PROCESSING.getValue());
                            profileHistory.setState(ProfileStateEnum.PROCESSING.getValue());
                        }
                    }
                }
            }
            // check
           
            save(profile);
            profileHistory.setProfileId(item.getProfileId());
            profileHistory.setDepartmentId(department.getId());

            profileHistory.setStaffId(user.getId());
            profileHistory.setProfileId(profile.getId());
            profileHistoryService.save(profileHistory);
            return profile;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ProfileDTO saveHistory(ConfirmRequest item) {
        try {

            ProfileHistoryDTO profileHistory = new ProfileHistoryDTO();
            UserDTO user = userService.findByUsername(item.getUsername());

            if (DataUtils.isNullObject(user)) {
                throw new ResourceNotFoundException("User " + item.getUsername() + " not found");
            }
            DepartmentDTO department = departmentService.findByCode(item.getCode());
            if (DataUtils.isNullObject(department)) {
                throw new ResourceNotFoundException("department " + department.getCode() + " not found");
            }
            // save staffId when create profile
            if (item.getCode().equals("QLKH")) {
                item.getProfile().setStaffId(user.getId());
            }
            profileHistory.setDepartmentCode(department.getCode());
            profileHistory.setDepartmentId(department.getId());
            profileHistory.setTimeReceived(LocalDateTime.now());
            profileHistory.setStaffId(user.getId());
            profileHistory.setState(item.profile.getState());
            ProfileDTO dto = save(item.profile);
            // if(DataUtils.isNullOrEmpty(item.profile.getProcessDate())) {
            //     // item.profile.processDate = item.profile.get
            // }
            profileHistory.setProfileId(dto.getId());
            profileHistoryService.save(profileHistory);
            return dto;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    @Override
    public void deleteList(List<Long> ids) {
        // TODO Auto-generated method stub
        try {
            repository.deleteList(ids);
            profileHistoryService.deleteListByProfileId(ids);
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
        }

    }

}
