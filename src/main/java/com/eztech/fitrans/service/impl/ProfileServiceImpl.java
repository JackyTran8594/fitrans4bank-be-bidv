package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.DepartmentDTO;
import com.eztech.fitrans.dto.response.MessageDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
// import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
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
            // if(DataUtils.isNullOrEmpty(cs))
            // profile.setPriorityNumber(0);
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
    public ProfileDTO detailByIdAndState(Long id, Integer state) {
        ProfileDTO dto = repository.detailByIdAndState(id, state);
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
            // check account admin or not - GDKH
            Boolean isAsc = false;

            if (item.username.toLowerCase().contains("admin")) {
                if (item.getCode().equals("GDKH")) {

                    // get data is waiting with order by process_date - QTTD
                    Map<String, Object> params = new HashMap<>();
                    params.put("state", ProfileStateEnum.WAITING.getValue());
                    params.put("staffIdCM", profile.getStaffId_CM());
                    isAsc = true;
                    List<ProfileDTO> listData = new ArrayList<>();
                    // checking profile that is deliveried
                    if (profile.getState().equals(ProfileStateEnum.ADDITIONAL.getValue())) {
                        profile.setTimeReceived_CT(LocalDateTime.now());
                        // params.put("staffIdCT", profile.getStaffId_CT());
                        // listData = repository.getProfileWithParams(params, isAsc);
                        // if(listData.size() > 0) {
                        // // int lastIndex = listData.size() - 1;
                        // profile.setTimeReceived_CT(LocalDateTime.now());
                        // }
                    } else {
                        // type 1,2 - QTTD
                        params.put("staffIdCT", "NULL");
                        listData = repository.getProfileWithParams(params, isAsc);
                        if (listData.size() > 0) {
                            this.updateProfileList(listData, profile, user, profileHistory, department.getId(),
                                    item.getCode(),
                                    transactionType.getType());
                        } else {
                            // listData waitting
                            profile.setTimeReceived_CT(LocalDateTime.now());
                        }

                    }

                    // set state again
                    profile.setState(ProfileStateEnum.RECEIVED.getValue());
                    profileHistory.setState(ProfileStateEnum.RECEIVED.getValue());
                    profile.setTimeReceived_CT(profileHistory.getTimeReceived());
                    profile.setRealTimeReceivedCT(profileHistory.getTimeReceived());

                }

            } else {
                Map<String, Object> params = new HashMap<>();
                Long count = null;
                params.put("state", ProfileStateEnum.PROCESSING.getValue());
                // checking: transaction is finished
                if (item.getIsFinished()) {
                    profile.setState(ProfileStateEnum.FINISHED.getValue());
                    profileHistory.setState(ProfileStateEnum.FINISHED.getValue());
                    profile.setEndTime(profileHistory.getTimeReceived());
                    // update first row is processing
                    params.put("state", ProfileStateEnum.WAITING.getValue());
                    params.put("code", item.getUsername());

                    Map<String, Object> paramsWaiting = new HashMap<>();
                    paramsWaiting.put("state", ProfileStateEnum.WAITING.getValue());
                    List<ProfileDTO> listProfileWaiting = new ArrayList<>();
                    switch (item.getCode()) {
                        // transaction type : 1,2
                        // type 1: QTTD is not finished transaction, it hasn't staffId_CT
                        // type 2 :QTTD is finished transaction, it hasn't staffId_CT
                        case "QTTD":
                            paramsWaiting.put("staffId_CM", user.getId());
                            paramsWaiting.put("staffId_CT", "NULL");
                            // tính thời gian còn lại để cộng vào lần bàn giao sau cho hồ sơ cần bổ sung
                            // bắt đầu từ thời điểm chuyển đổi trạng thái thành cần bổ sung - additional
                            LocalDateTime from = profile.getTimeReceived_CM();
                            LocalDateTime to = profile.getEndTime();
                            LocalDateTime processTime = profile.getProcessDate();
                            if (to.isAfter(from) && processTime.isAfter(to)) {
                                // set endtime = processDate
                                profile.setProcessDate(to);
                            } else {

                            }

                            listProfileWaiting = repository.getProfileWithParams(paramsWaiting, isAsc);
                            break;
                        case "GDKH":
                            if (transactionType.getType().equals(1)) {
                                paramsWaiting.put("staffId_CT", user.getId());
                            }
                            if (transactionType.getType().equals(3)) {
                                paramsWaiting.put("staffId_CM", "NULL");
                                paramsWaiting.put("staffId_CT", user.getId());
                            }
                            listProfileWaiting = repository.getProfileWithParams(paramsWaiting, isAsc);
                            break;
                    }

                    this.updateProfileList(listProfileWaiting, profile, user, profileHistory, department.getId(),
                            item.getCode(), transactionType.getType());

                } else {

                    if (item.getCode().equals("QTTD")) {
                        LocalDateTime timeReceived_CM = LocalDateTime.now();
                        item.getProfile().setStaffId_CM(user.getId());
                        params.put("staffId_CM", user.getId());
                        // params.put("type", transactionType.getType());
                        // check profile is additional and delivery again
                        Integer timeForAdditional = 0;
                        if (profile.getState().equals(ProfileStateEnum.ADDITIONAL.getValue())) {
                            if (!DataUtils.isNullOrEmpty(profile.getAdditionalTime())) {
                                timeForAdditional = profile.getAdditionalTime();
                            }
                        }

                        // check if profile is processing
                        // calculating time for processing time for
                        LocalDateTime processTime = LocalDateTime.now();
                        Integer additionalTime = 0;
                        // checking transaction type and plusing additional time
                        switch (transactionType.getType()) {
                            case 1:
                                if (!DataUtils.isNullOrEmpty(profile.getNumberOfPO())) {
                                    if (profile.getNumberOfPO() >= 2) {
                                        additionalTime = additionalTime + 5 * profile.getNumberOfPO();
                                    }
                                }

                                if (!DataUtils.isNullOrEmpty(profile.getNumberOfBill())) {
                                    if (profile.getNumberOfBill() >= 2) {
                                        additionalTime = additionalTime + 1 * profile.getNumberOfBill();
                                    }
                                }

                                params.put("staffId_CT", "NULL");

                                break;
                            case 2:

                                params.put("staffId_CT", "NULL");
                                break;
                            default:
                                break;
                        }

                        List<ProfileDTO> listData = repository.getProfileWithParams(params, isAsc);

                        // checking process of profile
                        if (listData.size() == 1) {
                            ProfileDTO profile_first = new ProfileDTO();

                            params.put("state", ProfileStateEnum.WAITING.getValue());
                            List<ProfileDTO> listDataWaiting = repository.getProfileWithParams(params, isAsc);
                            LocalDateTime date = LocalDateTime.now();
                            if (listDataWaiting.size() >= 1) {
                                // check time received
                                profile_first = listDataWaiting.get(0);

                            } else {
                                profile_first = listData.get(0);
                            }

                            // default not null
                            date = profile_first.getProcessDate();
                            boolean isAfter = profileHistory.getTimeReceived()
                                    .isAfter(date);

                            // if(isAfter) {
                            // profile.setTimeReceived_CM(profileHistory.getTimeReceived());
                            // processTime = profileHistory.getTimeReceived();
                            // } else {
                            // profile.setTimeReceived_CM(profile_first.getProcessDate());
                            // processTime = profile_first.getProcessDate();
                            // }

                            processTime = profile_first.getProcessDate();

                            if (!DataUtils.isNullOrEmpty(transactionType.getStandardTimeCM())) {
                                processTime = processTime.plusMinutes(transactionType.getStandardTimeCM());
                            }

                            if (!DataUtils.isNullOrEmpty(transactionType.getStandardTimeChecker())) {
                                processTime = processTime.plusMinutes(transactionType.getStandardTimeChecker());
                            }

                            if (!DataUtils.isNullOrEmpty(additionalTime)) {
                                processTime = processTime.plusMinutes(additionalTime);
                            }

                            // processTime = profile_first.getProcessDate()
                            // .plusMinutes(
                            // transactionType.getStandardTimeCM()
                            // + transactionType.getStandardTimeChecker());

                            // processTime = processTime.plusMinutes(additionalTime);

                            profile.setState(ProfileStateEnum.WAITING.getValue());
                            profileHistory.setState(ProfileStateEnum.WAITING.getValue());
                            // processTime = processTime.plus(listData.get(0).getEndTime());

                            // day, hour, time of process time
                            int monthOfProfile = processTime.getMonthValue();
                            int dayOfProfile = processTime.getDayOfMonth();
                            int hourOfProfile = processTime.getHour();
                            int minutesOfProfile = processTime.getMinute();
                            int month = LocalDateTime.now().getMonthValue();
                            int dayOfMonth = LocalDateTime.now().getDayOfMonth();

                            // checking time received of record to moving profile in tomorrow

                            if ((monthOfProfile == month) && (dayOfProfile == dayOfMonth)) {
                                if (hourOfProfile >= 17 && minutesOfProfile > 0) {
                                    processTime = DataUtils.checkTime(processTime, 17,
                                            transactionType.getStandardTimeCM(),
                                            transactionType.getStandardTimeChecker(), additionalTime);
                                    LocalDate tomorrow = LocalDate.now().plusDays(1);
                                    int year = tomorrow.getYear();
                                    int m = tomorrow.getMonthValue();
                                    int day = tomorrow.getDayOfMonth();
                                    LocalDateTime timeReceived = LocalDateTime.of(year, m, day, 8, 0, 0);
                                    profile.setTimeReceived_CM(timeReceived);
                                }

                            }

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

                            if (processTime.getHour() > 17) {
                                processTime = DataUtils.checkTime(processTime, 17, transactionType.getStandardTimeCM(),
                                        transactionType.getStandardTimeChecker(), additionalTime);
                            }
                            profile.setTimeReceived_CM(profileHistory.getTimeReceived());
                        }

                        // minus time for profile which is deliveried again
                        if (timeForAdditional > 0) {
                            processTime = processTime.minusMinutes(timeForAdditional);
                        }

                        profile.setProcessDate(processTime);
                        profile.setRealTimeReceivedCM(profileHistory.getTimeReceived());

                    } else if (item.getCode().equals("GDKH")) {
                        // received profile at GDKH
                        item.getProfile().setStaffId_CT(user.getId());
                        profile.setState(ProfileStateEnum.PROCESSING.getValue());
                        profileHistory.setState(ProfileStateEnum.PROCESSING.getValue());

                        // params.put("staffId_CT", user.getId());
                        // params.put("state", ProfileStateEnum.PROCESSING.getValue());
                        // // get profiles is processing
                        // List<ProfileDTO> listData = repository.getProfileWithParams(params, isAsc);

                        // if (listData.size() == 1) {
                        // profile.setState(ProfileStateEnum.WAITING.getValue());
                        // profileHistory.setState(ProfileStateEnum.WAITING.getValue());
                        // } else if (listData.size() == 0) {
                        // profile.setState(ProfileStateEnum.PROCESSING.getValue());
                        // profileHistory.setState(ProfileStateEnum.PROCESSING.getValue());
                        // }
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
            Boolean isAsc = false;
            ProfileDTO old = item.getProfile();
            ProfileHistoryDTO profileHistory = new ProfileHistoryDTO();
            profileHistory.setTimeReceived(LocalDateTime.now());
            UserDTO user = userService.findByUsername(item.getUsername());

            if (DataUtils.isNullObject(user)) {
                throw new ResourceNotFoundException("User " + item.getUsername() + " not found");
            }
            DepartmentDTO department = departmentService.findByCode(item.getCode());
            if (DataUtils.isNullObject(department)) {
                throw new ResourceNotFoundException("department " + department.getCode() + " not found");
            }

            TransactionTypeDTO transactionType = transactionTypeService
                    .findById(Long.parseLong(old.getType().toString()));
            if (DataUtils.isNullObject(transactionType)) {
                throw new ResourceNotFoundException("transaction Type " + old.getType().toString() + " not found");
            }
            // save staffId when create profile
            if (item.getCode().equals("QLKH")) {
                old.setStaffId(user.getId());
            }

            // không reset lại thời gian cho QTTD nữa, reset cho GDKH và update hồ sơ chờ
            // thành đang xử lý
            if (old.getState().equals(ProfileStateEnum.ADDITIONAL.getValue())) {

                isAsc = true;
                Map<String, Object> params = new HashMap<>();

                params.put("state", ProfileStateEnum.WAITING.getValue());
                params.put("code", item.getCode());
                List<ProfileDTO> listDataWaiting = new ArrayList<>();
                switch (item.getCode().trim().toUpperCase()) {
                    case "GDKH":
                        old.setTimeReceived_CT(null);
                        break;
                    case "QTTD":
                        params.put("staffId_CM", user.getId());
                        params.put("staffId_CT", "NULL");
                        // tính thời gian còn lại để cộng vào lần bàn giao sau cho hồ sơ cần bổ sung
                        // bắt đầu từ thời điểm chuyển đổi trạng thái thành cần bổ sung - additional
                        LocalDateTime from = old.getTimeReceived_CM();
                        LocalDateTime to = LocalDateTime.now();
                        LocalDateTime processTime = old.getProcessDate();
                        if (!to.isAfter(from) && !processTime.isAfter(to)) {
                            Long additionalTime = DataUtils.durationToMinute(from, processTime);
                            old.setAdditionalTime(Integer.valueOf(additionalTime.intValue()));
                            old.setProcessDate(to);
                        } else {

                        }
                        listDataWaiting = repository.getProfileWithParams(params, isAsc);
                        this.updateProfileList(listDataWaiting, old, user, profileHistory, department.getId(),
                                item.getCode(), transactionType.getType());

                        break;

                    default:
                        break;
                }

            }
            profileHistory.setDepartmentCode(department.getCode());
            profileHistory.setDepartmentId(department.getId());
            // profileHistory.setTimeReceived(LocalDateTime.now());
            profileHistory.setStaffId(user.getId());
            profileHistory.setState(old.getState());

            ProfileDTO dto = save(old);

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

    @Override
    public MessageDTO checkScanAgain(ConfirmRequest item) {
        // TODO Auto-generated method stub
        MessageDTO message = new MessageDTO();
        try {
            ProfileDTO dto = findById(item.getProfile().getId());
            ProfileDTO old = item.getProfile();
            Boolean isExist = false;
            if (!DataUtils.isNullOrEmpty(dto)) {
                TransactionTypeDTO transactionType = transactionTypeService
                        .findById(Long.parseLong(dto.getType().toString()));

                if (DataUtils.isNullOrEmpty(transactionType)) {
                    throw new ResourceNotFoundException(
                            "transaction Type " + dto.getType().toString() + " not found");
                }

                UserDTO user = userService.findByUsername(item.getUsername());
                if (DataUtils.isNullOrEmpty(user)) {
                    throw new ResourceNotFoundException("User " + item.getUsername() + " not found");
                }

                DepartmentDTO deparment = departmentService.findByCode(item.getCode());
                if (DataUtils.isNullOrEmpty(deparment)) {
                    throw new ResourceNotFoundException("Deparment " + item.getCode() + " not found");
                }

                if (transactionType.getType().equals(1)) {
                    switch (item.getCode()) {
                        case "QTTD":
                            // check scan
                            if (!DataUtils.isNullOrEmpty(dto.getStaffId_CM())) {
                                if (dto.getState().equals(ProfileStateEnum.PROCESSING.getValue())
                                        || dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                    if (!item.getIsFinished()) {
                                        if (old.getState().equals(ProfileStateEnum.PROCESSING.getValue())
                                                || dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                            message.setMessage("Bạn đã nhận giao dịch này 1 lần");
                                            message.setIsExist(true);
                                        } else {
                                            message.setIsExist(false);

                                        }
                                    } else {
                                        message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                        message.setIsExist(true);
                                    }

                                } else if (dto.getState().equals(ProfileStateEnum.FINISHED.getValue())) {
                                    message.setMessage("Bạn đã kết thúc giao dịch này");
                                    message.setIsExist(true);

                                } else if (dto.getState().equals(ProfileStateEnum.ADDITIONAL.getValue())) {
                                    if (item.getIsFinished()) {
                                        message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                                        message.setIsExist(true);
                                    } else {
                                        message.setIsExist(false);
                                    }

                                }

                            } else {
                                if (!item.getIsFinished()) {
                                    message.setIsExist(false);
                                } else {
                                    message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                                    message.setIsExist(true);
                                }
                            }
                            break;
                        case "GDKH":
                            if (!DataUtils.isNullOrEmpty(dto.getStaffId_CM())) {
                                // check delivery to GDKH
                                // state of profile: not_yet, tranfer
                                Integer[] intArray = new Integer[] { 6 };
                                if (DataUtils.isNullOrEmpty(dto.getStaffId_CT())) {
                                    if (Arrays.asList(intArray).contains(dto.getState())) {
                                        message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                                        message.setIsExist(true);
                                    } else if (dto.getState().equals(ProfileStateEnum.PROCESSING.getValue())
                                            || dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                        // nhận bàn giao từ QTTD tới máy chung - admin
                                        if (item.getUsername().contains("admin")) {
                                            if (item.getIsFinished()) {
                                                message.setMessage(
                                                        "Không thể kết thúc giao dịch do cán bộ chưa nhận hồ sơ");
                                                message.setIsExist(true);
                                            } else {
                                                message.setIsExist(false);
                                            }
                                        } else {
                                            // cán bộ GDKH quét QR nhầm
                                            message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                            message.setIsExist(true);
                                        }
                                    } else if (dto.getState().equals(ProfileStateEnum.RECEIVED.getValue())) {
                                        if (item.getUsername().contains("admin")) {
                                            if (item.getIsFinished()) {
                                                message.setMessage(
                                                        "Không thể kết thúc giao dịch do cán bộ chưa nhận hồ sơ");
                                                message.setIsExist(true);
                                            } else {
                                                message.setMessage("Bạn đã nhận giao dịch này 1 lần");
                                                message.setIsExist(true);
                                            }
                                        } else {
                                            // cán bộ GDKH quét QR nhầm
                                            if (item.getIsFinished()) {
                                                message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                                message.setIsExist(true);
                                            } else {
                                                message.setIsExist(false);
                                            }
                                        }
                                    }
                                } else {
                                    if (dto.getState().equals(ProfileStateEnum.PROCESSING.getValue())
                                            || dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                        // check scan
                                        if (old.getState().equals(ProfileStateEnum.PROCESSING.getValue())
                                                || dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                            if (item.getUsername().contains("admin")) {
                                                message.setIsExist(false);
                                            } else {
                                                message.setMessage("Bạn đã nhận giao dịch này 1 lần");
                                                message.setIsExist(true);
                                            }

                                        } else {
                                            message.setIsExist(false);

                                        }

                                    } else if (dto.getState().equals(ProfileStateEnum.FINISHED.getValue())) {
                                        message.setMessage("Bạn đã kết thúc giao dịch này");
                                        message.setIsExist(true);

                                    } else if (dto.getState().equals(ProfileStateEnum.ADDITIONAL.getValue())) {
                                        if (item.getIsFinished()) {
                                            message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                            message.setIsExist(true);
                                        } else {
                                            message.setIsExist(false);
                                        }
                                    }
                                }

                            } else {
                                message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                                message.setIsExist(true);
                            }

                            break;
                        default:
                            message.setMessage("Tài khoản của bạn không phù hợp với luồng giao dịch");
                            message.setIsExist(true);
                            break;
                    }

                }
                if (transactionType.getType().equals(2)) {
                    // QTTD không tính thời gian cho phòng, do đó bàn giao thẳng cho chuyên viên
                    if (item.getCode().equals("QTTD")) {
                        // kiểm tra đã bàn giao tại QTTD chưa
                        if (!DataUtils.isNullOrEmpty(dto.getStaffId_CM())) {
                            if (dto.getState().equals(ProfileStateEnum.PROCESSING.getValue())
                                    || dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                if (item.getIsFinished()) {
                                    message.setIsExist(false);
                                } else {
                                    if (old.getState().equals(ProfileStateEnum.PROCESSING.getValue())
                                            || dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                        message.setMessage("Bạn đã nhận giao dịch này 1 lần");
                                        message.setIsExist(true);
                                    } else {
                                        message.setIsExist(false);

                                    }
                                }

                            } else if (dto.getState().equals(ProfileStateEnum.FINISHED.getValue())) {
                                message.setMessage("Bạn đã kết thúc giao dịch này");

                                message.setIsExist(true);

                            } else if (dto.getState().equals(ProfileStateEnum.ADDITIONAL.getValue())) {
                                if (item.getIsFinished()) {
                                    message.setMessage("Bàn giao tại quản trị tín dụng");
                                    message.setIsExist(true);
                                } else {
                                    message.setIsExist(false);
                                }
                            }
                        } else {
                            // return false => cho phép bàn giao
                            // message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                            message.setIsExist(false);
                        }
                    } else {
                        message.setMessage("Tài khoản của bạn không phù hợp với luồng giao dịch");
                        message.setIsExist(true);
                    }

                }
                if (transactionType.getType().equals(3)) {
                    if (item.getCode().equals("GDKH")) {
                        if (!DataUtils.isNullOrEmpty(dto.getStaffId_CT())) {
                            if (dto.getState().equals(ProfileStateEnum.PROCESSING.getValue())
                                    || dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                if (item.getIsFinished()) {
                                    message.setIsExist(false);
                                } else {
                                    if (old.getState().equals(ProfileStateEnum.PROCESSING.getValue())
                                            || dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                        message.setMessage("Bạn đã nhận giao dịch này 1 lần");
                                        message.setIsExist(true);
                                    } else {
                                        message.setIsExist(false);

                                    }
                                }

                            } else if (dto.getState().equals(ProfileStateEnum.FINISHED.getValue())) {
                                message.setMessage("Bạn đã kết thúc giao dịch này");
                                message.setIsExist(true);
                            } else if (dto.getState().equals(ProfileStateEnum.ADDITIONAL.getValue())) {
                                if (item.getIsFinished()) {
                                    message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                    message.setIsExist(true);
                                } else {
                                    message.setIsExist(false);
                                }
                            }
                        } else {
                            // kiểm tra cán bộ GDKH đã quét chưa
                            if (!DataUtils.isNullOrEmpty(dto.getTimeReceived_CT())) {
                                message.setIsExist(false);
                            } else {
                                // kiểm tra đã quét tại phòng GDKH chưa
                                if (item.getUsername().contains("admin")) {
                                    message.setIsExist(false);
                                } else {
                                    message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                    message.setIsExist(true);
                                }
                            }
                        }

                    } else {
                        message.setMessage("Tài khoản của bạn không phù hợp với luồng giao dịch");
                        message.setIsExist(true);
                    }

                }

            }
            return message;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            message.setMessage(e.getMessage());
            message.setIsExist(true);
            return message;
        }
    }

    @Override
    public MessageDTO checkIsReturn(ConfirmRequest item) {
        MessageDTO message = new MessageDTO();
        try {
            ProfileDTO dto = findById(item.getProfile().getId());

            if (!DataUtils.isNullOrEmpty(dto)) {
                TransactionTypeDTO transactionType = transactionTypeService
                        .findById(Long.parseLong(dto.getType().toString()));

                if (DataUtils.isNullOrEmpty(transactionType)) {
                    throw new ResourceNotFoundException(
                            "transaction Type " + dto.getType().toString() + " not found");
                }

                UserDTO user = userService.findByUsername(item.getUsername());
                if (DataUtils.isNullOrEmpty(user)) {
                    throw new ResourceNotFoundException("User " + item.getUsername() + " not found");
                }

                DepartmentDTO deparment = departmentService.findByCode(item.getCode());
                if (DataUtils.isNullOrEmpty(deparment)) {
                    throw new ResourceNotFoundException("Deparment " + item.getCode() + " not found");
                }
                // state of profile: not_yet, tranfer
                Integer[] intArray = new Integer[] { 0, 1 };
                Integer[] intArray2 = new Integer[] { 6 };
                if (transactionType.getType().equals(1)) {

                    switch (item.getCode()) {
                        case "QTTD":
                            if (!DataUtils.isNullOrEmpty(dto.getStaffId_CM())) {

                                if (dto.getState().equals(ProfileStateEnum.FINISHED.getValue())) {
                                    message.setMessage("Bạn đã kết thúc giao dịch này, không thể trả hồ sơ");
                                    message.setIsExist(true);
                                } else if (Arrays.asList(intArray).contains(dto.getState())) {
                                    message.setMessage("Hồ sơ chưa được bàn giao, chuyển");
                                    message.setIsExist(true);
                                } else if (dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                    message.setMessage("Chưa thể trả hồ sơ do hồ sơ chưa được xử lý");
                                    message.setIsExist(true);
                                } else {
                                    message.setIsExist(false);
                                }

                            } else {
                                message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                                message.setIsExist(true);
                            }
                            break;
                        case "GDKH":
                            if (DataUtils.isNullOrEmpty(dto.getStaffId_CM())) {
                                message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                                message.setIsExist(true);
                            } else {
                                if (!DataUtils.isNullOrEmpty(dto.getStaffId_CT())) {
                                    if (dto.getState().equals(ProfileStateEnum.FINISHED.getValue())) {
                                        message.setMessage("Bạn đã kết thúc giao dịch này, không thể trả hồ sơ");
                                        message.setIsExist(true);
                                    } else if (Arrays.asList(intArray).contains(dto.getState())) {
                                        message.setMessage("Hồ sơ chưa được bàn giao, chuyển");
                                        message.setIsExist(true);
                                    } else if (dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                        message.setMessage("Chưa thể trả hồ sơ do hồ sơ chưa được xử lý");
                                        message.setIsExist(true);
                                    } else {
                                        message.setIsExist(false);
                                    }
                                } else {
                                    if (Arrays.asList(intArray2).contains(dto.getState())) {
                                        message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                                        message.setIsExist(true);
                                    } else if (dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                        message.setMessage("Chưa thể trả hồ sơ do hồ sơ chưa được xử lý");
                                        message.setIsExist(true);
                                    } else if (dto.getState().equals(ProfileStateEnum.PROCESSING.getValue())) {
                                        if (DataUtils.isNullOrEmpty(dto.getTimeReceived_CT())) {
                                            message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                            message.setIsExist(true);

                                        } else {
                                            message.setIsExist(false);
                                        }
                                    }
                                }
                            }

                            break;
                        default:
                            message.setMessage("Tài khoản của bạn không phù hợp với luồng giao dịch");
                            message.setIsExist(true);
                            break;
                    }
                }

                if (transactionType.getType().equals(2)) {
                    // state of profile: not_yet, tranfer
                    if (item.getCode().equals("QTTD")) {
                        if (!DataUtils.isNullOrEmpty(dto.getStaffId_CM())) {
                            if (dto.getState().equals(ProfileStateEnum.FINISHED.getValue())) {
                                message.setMessage("Bạn đã kết thúc giao dịch này, không thể trả hồ sơ");
                                message.setIsExist(true);
                            } else if (Arrays.asList(intArray).contains(dto.getState())) {
                                message.setMessage("Hồ sơ chưa được bàn giao, chuyển");
                                message.setIsExist(true);
                            } else if (dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                message.setMessage("Chưa thể trả hồ sơ do hồ sơ chưa được xử lý");
                                message.setIsExist(true);
                            } else {
                                message.setIsExist(false);
                            }
                        } else {
                            message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                            message.setIsExist(true);
                        }
                    } else {
                        message.setMessage("Tài khoản của bạn không phù hợp với luồng giao dịch");
                        message.setIsExist(true);
                    }

                }
                if (transactionType.getType().equals(3)) {
                    if (item.getCode().equals("GDKH")) {
                        if (!DataUtils.isNullOrEmpty(dto.getStaffId_CT())) {
                            if (dto.getState().equals(ProfileStateEnum.FINISHED.getValue())) {
                                message.setMessage("Bạn đã kết thúc giao dịch này, không thể trả hồ sơ");
                                message.setIsExist(true);
                            } else if (Arrays.asList(intArray).contains(dto.getState())) {
                                message.setMessage("Hồ sơ chưa được bàn giao, chuyển");
                                message.setIsExist(true);
                            } else if (dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                message.setMessage("Chưa thể trả hồ sơ do hồ sơ chưa được xử lý");
                                message.setIsExist(true);
                            } else {
                                message.setIsExist(false);
                            }
                        } else {
                            if (item.getIsReturned()) {
                                message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                message.setIsExist(true);
                            } else {
                                message.setIsExist(false);
                            }
                        }
                    } else {
                        message.setMessage("Tài khoản của bạn không phù hợp với luồng giao dịch");
                        message.setIsExist(true);
                    }

                }
            }
            return message;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            // message.setMessage(e.getMessage());
            // message.setIsExist(true);
            return null;
        }
    }

    @Override
    public MessageDTO priorityProfile(ConfirmRequest item) {
        MessageDTO message = new MessageDTO();

        try {
            ProfileDTO dto = findById(item.getProfile().getId());
            if (!DataUtils.isNullOrEmpty(dto)) {

                TransactionTypeDTO transactionType = transactionTypeService
                        .findById(Long.parseLong(dto.getType().toString()));

                if (DataUtils.isNullOrEmpty(transactionType)) {
                    throw new ResourceNotFoundException(
                            "transaction Type " + dto.getType().toString() + " not found");
                }

                List<Profile> process = repository.findBySateAndStaffId(
                        ProfileStateEnum.PROCESSING.getValue(), dto.getStaffId_CM());

                // LocalDateTime timeReceived = LocalDateTime.now();
                LocalDateTime processDate = null;

                if (process.size() > 0) {
                    // first record
                    if (item.getCode().trim().toUpperCase().equals("QTTD")) {
                        LocalDateTime from = dto.getTimeReceived_CM();
                        LocalDateTime to = dto.getProcessDate();

                        LocalDateTime timeReceived = process.get(0).getProcessDate();
                        processDate = DataUtils.calculatingDate(from, to, timeReceived);
                        // save dto
                        dto.setTimeReceived_CM(process.get(0).getProcessDate());
                        dto.setProcessDate(processDate);
                        save(dto);

                        // get list after saving dto
                        List<Profile> listData = repository.findBySateAndStaffIdAndIgnore(
                                ProfileStateEnum.WAITING.getValue(), dto.getStaffId_CM(), dto.getId());

                        // update processDate for all list
                        // int i = 0;
                        if (listData.size() > 0) {

                            for (int i = 0; i < listData.size(); i++) {
                                // first record update by dto (priority)
                                if (i == 0) {
                                    Profile first = listData.get(i);
                                    LocalDateTime fromFirst = first.getTimeReceived_CM();
                                    LocalDateTime toFirst = first.getProcessDate();
                                    LocalDateTime timeReceivedOfSecond = dto.getProcessDate();
                                    LocalDateTime date = DataUtils.calculatingDate(fromFirst, toFirst,
                                            timeReceivedOfSecond);
                                    first.setTimeReceived_CM(dto.getProcessDate());
                                    first.setProcessDate(date);
                                    first.setLastUpdatedDate(LocalDateTime.now());
                                    repository.save(first);

                                } else {
                                    Profile first = listData.get(i - 1);
                                    Profile second = listData.get(i);
                                    // processDate: hours, minutes
                                    LocalDateTime fromFirst = second.getTimeReceived_CM();
                                    LocalDateTime toFirst = second.getProcessDate();
                                    LocalDateTime timeReceivedOfSecond = first.getProcessDate();
                                    LocalDateTime date = DataUtils.calculatingDate(fromFirst, toFirst,
                                            timeReceivedOfSecond);
                                    // end
                                    second.setTimeReceived_CM(timeReceivedOfSecond);
                                    second.setProcessDate(date);
                                    second.setLastUpdatedDate(LocalDateTime.now());
                                    repository.save(second);
                                }
                            }
                        }
                        message.setMessage("");
                        message.setIsExist(false);
                    }

                }
            }
            return message;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            message.setMessage(e.getMessage());
            message.setIsExist(true);
            return message;

        }

    }

    private void updateProfileList(List<ProfileDTO> listData, ProfileDTO profile, UserDTO user,
            ProfileHistoryDTO profileHistory, Long departmentId, String code, Integer transactionType) {
        try {
            if (listData.size() >= 1) {

                for (int i = 0; i < listData.size(); i++) {
                    // first record update by profile
                    if (i == 0) {
                        // first waiting profile
                        ProfileDTO first = listData.get(i);
                        LocalDateTime fromFirst = first.getTimeReceived_CM();
                        LocalDateTime toFirst = first.getProcessDate();
                        LocalDateTime timeReceivedOfSecond = LocalDateTime.now();

                        // (timeReceived - profileHistory) = (endtime - profile)
                        // boolean isAfter =
                        // profileHistory.getTimeReceived().isAfter(profile.getProcessDate());
                        timeReceivedOfSecond = profile.getProcessDate();
                        // if (isAfter) {
                        // timeReceivedOfSecond = profile.getProcessDate();
                        // } else {
                        // timeReceivedOfSecond = profileHistory.getTimeReceived();
                        // }
                        LocalDateTime date = DataUtils.calculatingDate(fromFirst, toFirst,
                                timeReceivedOfSecond);
                        first.setTimeReceived_CM(timeReceivedOfSecond);
                        first.setProcessDate(date);
                        first.setLastUpdatedDate(LocalDateTime.now());

                        // update state for first waiting profile
                        first.setState(ProfileStateEnum.PROCESSING.getValue());

                        // save history
                        ProfileHistoryDTO his = new ProfileHistoryDTO();
                        switch (transactionType) {
                            case 1:
                                // at QTTD
                                if (code.trim().toUpperCase().equals("QTTD")) {

                                    his.setStaffId(first.getStaffId_CM());
                                }
                                // at GDKH
                                if (code.trim().toUpperCase().equals("GDKH")) {
                                    if (user.getUsername().trim().toLowerCase().contains("admin")) {
                                        his.setStaffId(first.getStaffId_CM());
                                    } else {
                                        his.setStaffId(first.getStaffId_CT());
                                    }
                                }
                                break;
                            case 2:
                                his.setStaffId(first.getStaffId_CM());
                                break;
                            case 3:
                                his.setStaffId(first.getStaffId_CT());
                                break;
                            default:
                                break;
                        }

                        // his.
                        UserDTO userDTO = userService.findById(his.getStaffId());
                        if (DataUtils.isNullOrEmpty(userDTO)) {
                            throw new ResourceNotFoundException("User " + userDTO.getUsername() + " not found");
                        }
                        DepartmentDTO deparment = departmentService.findById(userDTO.getDepartmentId());
                        if (DataUtils.isNullOrEmpty(deparment)) {
                            throw new ResourceNotFoundException("Deparment " + deparment.getCode() + " not found");
                        }

                        his.setTimeReceived(LocalDateTime.now());
                        his.setDepartmentId(deparment.getId());
                        his.setProfileId(first.getId());
                        his.setState(first.getState());
                        profileHistoryService.save(his);
                        save(first);

                    } else {
                        ProfileDTO first = listData.get(i - 1);
                        ProfileDTO second = listData.get(i);
                        // processDate: hours, minutes
                        LocalDateTime fromFirst = second.getTimeReceived_CM();
                        LocalDateTime toFirst = second.getProcessDate();
                        LocalDateTime timeReceivedOfSecond = first.getProcessDate();
                        LocalDateTime date = DataUtils.calculatingDate(fromFirst, toFirst,
                                timeReceivedOfSecond);
                        // end
                        second.setTimeReceived_CM(timeReceivedOfSecond);
                        second.setProcessDate(date);
                        second.setLastUpdatedDate(LocalDateTime.now());
                        save(second);
                    }
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

}
