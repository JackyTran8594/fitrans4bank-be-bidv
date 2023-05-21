package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.DepartmentDTO;
import com.eztech.fitrans.dto.response.MessageDTO;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import com.eztech.fitrans.dto.response.TransactionTypeDTO;
import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.dto.response.dashboard.DashboardDTO;
import com.eztech.fitrans.dto.response.dashboard.ProfileListDashBoardDTO;
import com.eztech.fitrans.event.ScheduledTasks;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.Profile;
import com.eztech.fitrans.model.ProfileHistory;
import com.eztech.fitrans.repo.ActionLogRepository;
import com.eztech.fitrans.repo.ProfileRepository;
import com.eztech.fitrans.service.DepartmentService;
import com.eztech.fitrans.service.ProfileHistoryService;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.service.TransactionTypeService;
import com.eztech.fitrans.service.UserService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.CalculatingTime;
import com.eztech.fitrans.util.DataUtils;
import com.eztech.fitrans.util.ReadAndWriteDoc;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Value("${app.timeConfig}")
    private Double timeConfig;

    @Autowired
    private ProfileRepository repository;

    @Autowired
    private CalculatingTime calculatingTime;

    @Autowired
    private ActionLogRepository actionLogRepository;

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
        DepartmentDTO department = departmentService.findById(user.getDepartmentId());
        if (DataUtils.isNullObject(department)) {
            throw new ResourceNotFoundException("department " + item.getCode() + " not found");
        }
        ProfileHistoryDTO profileHistory = new ProfileHistoryDTO();

        TransactionTypeDTO transactionType = transactionTypeService
                .findById(Long.parseLong(profile.getType().toString()));
        if (DataUtils.isNullObject(transactionType)) {
            throw new ResourceNotFoundException("transaction Type " + profile.getType().toString() + " not found");
        }

        // kiểm tra hồ sơ được tạo mới hay update - không lưu hồ sơ mà chuyển luôn
        if (DataUtils.isNullObject(item.profile.getCreatedDate())) {
            profile.setCreatedDate(LocalDateTime.now());
        }
        profileHistory.setTimeReceived(LocalDateTime.now());

        LocalDateTime timeMarkerValue = calculatingTime.convertTimeMarker(timeConfig);

        try {
            // kiểm tra account admin - GDKH
            Boolean isAsc = false;

            if (item.username.toLowerCase().contains("admin")) {
                if (item.getCode().equals("GDKH")) {

                    // lấy các hồ sơ đang có trạng thái chờ xử lý (order by process_date) - QTTD
                    Map<String, Object> params = new HashMap<>();
                    params.put("state", ProfileStateEnum.WAITING.getValue());
                    params.put("staffId_CM", profile.getStaffId_CM());
                    isAsc = true;
                    List<ProfileDTO> listData = new ArrayList<>();
                    // kiểm tra hồ sơ được bàn giao chưa
                    if (profile.getState().equals(ProfileStateEnum.ADDITIONAL.getValue())) {

                    } else if (profile.getState().equals(ProfileStateEnum.PROCESSING.getValue())) {
                        // type 1,2 - QTTD
                        // cập nhật listWaiting
                        // params.put("staffId_CT", "NULL");
                        params.put("timeReceived_CT", "NULL");

                        // kiểm tra xem ngày scan có phải ngày hôm nay không
                        // nếu có chỉ lấy ngày hôm nay làm mốc, nếu không thì lấy ngày scan làm mốc để
                        // update thời gian cho các hồ sơ

                        if (profile.getRealTimeReceivedCM().getDayOfMonth() == timeMarkerValue.getDayOfMonth()) {
                            if (profile.getRealTimeReceivedCM().isBefore(timeMarkerValue)) {
                                // chỉ update những hồ sơ bàn giao trước 16h
                                params.put("isToday", true);
                            }
                        }

                        // lấy các hồ sơ chờ
                        listData = repository.getProfileWithParams(params, isAsc);

                        if (listData.size() > 0) {
                            // update các hồ sơ của quản trị tín dụng
                            this.updateProfileList(listData, profile, user, profileHistory, department.getId(),
                                    item.getCode(),
                                    transactionType);
                        } else {
                            // nếu không có hồ sơ chờ

                            // kiểm tra xem ngày scan có phải ngày hôm nay không - bước này để check lại
                            // list hồ sơ chờ của ngày hôm sau (bàn giao sau 16h)
                            // nếu có chỉ lấy ngày hôm nay làm mốc
                            // nếu không phải ngày hôm nay và ko có hồ sơ chờ thì không làm gì cả

                            Map<String, Object> paramsWaiting = new HashMap<>();
                            List<ProfileDTO> listDataWaittingTomorrow = new ArrayList<>();
                            paramsWaiting.put("state", ProfileStateEnum.WAITING.getValue());
                            paramsWaiting.put("staffId_CM", profile.getStaffId_CM());
                            // paramsWaiting.put("staffId_CT", "NULL");
                            paramsWaiting.put("timeReceived_CT", "NULL");

                            if (profile.getRealTimeReceivedCM().getDayOfMonth() == timeMarkerValue
                                    .getDayOfMonth()) {
                                if (profile.getRealTimeReceivedCM().isAfter(timeMarkerValue)) {
                                    // các hồ sơ chờ của ngày hôm sau
                                    // chỉ update trạng thái xử lý với hồ sơ đầu tiên
                                    paramsWaiting.put("isToday", false);
                                }
                            }

                            listDataWaittingTomorrow = repository.getProfileWithParams(paramsWaiting, isAsc);
                            if (listDataWaittingTomorrow.size() > 0) {
                                ProfileDTO profileProcessingTomorrow = listDataWaittingTomorrow.get(0);
                                profileProcessingTomorrow.setState(ProfileStateEnum.PROCESSING.getValue());
                                save(profileProcessingTomorrow);
                            }

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

                // lưu kết quả của timeReceived và processTime;
                Map<String, Object> mapResult = new HashMap<>();

                params.put("state", ProfileStateEnum.PROCESSING.getValue());

                // checking: transaction is finished
                if (item.getIsFinished()) {

                    // update first row is processing
                    params.put("state", ProfileStateEnum.WAITING.getValue());
                    params.put("code", item.getCode());

                    // kiểm tra xem có thuộc luồng chưa giải quyết không
                    // luông chưa giải quyết thì save riêng và không update những bản ghi đằng sau
                    if (profile.getState().equals(ProfileStateEnum.PENDING.getValue())) {

                    } else {
                        Map<String, Object> paramsWaiting = new HashMap<>();
                        Map<String, Object> paramsWaitingTomorrow = new HashMap<>();
                        paramsWaiting.put("state", ProfileStateEnum.WAITING.getValue());

                        List<ProfileDTO> listProfileWaiting = new ArrayList<>();

                        switch (item.getCode()) {
                            // transaction type : 1,2
                            // type 1: QTTD không kết thúc giao dịch
                            // type 2 :QTTD kết thúc giao dịch, do đó không có staffId_CT
                            case "QTTD":
                                paramsWaiting.put("staffId_CM", user.getId());
                                // paramsWaiting.put("staffId_CT", "NULL");
                                paramsWaiting.put("timeReceived_CT", "NULL");

                                // kiểm tra xem ngày scan có phải ngày hôm nay không
                                // nếu có chỉ lấy ngày hôm nay làm mốc, nếu không thì lấy ngày scan làm mốc để
                                // update thời gian cho các hồ sơ
                                if (profile.getRealTimeReceivedCM().getDayOfMonth() == timeMarkerValue
                                        .getDayOfMonth()) {
                                    if (profile.getRealTimeReceivedCM().isBefore(timeMarkerValue)) {
                                        // chỉ update những hồ sơ bàn giao trước 16h
                                        paramsWaiting.put("isToday", true);
                                    }
                                }

                                // sort theo process_date tăng dần và lấy thằng đầu tiên để update
                                isAsc = true;
                                // lọc ra các hồ sơ/ các hồ sơ trong ngày
                                listProfileWaiting = repository.getProfileWithParams(paramsWaiting, isAsc);

                                if (listProfileWaiting.size() > 0) {
                                    this.updateProfileList(listProfileWaiting, profile, user, profileHistory,
                                            department.getId(),
                                            item.getCode(), transactionType);
                                } else {

                                    // nếu không có hồ sơ chờ

                                    // kiểm tra xem ngày scan có phải ngày hôm nay không - bước này để check lại
                                    // list hồ sơ chờ của ngày hôm sau (bàn giao sau 16h)
                                    // nếu có chỉ lấy ngày hôm nay làm mốc
                                    // nếu không phải ngày hôm nay và ko có hồ sơ chờ thì không làm gì cả

                                    List<ProfileDTO> listProfileWaitingTomorrow = new ArrayList<>();
                                    paramsWaitingTomorrow.put("state", ProfileStateEnum.WAITING.getValue());
                                    paramsWaitingTomorrow.put("staffId_CM", user.getId());
                                    // paramsWaitingTomorrow.put("staffId_CT", "NULL");
                                    paramsWaitingTomorrow.put("timeReceived_CT", "NULL");
                                    // lọc ra các hồ sơ chờ ngày hôm sau
                                    // isToday = false
                                    // profileHistory.getTimeReceived()

                                    if (profile.getRealTimeReceivedCM().isAfter(timeMarkerValue)) {

                                        if (profile.getRealTimeReceivedCM().getDayOfMonth() == timeMarkerValue
                                                .getDayOfMonth()) {
                                            paramsWaitingTomorrow.put("isToday", false);

                                        }

                                    }

                                    listProfileWaitingTomorrow = repository.getProfileWithParams(
                                            paramsWaitingTomorrow,
                                            isAsc);
                                    // các hồ sơ chờ của ngày hôm sau
                                    // chỉ update trạng thái xử lý với hồ sơ đầu tiên
                                    if (listProfileWaitingTomorrow.size() > 0) {
                                        ProfileDTO profileProcessingTomorrow = listProfileWaitingTomorrow
                                                .get(0);
                                        profileProcessingTomorrow
                                                .setState(ProfileStateEnum.PROCESSING.getValue());
                                        save(profileProcessingTomorrow);
                                    }

                                }

                                break;
                            case "GDKH":
                                // GDKH ko có chờ xử lý

                                break;
                        }
                    }
                    profile.setState(ProfileStateEnum.FINISHED.getValue());
                    profileHistory.setState(ProfileStateEnum.FINISHED.getValue());
                    profile.setEndTime(profileHistory.getTimeReceived());

                } else {
                    // nếu không phải trạng thái kết thúc thì hồ sơ đối với QTTD có trạng thái: đang
                    // xử lý, chờ xử lý đối với hàm này
                    if (item.getCode().equals("QTTD")) {

                        item.getProfile().setStaffId_CM(user.getId());
                        params.put("staffId_CM", user.getId());
                        params.put("timeReceived_CT", "NULL");
                        // params.put("staffId_CT", "NULL");
                        // calculating time for processing time for
                        LocalDateTime processTime = LocalDateTime.now();

                        // danh sách hồ sơ đang xử lý - thường chỉ là 1
                        List<ProfileDTO> listData = repository.getProfileWithParams(params, isAsc);

                        // checking process of profile : processing
                        if (listData.size() == 1) {
                            ProfileDTO profile_first = new ProfileDTO();
                            // update lại state trong hashmap
                            params.put("state", ProfileStateEnum.WAITING.getValue());

                            // kiểm tra thời gian nhận có phải hôm nay không

                            // if (DataUtils.notNull(profile.getRealTimeReceivedCM())) {
                            ProfileDTO profileProcessing = listData.get(0);
                            // nếu thời gian xử lý của hồ sơ đang xử lý là trong ngày thực tế tồn tại: bàn
                            // giao lại trong th trả hồ sơ
                            if (profileProcessing.getRealTimeReceivedCM().getDayOfMonth() == timeMarkerValue
                                    .getDayOfMonth()) {
                                // kiểm tra xem thời gian xử lý có vượt sang ngày hôm sau không
                                // nếu không thì check trong ngày : bàn giao trước và sau 16h
                                if (profileProcessing.getProcessDate().getDayOfMonth() == profileHistory
                                        .getTimeReceived()
                                        .getDayOfMonth()) {
                                    if (profileHistory.getTimeReceived().isBefore(timeMarkerValue)) {
                                        // isToday = true => tìm tất cả các hồ sơ chờ trong ngày
                                        params.put("isToday", true);
                                    }
                                    // else {
                                    // // isToday = false => tìm tất cả các hồ sơ chờ từ 8h ngày hôm sau
                                    // params.put("isToday", false);
                                    // }
                                    if (profileHistory.getTimeReceived().isAfter(timeMarkerValue)) {
                                        // isToday = true => tìm tất cả các hồ sơ chờ trong ngày
                                        params.put("isToday", false);
                                    }
                                }

                            }
                            // }

                            // isAsc = false
                            List<ProfileDTO> listDataWaiting = repository.getProfileWithParams(params, isAsc);
                            // nếu hồ sơ chờ lớn hơn 1
                            if (listDataWaiting.size() >= 1) {
                                // check time received
                                profile_first = listDataWaiting.get(0);
                                // timeReceivedProfile = profile_first.getProcessDate();

                            } else {
                                // chính là hồ sơ đang xử lý trong ngày
                                profile_first = listData.get(0);
                            }

                            // kiểm tra xem thời gian nhận có vượt 16h hôm nay ko để chọn hàm tính thời gian
                            // ngày của thời gian scan và thời gian marker luôn bằng nhau
                            if (profileHistory.getTimeReceived().isBefore(timeMarkerValue)) {
                                // kiểm tra xem thời gian xử lý (ngày xử lý) của hồ ơ đang xử lý có nằm sau
                                // timeMarkerValue không, nếu có thì trường hợp này là đang xử lý vào
                                // ngày hôm sau (8h sáng)
                                // nếu thời gian xử lý trong ngày
                                // tính toán theo hàm trong ngày
                                int dayOfMonth_profile = profile_first.getRealTimeReceivedCM().getDayOfMonth();
                                int dayOfMonth_marker = profileHistory.getTimeReceived().getDayOfMonth();

                                // kiểm tra xem có scan trong ngày với thời gian thực nhận không
                                // có: thời gian nhận bằng thời gian của hồ sơ liên trước đó (đang xử lý/chờ xử
                                // lý)
                                // không: thời gian nhận bằng thời gian hiện tại (thời gian quét hồ sơ)

                                if (dayOfMonth_profile == dayOfMonth_marker) {

                                    mapResult = calculatingTime.calculatingDateFromRealTimeReceived(
                                            profile_first.getProcessDate(), transactionType.getStandardTimeCM(),
                                            transactionType.getStandardTimeChecker(), profile.getAdditionalTime(),
                                            profile.getNumberOfPO(),
                                            profile.getNumberOfBill(), transactionType.getType());
                                } else {

                                    mapResult = calculatingTime.calculatingDateFromRealTimeReceived(
                                            profileHistory.getTimeReceived(), transactionType.getStandardTimeCM(),
                                            transactionType.getStandardTimeChecker(), profile.getAdditionalTime(),
                                            profile.getNumberOfPO(),
                                            profile.getNumberOfBill(), transactionType.getType());
                                }

                            } else {

                                LocalDateTime timeReceivedProfile = null;

                                switch (listDataWaiting.size()) {
                                    case 0:
                                        // thời gian nhận bằng thời gian xử lý của hồ sơ đang xử lý
                                        // timeReceivedProfile = profileHistory.getTimeReceived();
                                        timeReceivedProfile = profile_first.getProcessDate();
                                        // }
                                        break;
                                    default:
                                        // thời gian nhận bằng thời gian scan hồ sơ
                                        timeReceivedProfile = profileHistory.getTimeReceived();

                                        break;
                                }

                                mapResult = calculatingTime.calculatingDateFromTimeReceived(
                                        timeReceivedProfile, transactionType.getStandardTimeCM(),
                                        transactionType.getStandardTimeChecker(), profile.getAdditionalTime(),
                                        profile.getNumberOfPO(),
                                        profile.getNumberOfBill(), transactionType.getType());

                            }

                            profile.setState(ProfileStateEnum.WAITING.getValue());
                            profileHistory.setState(ProfileStateEnum.WAITING.getValue());
                            LocalDateTime timeReceived = (LocalDateTime) mapResult.get("timeReceived");
                            profile.setTimeReceived_CM(timeReceived);
                            processTime = (LocalDateTime) mapResult.get("processTime");

                        }
                        // không có hồ sơ đang xử lý
                        else if (listData.size() == 0) {

                            profile.setState(ProfileStateEnum.PROCESSING.getValue());
                            profileHistory.setState(ProfileStateEnum.PROCESSING.getValue());

                            profile.setTimeReceived_CM(profileHistory.getTimeReceived());

                            // kiểm tra xem thời gian nhận có vượt 16h hôm nay ko để chọn hàm tính thời gian
                            if (profileHistory.getTimeReceived().isBefore(timeMarkerValue)) {
                                mapResult = calculatingTime.calculatingDateFromRealTimeReceived(
                                        profileHistory.getTimeReceived(), transactionType.getStandardTimeCM(),
                                        transactionType.getStandardTimeChecker(), profile.getAdditionalTime(),
                                        profile.getNumberOfPO(),
                                        profile.getNumberOfBill(), transactionType.getType());
                            } else {
                                mapResult = calculatingTime.calculatingDateFromTimeReceived(
                                        profileHistory.getTimeReceived(), transactionType.getStandardTimeCM(),
                                        transactionType.getStandardTimeChecker(), profile.getAdditionalTime(),
                                        profile.getNumberOfPO(),
                                        profile.getNumberOfBill(), transactionType.getType());
                            }

                            LocalDateTime timeReceived = (LocalDateTime) mapResult.get("timeReceived");
                            processTime = (LocalDateTime) mapResult.get("processTime");
                            profile.setTimeReceived_CM(timeReceived);
                        }

                        profile.setProcessDate(processTime);
                        profile.setRealTimeReceivedCM(profileHistory.getTimeReceived());

                    } else if (item.getCode().equals("GDKH")) {
                        // nhận hồ sơ tại GDKH
                        // GDKH không tính thời gian chờ xử lý
                        // nhiều hồ sơ có thể xử lý cùng 1 lúc => do đó ko tính thời gian chờ cho chuyên
                        // viên
                        item.getProfile().setStaffId_CT(user.getId());
                        profile.setState(ProfileStateEnum.PROCESSING.getValue());
                        profileHistory.setState(ProfileStateEnum.PROCESSING.getValue());

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

    /**
     * chỉ dùng trong các trường hợp chuyển hồ sơ - mới tạo, chuyển nội bộ, tạo mới
     * hồ sơ, trả hồ sơ, đưa vào luồng chờ giải quyết
     *
     * @param ConfirmRequest
     */
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
            DepartmentDTO department = departmentService.findById(user.getDepartmentId());
            if (DataUtils.isNullObject(department)) {
                throw new ResourceNotFoundException("department " + department.getCode() + " not found");
            }

            TransactionTypeDTO transactionType = transactionTypeService
                    .findById(Long.parseLong(old.getType().toString()));
            if (DataUtils.isNullObject(transactionType)) {
                throw new ResourceNotFoundException("transaction Type " + old.getType().toString() + " not found");
            }

            LocalDateTime timeMarkerValue = calculatingTime.convertTimeMarker(timeConfig);

            // lưu staffId-QLKH khi tạo mới hồ sơ
            if (item.getCode().equals("QLKH")) {
                old.setStaffId(user.getId());
            }

            // không reset lại thời gian cho QTTD nữa, reset cho GDKH và update hồ sơ chờ
            // thành đang xử lý
            // lưu khi hoàn trả hồ sơ
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

                        // tính thời gian còn lại để cộng vào lần bàn giao sau cho hồ sơ cần bổ sung
                        // bắt đầu từ thời điểm chuyển đổi trạng thái thành cần bổ sung - additional
                        LocalDateTime from = old.getTimeReceived_CM();
                        LocalDateTime to = LocalDateTime.now();
                        LocalDateTime processTime = old.getProcessDate();
                        if (to.isAfter(from) && processTime.isAfter(to)) {
                            Long additionalTime = DataUtils.durationToMinute(from, to);
                            old.setAdditionalTime(Integer.valueOf(additionalTime.intValue()));
                            // old.setProcessDate(to);
                        } else {

                        }

                        params.put("staffId_CM", user.getId());
                        // params.put("staffId_CT", "NULL");
                        params.put("timeReceived_CT", "NULL");

                        // kiểm tra xem hồ sơ có trong ngày hôm nay không
                        if (old.getRealTimeReceivedCM().getDayOfMonth() == timeMarkerValue.getDayOfMonth()) {
                            if (old.getRealTimeReceivedCM().isBefore(timeMarkerValue)) {
                                // chỉ update những hồ sơ bàn giao trước 16h
                                params.put("isToday", true);
                            }
                        }

                        // lọc ra các hồ sơ chờ
                        listDataWaiting = repository.getProfileWithParams(params, isAsc);
                        if (listDataWaiting.size() > 0) {
                            this.updateProfileList(listDataWaiting, old, user, profileHistory, department.getId(),
                                    item.getCode(), transactionType);
                        } else {
                            // lọc ra các hồ sơ của ngày hôm sau với trường hợp bàn giao trong ngày nhưng
                            // sau 16h
                            // các hồ sơ của ngày hôm sau chỉ update trạng thái với hồ sơ đầu tiên

                            // if (old.getRealTimeReceivedCM().getDayOfMonth() ==
                            // timeMarkerValue.getDayOfMonth()) {
                            // các hồ sơ nhận trong ngay nhưng bàn giao sau 16h

                            List<ProfileDTO> listDataWaittingTomorrow = new ArrayList<>();
                            Map<String, Object> paramsWaiting = new HashMap<>();
                            paramsWaiting.put("state", ProfileStateEnum.WAITING.getValue());
                            paramsWaiting.put("staffId_CM", user.getId());
                            // paramsWaiting.put("staffId_CT", "NULL");
                            paramsWaiting.put("timeReceived_CT", "NULL");

                            if (old.getRealTimeReceivedCM().getDayOfMonth() == timeMarkerValue.getDayOfMonth()) {
                                if (old.getRealTimeReceivedCM().isAfter(timeMarkerValue)) {
                                    // chỉ update những hồ sơ bàn giao trước 16h
                                    paramsWaiting.put("isToday", false);
                                }
                            }

                            listDataWaittingTomorrow = repository.getProfileWithParams(paramsWaiting, isAsc);
                            if (listDataWaittingTomorrow.size() > 0) {
                                ProfileDTO profileProcessingTomorrow = listDataWaittingTomorrow.get(0);
                                profileProcessingTomorrow.setState(ProfileStateEnum.PROCESSING.getValue());
                                save(profileProcessingTomorrow);
                            }

                            // }

                        }

                        break;

                    default:
                        break;
                }

            }

            // lưu khi chưa giải quyết, không tính thời gian đã làm -> cái này giành cho
            // hoàn trả hồ sơ
            if (old.getState().equals(ProfileStateEnum.PENDING.getValue())) {

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
                        // params.put("staffId_CT", "NULL");
                        params.put("timeReceived_CT", "NULL");

                        // // chỉ update những hồ sơ bàn giao trước 16h
                        // params.put("isToday", true);

                        // kiểm tra xem hồ sơ có bàn giao trong ngày hôm nay không
                        if (old.getRealTimeReceivedCM().getDayOfMonth() == timeMarkerValue.getDayOfMonth()) {
                            if (old.getRealTimeReceivedCM().isBefore(timeMarkerValue)) {
                                // chỉ update những hồ sơ bàn giao trước 16h
                                params.put("isToday", true);
                            }
                        }
                        // lọc ra các hồ sơ trong ngày
                        listDataWaiting = repository.getProfileWithParams(params, isAsc);
                        if (listDataWaiting.size() > 0) {
                            this.updateProfileList(listDataWaiting, old, user, profileHistory, department.getId(),
                                    item.getCode(), transactionType);
                        } else {
                            // lọc ra các hồ sơ của ngày hôm sau với trường hợp bàn giao trong ngày nhưng
                            // các hồ sơ của ngày hôm sau chỉ update trạng thái với hồ sơ đầu tiên
                            // các hồ sơ nhận trong ngay nhưng bàn giao sau 16h
                            // if (old.getRealTimeReceivedCM().getDayOfMonth() ==
                            // timeMarkerValue.getDayOfMonth()) {

                            // chỉ update những hồ sơ bàn giao trước 16h
                            List<ProfileDTO> listDataWaittingTomorrow = new ArrayList<>();
                            Map<String, Object> paramsWaiting = new HashMap<>();
                            paramsWaiting.put("state", ProfileStateEnum.WAITING.getValue());
                            paramsWaiting.put("staffId_CM", user.getId());
                            // paramsWaiting.put("staffId_CT", "NULL");
                            paramsWaiting.put("timeReceived_CT", "NULL");

                            if (old.getRealTimeReceivedCM().isAfter(timeMarkerValue)) {

                                // các hồ sơ nhận trong ngay nhưng bàn giao sau 16h
                                if (old.getRealTimeReceivedCM().getDayOfMonth() == timeMarkerValue
                                        .getDayOfMonth()) {
                                    paramsWaiting.put("isToday", false);
                                }

                            }

                            listDataWaittingTomorrow = repository.getProfileWithParams(paramsWaiting, isAsc);
                            if (listDataWaittingTomorrow.size() > 0) {
                                ProfileDTO profileProcessingTomorrow = listDataWaittingTomorrow.get(0);
                                profileProcessingTomorrow.setState(ProfileStateEnum.PROCESSING.getValue());
                                save(profileProcessingTomorrow);
                            }
                            // }

                        }

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
    public MessageDTO checkTransfer(ConfirmRequest item) {
        // TODO Auto-generated method stub
        MessageDTO message = new MessageDTO();
        try {
            ProfileDTO old = item.getProfile();

            TransactionTypeDTO transactionType = transactionTypeService
                    .findById(Long.parseLong(old.getType().toString()));
            if (DataUtils.isNullObject(transactionType)) {
                throw new ResourceNotFoundException("transaction Type " + old.getType().toString() + " not found");
            }

            UserDTO user = userService.findByUsername(item.getUsername());

            if (DataUtils.isNullObject(user)) {
                throw new ResourceNotFoundException("User " + item.getUsername() + " not found");
            }

            if (old.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                switch (transactionType.getType()) {
                    case 1:
                        if (item.getCode().trim().toUpperCase().equals("QTTD")) {
                            if (DataUtils.isNullOrEmpty(old.getStaffId_CM())) {
                                message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                                message.setIsExist(true);
                            } else {
                                if (old.getStaffId_CM().equals(user.getId())) {
                                    message.setMessage("Hồ sơ cần chuyển cho cán bộ khác");
                                    message.setIsExist(true);
                                } else {
                                    message.setIsExist(false);
                                }
                            }
                        }

                        if (item.getCode().trim().toUpperCase().equals("GDKH")) {
                            if (DataUtils.isNullOrEmpty(old.getStaffId_CT())) {
                                message.setMessage("Hồ sơ chưa bàn giao cho cán bộ GDKH");
                                message.setIsExist(true);
                            } else {
                                if (old.getStaffId_CT().equals(user.getId())) {
                                    message.setMessage("Hồ sơ cần chuyển cho cán bộ khác");
                                    message.setIsExist(true);
                                } else {
                                    message.setIsExist(false);

                                }
                            }
                        }

                        break;
                    case 2:
                        if (item.getCode().equals("QTTD")) {
                            if (DataUtils.isNullOrEmpty(old.getStaffId_CM())) {
                                message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                                message.setIsExist(true);
                            } else {
                                if (old.getStaffId_CM().equals(user.getId())) {
                                    message.setMessage("Hồ sơ cần chuyển cho cán bộ khác");
                                    message.setIsExist(true);
                                } else {
                                    message.setIsExist(false);
                                }
                            }
                        }
                        break;
                    case 3:
                        if (item.getCode().equals("GDKH")) {
                            if (DataUtils.isNullOrEmpty(old.getStaffId_CT())) {
                                message.setMessage("Hồ sơ chưa bàn giao cho cán bộ GDKH");
                                message.setIsExist(true);
                            } else {
                                if (old.getStaffId_CT().equals(user.getId())) {
                                    message.setMessage("Hồ sơ cần chuyển cho cán bộ khác");
                                    message.setIsExist(true);
                                } else {
                                    message.setIsExist(false);

                                }
                            }
                        }
                        break;
                    default:
                        break;
                }

            } else {
                message.setMessage("Hồ sơ không bàn giao được do không trong trạng thái chờ xử lý");
                message.setIsExist(true);
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

    @Override
    public ProfileDTO transferInternal(ConfirmRequest item) {
        try {
            ProfileDTO old = item.getProfile();
            // lưu log hồ sơ tiếp nhận
            ProfileHistoryDTO profileHistory = new ProfileHistoryDTO();
            // lưu log chuyển hồ sơ
            ProfileHistoryDTO pHistoryInternal = new ProfileHistoryDTO();
            profileHistory.setTimeReceived(LocalDateTime.now());
            UserDTO user = userService.findByUsername(item.getUsername());
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> paramsProcessing = new HashMap<>();
            params.put("state", ProfileStateEnum.WAITING.getValue());
            paramsProcessing.put("state", ProfileStateEnum.PROCESSING.getValue());
            boolean isAsc = false;
            List<ProfileDTO> listDataWaiting = new ArrayList<>();
            List<ProfileDTO> listDataProcessing = new ArrayList<>();

            // lưu kết quả của timeReceived và processTime;
            Map<String, Object> mapResult = new HashMap<>();

            if (DataUtils.isNullObject(user)) {
                throw new ResourceNotFoundException("User " + item.getUsername() + " not found");
            }
            DepartmentDTO department = departmentService.findById(user.getDepartmentId());
            if (DataUtils.isNullObject(department)) {
                throw new ResourceNotFoundException("department " + department.getCode() + " not found");
            }

            TransactionTypeDTO transactionType = transactionTypeService
                    .findById(Long.parseLong(old.getType().toString()));
            if (DataUtils.isNullObject(transactionType)) {
                throw new ResourceNotFoundException("transaction Type " + old.getType().toString() + " not found");
            }

            // lịch sử hồ sơ chuyển nội bộ của user trước
            pHistoryInternal.setDepartmentCode(department.getCode());
            pHistoryInternal.setDepartmentId(department.getId());
            pHistoryInternal.setTimeReceived(LocalDateTime.now());
            pHistoryInternal.setState(ProfileStateEnum.INTERNALTRANSFERED.getValue());

            LocalDateTime timeMarkerValue = calculatingTime.convertTimeMarker(timeConfig);

            // luồng giao dịch
            Integer[] intArray = new Integer[] { 1, 2 };

            if (Arrays.asList(intArray).contains(transactionType.getType())) {
                if (item.getCode().trim().toUpperCase().equals("QTTD")) {

                    // check hồ sơ chờ của người chuyển nội bộ - người bàn giao
                    Map<String, Object> paramWaitingOfOldUser = new HashMap<>();
                    Map<String, Object> paramProcessingOfOldUser = new HashMap<>();
                    paramWaitingOfOldUser.put("staffId_CM", old.getStaffId_CM());
                    paramWaitingOfOldUser.put("state", ProfileStateEnum.WAITING.getValue());
                    // paramWaitingOfOldUser.put("staffId_CT", "NULL");
                    paramWaitingOfOldUser.put("timeReceived_CT", "NULL");

                    // ignoreId = loại bỏ hồ sơ đang chuyển
                    paramWaitingOfOldUser.put("ignoreId", old.getId());

                    paramProcessingOfOldUser.put("staffId_CM", old.getStaffId_CM());
                    paramProcessingOfOldUser.put("state", ProfileStateEnum.PROCESSING.getValue());
                    // paramProcessingOfOldUser.put("staffId_CT", "NULL");
                    paramProcessingOfOldUser.put("timeReceived_CT", "NULL");

                    // kiểm tra xem ngày nhận có phải trong ngày hôm nay không
                    // nếu có thì kiểm tra xem có vượt 16h không
                    // nếu không phải ngày hôm nay thì không add thêm param vào để lọc ngày
                    if (timeMarkerValue.getDayOfMonth() == old.getRealTimeReceivedCM()
                            .getDayOfMonth()) {
                        // kiểm tra xem thời gian nhận có vượt 16h hôm nay ko
                        if (profileHistory.getTimeReceived().isBefore(timeMarkerValue)) {
                            paramWaitingOfOldUser.put("isToday", true);
                        } else {
                            paramWaitingOfOldUser.put("isToday", false);
                        }
                    }

                    // get list after saving dto
                    List<ProfileDTO> dataWaitingOfOldUser = repository.getProfileWithParams(paramWaitingOfOldUser,
                            isAsc);
                    List<ProfileDTO> dataProcessingOfOldUser = repository.getProfileWithParams(paramProcessingOfOldUser,
                            isAsc);

                    // update các hồ sơ chờ còn lại của hồ sơ cũ
                    if (dataWaitingOfOldUser.size() > 0) {
                        // có hồ sơ đang xử lý
                        if (dataProcessingOfOldUser.size() > 0) {
                            this.updateProfileWaitingList(dataWaitingOfOldUser, dataProcessingOfOldUser.get(0),
                                    profileHistory.getTimeReceived());
                        }
                        // không có hồ sơ đang xử lý thì không làm gì vì sẽ tự add sang đang xử lý
                        else {

                        }

                    }

                    // đã check null staffId_CM ở hàm checkTransfer
                    pHistoryInternal.setStaffId(old.getStaffId_CM());
                    // hồ sơ luồng 1,2 : staffId_CT = null; timeReceived_CT = NULL (khi hồ sơ chưa
                    // bàn giao tại GDKH)
                    // hồ sơ chờ xử lý của người được chuyển
                    params.put("staffId_CM", user.getId());
                    // params.put("staffId_CT", "NULL");
                    params.put("timeReceived_CT", "NULL");

                    // kiểm tra xem ngày nhận có phải trong ngày hôm nay không
                    // nếu có thì kiểm tra xem có vượt 16h không
                    // nếu không phải ngày hôm nay thì không add thêm param vào để lọc ngày
                    if (timeMarkerValue.getDayOfMonth() == old.getRealTimeReceivedCM()
                            .getDayOfMonth()) {
                        // kiểm tra xem thời gian nhận có vượt 16h hôm nay ko
                        if (profileHistory.getTimeReceived().isBefore(timeMarkerValue)) {
                            params.put("isToday", true);
                        } else {
                            params.put("isToday", false);
                        }
                    }

                    listDataWaiting = repository.getProfileWithParams(params, isAsc);

                    // hồ sơ đang xử lý của người quét chuyển hồ sơ - người nhận
                    paramsProcessing.put("staffId_CM", user.getId());
                    // paramsProcessing.put("staffId_CT", "NULL");
                    paramsProcessing.put("timeReceived_CT", "NULL");
                    listDataProcessing = repository.getProfileWithParams(paramsProcessing, isAsc);

                    if (listDataProcessing.size() == 0) {
                        // do không có hồ sơ chờ nên set hồ sơ tiếp theo thành đang xử lý
                        old.setState(ProfileStateEnum.PROCESSING.getValue());
                        // set log trạng thái hồ sơ
                        profileHistory.setState(ProfileStateEnum.PROCESSING.getValue());

                        // set user bằng user người dùng thực hiện quét
                        old.setStaffId_CM(user.getId());

                        // kiểm tra xem thời gian nhận có vượt 16h hôm nay ko để chọn hàm tính thời gian
                        if (profileHistory.getTimeReceived().isBefore(timeMarkerValue)) {
                            mapResult = calculatingTime.calculatingDateFromRealTimeReceived(
                                    pHistoryInternal.getTimeReceived(),
                                    transactionType.getStandardTimeCM(),
                                    transactionType.getStandardTimeChecker(), old.getAdditionalTime(),
                                    old.getNumberOfPO(), old.getNumberOfBill(),
                                    transactionType.getType());
                        } else {

                            mapResult = calculatingTime.calculatingDateFromTimeReceived(
                                    pHistoryInternal.getTimeReceived(),
                                    transactionType.getStandardTimeCM(),
                                    transactionType.getStandardTimeChecker(), old.getAdditionalTime(),
                                    old.getNumberOfPO(), old.getNumberOfBill(),
                                    transactionType.getType());
                        }

                        LocalDateTime processTime = (LocalDateTime) mapResult.get("processTime");
                        LocalDateTime timeReceived = (LocalDateTime) mapResult.get("timeReceived");

                        // set lại thời gian nhận cho hồ sơ
                        old.setTimeReceived_CM(timeReceived);
                        old.setProcessDate(processTime);
                    } else {
                        if (listDataWaiting.size() == 0) {
                            // do có 1 hồ sơ đang xử lý và không có hồ sơ chờ nên set hồ sơ tiếp theo thành
                            // chờ xử lý => đây là hồ sơ chờ xử lý đầu tiên
                            old.setState(ProfileStateEnum.WAITING.getValue());

                            // set user bằng user người dùng thực hiện quét
                            old.setStaffId_CM(user.getId());

                            // kiểm tra xem thời gian nhận có vượt 16h hôm nay ko để chọn hàm tính thời gian
                            if (profileHistory.getTimeReceived().isBefore(timeMarkerValue)) {
                                // không vượt thì tính liền
                                mapResult = calculatingTime.calculatingDateFromRealTimeReceived(
                                        listDataProcessing.get(0).getProcessDate(), transactionType.getStandardTimeCM(),
                                        transactionType.getStandardTimeChecker(),
                                        old.getAdditionalTime(),
                                        old.getNumberOfPO(), old.getNumberOfBill(),
                                        transactionType.getType());

                            } else {
                                // vượt thì tính mốc
                                mapResult = calculatingTime.calculatingDateFromTimeReceived(
                                        listDataProcessing.get(0).getProcessDate(), transactionType.getStandardTimeCM(),
                                        transactionType.getStandardTimeChecker(),
                                        old.getAdditionalTime(),
                                        old.getNumberOfPO(), old.getNumberOfBill(),
                                        transactionType.getType());
                            }

                            LocalDateTime processTime = (LocalDateTime) mapResult.get("processTime");
                            LocalDateTime timeReceived = (LocalDateTime) mapResult.get("timeReceived");
                            // set lại thời gian nhận cho hồ sơ bằng thời gian xử lý của hồ sơ đang xử lý
                            old.setTimeReceived_CM(timeReceived);
                            old.setProcessDate(processTime);
                        } else {
                            // do có hồ sơ chờ nên set hồ sơ tiếp theo thành chờ xử lý
                            old.setState(ProfileStateEnum.WAITING.getValue());
                            // set user bằng user người dùng thực hiện quét chuyển hồ sơ
                            old.setStaffId_CM(user.getId());

                            ProfileDTO last = listDataWaiting.get(listDataWaiting.size() - 1);

                            // thời gian xử lý : tính từ thời gian của hồ sơ chờ phía trước

                            // kiểm tra xem thời gian nhận có vượt 16h hôm nay ko để chọn hàm tính thời gian
                            // dùng để update thời gian của hồ sơ chờ xử lý thứ 2 trở đi
                            if (profileHistory.getTimeReceived().isBefore(timeMarkerValue)) {
                                mapResult = calculatingTime.calculatingDateFromRealTimeReceived(last.getProcessDate(),
                                        transactionType.getStandardTimeCM(),
                                        transactionType.getStandardTimeChecker(),
                                        old.getAdditionalTime(),
                                        old.getNumberOfPO().intValue(), old.getNumberOfBill(),
                                        transactionType.getType());
                            } else {

                                mapResult = calculatingTime.calculatingDateFromTimeReceived(
                                        last.getProcessDate(), transactionType.getStandardTimeCM(),
                                        transactionType.getStandardTimeChecker(),
                                        old.getAdditionalTime(),
                                        old.getNumberOfPO().intValue(), old.getNumberOfBill(),
                                        transactionType.getType());
                            }

                            LocalDateTime processTime = (LocalDateTime) mapResult.get("processTime");
                            LocalDateTime timeReceived = (LocalDateTime) mapResult.get("timeReceived");
                            // set lại thời gian nhận cho hồ sơ bằng thời gian xử lý của hồ sơ chờ cuối cùng
                            // trong list
                            old.setTimeReceived_CM(timeReceived);
                            old.setProcessDate(processTime);
                        }
                        // set log trạng thái hồ sơ
                        profileHistory.setState(ProfileStateEnum.WAITING.getValue());
                    }

                }

                if (item.getCode().trim().toUpperCase().equals("GDKH")) {
                    if (!item.getUsername().contains("admin")) {
                        // đã check null staffId_CT ở hàm checkTransfer
                        pHistoryInternal.setStaffId(old.getStaffId_CT());

                        // set user bằng user người dùng thực hiện quét chuyển hồ sơ
                        old.setStaffId_CT(user.getId());
                        old.setState(ProfileStateEnum.PROCESSING.getValue());
                    }

                }

            } else {
                if (item.getCode().trim().toUpperCase().equals("GDKH")) {
                    if (!item.getUsername().contains("admin")) {
                        // đã check null staffId_CT ở hàm checkTransfer
                        pHistoryInternal.setStaffId(old.getStaffId_CT());

                        // set user bằng user người dùng thực hiện quét chuyển hồ sơ
                        old.setStaffId_CT(user.getId());
                        old.setState(ProfileStateEnum.PROCESSING.getValue());
                    }
                }
            }

            profileHistory.setDepartmentCode(department.getCode());
            profileHistory.setDepartmentId(department.getId());
            profileHistory.setTimeReceived(LocalDateTime.now());
            profileHistory.setStaffId(user.getId());

            ProfileDTO dto = save(old);
            // lưu log nhận hồ sơ
            profileHistory.setProfileId(dto.getId());
            profileHistoryService.save(profileHistory);
            // lưu log chuyển hồ sơ
            pHistoryInternal.setProfileId(dto.getId());
            profileHistoryService.save(pHistoryInternal);

            return dto;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    @Override
    public Boolean deleteList(List<Long> ids) {
        // TODO Auto-generated method stub
        try {
            Integer delete = repository.deleteList(ids);
            if (delete > 0) {
                profileHistoryService.deleteListByProfileId(ids);
                return true;
            }
            return false;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return false;
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

                DepartmentDTO deparment = departmentService.findById(user.getDepartmentId());
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
                                                || old.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                            message.setMessage("Giao dịch này đã được nhận 1 lần");
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

                        case "GDKH-test":
                            // đã bàn giao tại quản trị tín dụng
                            if (!DataUtils.isNullOrEmpty(dto.getStaffId_CM())) {
                                // check bàn giao tại GDKH
                                if (DataUtils.isNullOrEmpty(dto.getStaffId_CT())) {
                                    switch (dto.getState()) {
                                        // Kết thúc giao dịch
                                        case 7:
                                            message.setMessage("Bạn đã kết thúc giao dịch này");
                                            message.setIsExist(true);
                                            break;
                                        // Cần bổ sung
                                        case 6:
                                            if (item.getIsFinished()) {
                                                message.setMessage("Hồ sơ phải được bàn giao tại rổ chung của Giao dịch khách hàng");
                                                message.setIsExist(true);
                                            } else {
                                                message.setIsExist(false);
                                            }
                                            break;

                                        // Đang xử lý
                                        case 5:
                                            // nhận bàn giao từ QTTD tới máy chung - admin
                                            if (item.getUsername().contains("admin")) {
                                                // nếu quét nhầm finish
                                                if (item.getIsFinished()) {
                                                    message.setMessage(
                                                            "Không thể kết thúc giao dịch do cán bộ chưa nhận hồ sơ");
                                                    message.setIsExist(true);
                                                } else {
                                                    message.setIsExist(false);
                                                }
                                            } else {
                                                // cán bộ GDKH quét QR nhầm
                                                // khi chưa bàn giao hồ sơ tại giao dịch khách hàng thì thời gian = null
                                                // do đó check thêm thời gian để biết bàn giao chưa
                                                if (DataUtils.isNullOrEmpty(dto.getTimeReceived_CT())) {
                                                    message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                                    message.setIsExist(true);
                                                } else {
                                                    message.setIsExist(false);
                                                }
                                              
                                            }
                                            break;
                                        // Chờ xử lý
                                        case 4:
                                            if (item.getIsFinished()) {
                                                message.setMessage("Hồ sơ này đã đang chờ xử lý");
                                                message.setIsExist(true);
                                            } else {
                                                if (dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                                    message.setMessage("Hồ sơ này đã được nhận 1 lần");
                                                    message.setIsExist(true);
                                                } else {
                                                    message.setIsExist(false);

                                                }
                                            }
                                            break;
                                        default:
                                            message.setIsExist(false);
                                            break;
                                    }
                                }

                            } else {
                                message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                                message.setIsExist(true);
                            }

                            break;

                        case "GDKH":
                            if (!DataUtils.isNullOrEmpty(dto.getStaffId_CM())) {
                                // check delivery to GDKH
                                // state of profile: not_yet, tranfer
                                Integer[] intArray = new Integer[] { 6 };
                                // trường hợp trả hồ sơ thì bàn giao lại từ đầu tại qttd do đó cần check lại
                                // trạng thái
                                // nếu = 6 thì phải bàn giao tại qttd trước
                                // cán bộ GDKH chưa nhận hồ sơ
                                if (DataUtils.isNullOrEmpty(dto.getStaffId_CT())) {
                                    if (Arrays.asList(intArray).contains(dto.getState())) {
                                        message.setMessage("Hồ sơ chưa bàn giao tại quản trị tín dụng");
                                        message.setIsExist(true);
                                    }
                                    // bàn giao hồ sơ đang xử lý sang GDKH từ QTTD
                                    else if (dto.getState().equals(ProfileStateEnum.PROCESSING.getValue())
                                            || dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                        // nhận bàn giao từ QTTD tới máy chung - admin
                                        if (item.getUsername().contains("admin")) {
                                            // nếu quét nhầm finish
                                            if (item.getIsFinished()) {
                                                message.setMessage(
                                                        "Không thể kết thúc giao dịch do cán bộ chưa nhận hồ sơ");
                                                message.setIsExist(true);
                                            } else {
                                                message.setIsExist(false);
                                            }
                                        } else {
                                            // cán bộ GDKH quét QR nhầm
                                            // khi chưa bàn giao hồ sơ tại giao dịch khách hàng thì thời gian = null
                                            // do đó check thêm thời gian để biết bàn giao chưa
                                            if (DataUtils.isNullOrEmpty(dto.getTimeReceived_CT())) {
                                                message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                                message.setIsExist(true);
                                            } else {
                                                message.setIsExist(false);
                                            }
                                            // message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                            // message.setIsExist(true);
                                        }
                                    }
                                    // hồ sơ đã nhận tại GDKH và cán bộ GDKH tiếp nhận
                                    else if (dto.getState().equals(ProfileStateEnum.RECEIVED.getValue())) {
                                        // quét bằng tk admin GDKH
                                        if (item.getUsername().contains("admin")) {
                                            if (item.getIsFinished()) {
                                                message.setMessage(
                                                        "Không thể kết thúc giao dịch do cán bộ GDKH chưa nhận hồ sơ");
                                                message.setIsExist(true);
                                            } else {
                                                message.setMessage(
                                                        "Hồ sơ này đã được nhận 1 lần tại phòng giao dịch khách hàng");
                                                message.setIsExist(true);
                                            }
                                        } else {
                                            // cán bộ GDKH quét QR nhầm kết thúc giao dịch
                                            if (item.getIsFinished()) {
                                                message.setMessage(
                                                        "Không thể kết thúc giao dịch do cán bộ GDKH chưa nhận hồ sơ");
                                                message.setIsExist(true);
                                            } else {
                                                message.setIsExist(false);
                                            }
                                        }
                                    }
                                }
                                // cán bộ GDKH đã nhận hồ sơ trong trường hợp luồng trả hồ sơ
                                // => phải bàn giao lại rỏ chung
                                else {
                                    // hồ sơ đang xử lý (luồng thông thường hoặc luồng đã có trả hồ sơ)
                                    if (dto.getState().equals(ProfileStateEnum.PROCESSING.getValue())
                                            || dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                        if (item.getUsername().contains("admin")) {
                                            // check scan kết thúc hồ sơ
                                            if (item.getIsFinished()) {
                                                message.setMessage(
                                                        "Không thể kết thúc giao dịch do cán bộ GDKH chưa nhận hồ sơ");
                                                message.setIsExist(true);
                                            } else {
                                                message.setIsExist(false);
                                            }

                                        } else {
                                            // khi hồ sơ thuộc luồng trả hồ sơ tức là đã bàn giao 1 lần tại GDKH
                                            // khi tiến hành hoàn trả hồ sơ thì thời gian bàn giao tại giao dịch khách
                                            // hàng bị reset = null
                                            // do đó cần check thêm thời gian bàn giao tại giao dịch khách hàng để biết
                                            // xem đã bàn giao chưa
                                            if (DataUtils.isNullOrEmpty(dto.getTimeReceived_CT())) {
                                                message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                                message.setIsExist(true);
                                            } else {
                                                message.setIsExist(false);
                                            }
                                        }

                                    }
                                    // hồ sơ đã nhận tại GDKH và cán bộ GDKH tiếp nhận
                                    else if (dto.getState().equals(ProfileStateEnum.RECEIVED.getValue())) {
                                        // quét bằng tk admin GDKH
                                        if (item.getUsername().contains("admin")) {
                                            if (item.getIsFinished()) {
                                                message.setMessage(
                                                        "Không thể kết thúc giao dịch do cán bộ GDKH chưa nhận hồ sơ");
                                                message.setIsExist(true);
                                            } else {
                                                message.setMessage(
                                                        "Hồ sơ này đã được nhận 1 lần tại phòng giao dịch khách hàng");
                                                message.setIsExist(true);
                                            }
                                        } else {
                                            // cán bộ GDKH quét QR nhầm kết thúc giao dịch
                                            if (item.getIsFinished()) {
                                                message.setMessage(
                                                        "Không thể kết thúc giao dịch do cán bộ GDKH chưa nhận hồ sơ");
                                                message.setIsExist(true);
                                            } else {
                                                message.setIsExist(false);
                                            }
                                        }
                                    }
                                    // hồ sơ đã kết thúc và quét nhầm
                                    else if (dto.getState().equals(ProfileStateEnum.FINISHED.getValue())) {
                                        message.setMessage("Bạn đã kết thúc giao dịch này");
                                        message.setIsExist(true);

                                    }
                                    // hồ sơ cần bổ sung thì cần bàn giao lại từ đầu
                                    else if (dto.getState().equals(ProfileStateEnum.ADDITIONAL.getValue())) {
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

                            switch (dto.getState()) {
                                // Kết thúc giao dịch
                                case 7:
                                    message.setMessage("Bạn đã kết thúc giao dịch này");
                                    message.setIsExist(true);
                                    break;
                                // Cần bổ sung
                                case 6:
                                    if (item.getIsFinished()) {
                                        message.setMessage("Bàn giao cho cán bộ quản trị tín dụng");
                                        message.setIsExist(true);
                                    } else {
                                        message.setIsExist(false);
                                    }
                                    break;

                                // Đang xử lý
                                case 5:
                                    if (item.getIsFinished()) {
                                        message.setIsExist(false);
                                    } else {
                                        if (old.getState().equals(ProfileStateEnum.PROCESSING.getValue())) {
                                            message.setMessage("Hồ sơ này đã được nhận 1 lần");
                                            message.setIsExist(true);
                                        } else {
                                            message.setIsExist(false);

                                        }
                                    }
                                    break;
                                // Chờ xử lý
                                case 4:
                                    if (item.getIsFinished()) {
                                        message.setMessage("Hồ sơ này đã đang chờ xử lý");
                                        message.setIsExist(true);
                                    } else {
                                        if (dto.getState().equals(ProfileStateEnum.WAITING.getValue())) {
                                            message.setMessage("Hồ sơ này đã được nhận 1 lần");
                                            message.setIsExist(true);
                                        } else {
                                            message.setIsExist(false);

                                        }
                                    }
                                    break;
                                default:
                                    message.setIsExist(false);
                                    break;
                            }

                        }
                        // hồ sơ chưa bàn giao cho cán bộ QTTD
                        else {
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

                            switch (dto.getState()) {
                                // Kết thúc giao dịch
                                case 7:
                                    // kiểm tra hồ sơ tồn tại có đang xử lý không
                                    message.setMessage("Bạn đã kết thúc giao dịch này");
                                    message.setIsExist(true);

                                    break;
                                // Cần bổ sung
                                case 6:
                                    if (item.getIsFinished()) {
                                        message.setMessage("Hồ sơ chưa bàn giao tại giao dịch khách hàng");
                                        message.setIsExist(true);
                                    } else {
                                        message.setIsExist(false);
                                    }
                                    break;

                                // Đang xử lý
                                case 5:
                                    if (item.getIsFinished()) {
                                        message.setIsExist(false);
                                    } else {
                                        // kiểm tra hồ sơ tồn tại có đang xử lý không, giao dịch khách hàng ko có chờ xử
                                        // lý
                                        if (old.getState().equals(ProfileStateEnum.PROCESSING.getValue())) {
                                            message.setMessage("Hồ sơ này đã được nhận 1 lần");
                                            message.setIsExist(true);
                                        } else {
                                            message.setIsExist(false);

                                        }
                                    }
                                    break;

                                default:
                                    message.setIsExist(false);
                                    break;
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

                DepartmentDTO deparment = departmentService.findById(user.getDepartmentId());
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

    /**
     * chỉ tính ưu tiên hồ sơ trong ngày
     */
    @Override
    public MessageDTO priorityProfile(ConfirmRequest item) {
        MessageDTO message = new MessageDTO();

        try {
            ProfileDTO dto = findById(item.getProfile().getId());
            if (!DataUtils.isNullOrEmpty(dto)) {

                Integer numberOfPriority = userService.getNumberOfPriorityByUsername(item.getUsername());
                Integer countActionLogCountPriorityProfile = actionLogRepository
                        .countActionLogPriorityProfile("priorityProfile", item.getDepartmentId());

                // kiểm tra xem số lần ưu tiên hồ sơ thành công trong ngày có lớn hơn số lần ưu
                // tiên đã được cấu hình
                if (countActionLogCountPriorityProfile >= numberOfPriority) {
                    message.setMessage("Không thể ưu tiên hồ sơ do vượt quá số lần ưu tiên: " + numberOfPriority);
                    message.setIsExist(true);
                    return message;
                }

                TransactionTypeDTO transactionType = transactionTypeService
                        .findById(Long.parseLong(dto.getType().toString()));

                if (DataUtils.isNullOrEmpty(transactionType)) {
                    throw new ResourceNotFoundException(
                            "transaction Type " + dto.getType().toString() + " not found");
                }

                List<Profile> process = repository.findBySateAndStaffId(
                        ProfileStateEnum.PROCESSING.getValue(), dto.getStaffId_CM());

                // LocalDateTime timeReceived = LocalDateTime.now();
                LocalDateTime processTime = null;

                if (process.size() > 0) {

                    if (process.get(0).getProcessDate().equals(dto.getTimeReceived_CM())) {
                        message.setMessage("Hồ sơ này tiếp sau hồ đang xử lý");
                        message.setIsExist(true);
                    } else {
                        // first record
                        if (item.getCode().equals("QTTD") || item.getCode().equals("QLKH")) {
                            // LocalDateTime from = dto.getTimeReceived_CM();
                            // LocalDateTime to = dto.getProcessDate();

                            LocalDateTime timeReceived = process.get(0).getProcessDate();

                            // processDate = DataUtils.calculatingDate(from, to, timeReceived);
                            // lưu kết quả của timeReceived và processTime;
                            Map<String, Object> mapResult = new HashMap<>();

                            // // get list after saving dto
                            // List<Profile> listData = repository.findBySateAndStaffIdAndIgnore(
                            // ProfileStateEnum.WAITING.getValue(), dto.getStaffId_CM(), dto.getId());

                            Map<String, Object> params = new HashMap<String, Object>();
                            params.put("staffId_CM", dto.getStaffId_CM());
                            params.put("state", ProfileStateEnum.WAITING.getValue());
                            // params.put("staffId_CT", "NULL");
                            params.put("timeReceived_CT", "NULL");
                            // ignoreId = loại bỏ hồ sơ đang chuyển
                            params.put("ignoreId", dto.getId());
                            params.put("isToday", true);

                            // get list after saving dto
                            List<ProfileDTO> listData = repository.getProfileWithParams(params,
                                    true);

                            // update processDate for all list
                            // int i = 0;
                            if (listData.size() > 0) {

                                mapResult = calculatingTime.calculatingDateFromRealTimeReceived(timeReceived,
                                        transactionType.getStandardTimeCM(),
                                        transactionType.getStandardTimeChecker(), dto.getAdditionalTime(),
                                        dto.getNumberOfPO(), dto.getNumberOfBill(),
                                        transactionType.getType());

                                processTime = (LocalDateTime) mapResult.get("processTime");
                                timeReceived = (LocalDateTime) mapResult.get("timeReceived");
                                // save dto nếu listData > 0 mới save
                                // dto.setTimeReceived_CM(process.get(0).getProcessDate());
                                dto.setTimeReceived_CM(timeReceived);
                                dto.setProcessDate(processTime);
                                save(dto);

                                for (int i = 0; i < listData.size(); i++) {
                                    // first record update by dto (priority)
                                    if (i == 0) {
                                        // Profile first = listData.get(i);
                                        ProfileDTO first = listData.get(i);
                                        // LocalDateTime fromFirst = first.getTimeReceived_CM();
                                        // LocalDateTime toFirst = first.getProcessDate();
                                        // thời gian nhận của bản ghi chờ thứ nhất là thời gian xử lý của dto
                                        LocalDateTime timeReceivedOfSecond = dto.getProcessDate();

                                        // LocalDateTime date = DataUtils.calculatingDate(fromFirst, toFirst,
                                        // timeReceivedOfSecond);

                                        TransactionTypeDTO transaction = transactionTypeService
                                                .findById(Long.parseLong(first.getType().toString()));

                                        // lưu kết quả của timeReceived và processTime;
                                        Map<String, Object> mapResultNew = new HashMap<>();

                                        mapResultNew = calculatingTime.calculatingDateFromRealTimeReceived(
                                                timeReceivedOfSecond,
                                                transaction.getStandardTimeCM(),
                                                transaction.getStandardTimeChecker(),
                                                first.getAdditionalTime(),
                                                first.getNumberOfPO(), first.getNumberOfBill(),
                                                transaction.getType());

                                        LocalDateTime date = (LocalDateTime) mapResultNew.get("processTime");
                                        LocalDateTime timeReceivedNew = (LocalDateTime) mapResultNew
                                                .get("timeReceived");
                                        // first.setTimeReceived_CM(timeReceivedOfSecond);
                                        first.setTimeReceived_CM(timeReceivedNew);
                                        first.setProcessDate(date);
                                        first.setLastUpdatedDate(LocalDateTime.now());
                                        // repository.save(first);
                                        save(first);

                                    } else {
                                        // Profile first = listData.get(i - 1);
                                        // Profile second = listData.get(i);

                                        ProfileDTO first = listData.get(i - 1);
                                        ProfileDTO second = listData.get(i);

                                        // thời gian xử lý: giờ, phút
                                        // LocalDateTime fromFirst = second.getTimeReceived_CM();
                                        // LocalDateTime toFirst = second.getProcessDate();

                                        // thời gian chờ của bản ghi thứ 2 là thời gian xử lý của bản ghi thứ nhất
                                        LocalDateTime timeReceivedOfSecond = first.getProcessDate();

                                        // loại giao dịch của mỗi hồ sơ - bản ghi
                                        TransactionTypeDTO transaction = transactionTypeService
                                                .findById(Long.parseLong(second.getType().toString()));

                                        // lưu kết quả của timeReceived và processTime;
                                        Map<String, Object> mapResultNew = new HashMap<>();

                                        // tính thời gian trong ngày
                                        mapResultNew = calculatingTime.calculatingDateFromRealTimeReceived(
                                                timeReceivedOfSecond,
                                                transaction.getStandardTimeCM(),
                                                transaction.getStandardTimeChecker(),
                                                second.getAdditionalTime(),
                                                second.getNumberOfPO(), second.getNumberOfBill(),
                                                transaction.getType());

                                        LocalDateTime date = (LocalDateTime) mapResultNew.get("processTime");
                                        second.setTimeReceived_CM(timeReceivedOfSecond);
                                        second.setProcessDate(date);
                                        second.setLastUpdatedDate(LocalDateTime.now());
                                        // repository.save(second);
                                        save(second);
                                    }
                                }
                            }
                            message.setMessage("");
                            message.setIsExist(false);
                        }
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

    // dùng cho update hồ sơ: transferInternal (chuyển nội bộ)
    private void updateProfileWaitingList(List<ProfileDTO> listData, ProfileDTO profile, LocalDateTime timeReceived) {
        // update processDate for all list
        // int i = 0;
        LocalDateTime timeMarkerValue = calculatingTime.convertTimeMarker(timeConfig);

        if (listData.size() > 0) {

            for (int i = 0; i < listData.size(); i++) {
                // first record update by dto (priority)
                if (i == 0) {
                    ProfileDTO first = listData.get(i);
                    LocalDateTime fromFirst = first.getTimeReceived_CM();
                    LocalDateTime toFirst = first.getProcessDate();
                    // thời gian nhận của bản ghi chờ thứ nhất là thời gian xử lý của dto
                    LocalDateTime timeReceivedOfSecond = profile.getProcessDate();

                    TransactionTypeDTO transaction = transactionTypeService
                            .findById(Long.parseLong(first.getType().toString()));

                    // lưu kết quả của timeReceived và processTime;
                    Map<String, Object> mapResultNew = new HashMap<>();

                    if (timeReceived.isBefore(timeMarkerValue)) {
                        mapResultNew = calculatingTime.calculatingDateFromRealTimeReceived(
                                timeReceivedOfSecond,
                                transaction.getStandardTimeCM(),
                                transaction.getStandardTimeChecker(),
                                first.getAdditionalTime(),
                                first.getNumberOfPO(), first.getNumberOfBill(),
                                transaction.getType());
                    } else {
                        // nếu thời gian nhận sau timemarker thì sẽ tính sang ngày hôm sau
                        // hàm này sẽ tính cho những hồ sơ bàn giao sau 16h
                        mapResultNew = calculatingTime.calculatingDateFromTimeReceived(
                                timeReceived,
                                transaction.getStandardTimeCM(),
                                transaction.getStandardTimeChecker(),
                                first.getAdditionalTime(),
                                first.getNumberOfPO(), first.getNumberOfBill(),
                                transaction.getType());
                    }

                    LocalDateTime processTime = (LocalDateTime) mapResultNew.get("processTime");
                    LocalDateTime timeReceivedNew = (LocalDateTime) mapResultNew.get("timeReceived");
                    first.setTimeReceived_CM(timeReceivedNew);
                    first.setProcessDate(processTime);
                    first.setLastUpdatedDate(LocalDateTime.now());
                    save(first);

                } else {
                    ProfileDTO first = listData.get(i - 1);
                    ProfileDTO second = listData.get(i);
                    // thời gian xử lý: giờ, phút
                    LocalDateTime fromFirst = second.getTimeReceived_CM();
                    LocalDateTime toFirst = second.getProcessDate();

                    // thời gian chờ của bản ghi thứ 2 là thời gian xử lý của bản ghi thứ nhất
                    LocalDateTime timeReceivedOfSecond = first.getProcessDate();

                    // loại giao dịch của mỗi hồ sơ - bản ghi
                    TransactionTypeDTO transaction = transactionTypeService
                            .findById(Long.parseLong(second.getType().toString()));

                    // lưu kết quả của timeReceived và processTime;
                    Map<String, Object> mapResultNew = new HashMap<>();

                    if (timeReceived.isBefore(timeMarkerValue)) {
                        mapResultNew = calculatingTime.calculatingDateFromRealTimeReceived(
                                timeReceivedOfSecond,
                                transaction.getStandardTimeCM(),
                                transaction.getStandardTimeChecker(),
                                second.getAdditionalTime(),
                                second.getNumberOfPO(), second.getNumberOfBill(),
                                transaction.getType());
                    } else {
                        mapResultNew = calculatingTime.calculatingDateFromTimeReceived(
                                timeReceivedOfSecond,
                                transaction.getStandardTimeCM(),
                                transaction.getStandardTimeChecker(),
                                second.getAdditionalTime(),
                                second.getNumberOfPO(), second.getNumberOfBill(),
                                transaction.getType());
                    }

                    LocalDateTime processTime = (LocalDateTime) mapResultNew.get("processTime");
                    LocalDateTime timeReceivedNew = (LocalDateTime) mapResultNew.get("timeReceived");
                    second.setTimeReceived_CM(timeReceivedNew);
                    second.setProcessDate(processTime);
                    second.setLastUpdatedDate(LocalDateTime.now());
                    save(second);
                }
            }
        }
    }

    // dùng cho update hồ sơ: pending, additional, finished, bàn giao

    /**
     * các hồ sơ sẽ được update tuần tự dựa vào hồ sơ trước đó
     * vì vậy mà các list hồ sơ đưa vào phải tuân thủ:
     * - là hồ sơ chờ trong 1 ngày
     * - là hồ sơ chờ của ngày hôm sau
     * - là hồ sơ chờ không phân biệt trước sau nhưng phải tuần tự
     */
    private void updateProfileList(List<ProfileDTO> listData, ProfileDTO profile, UserDTO user,
            ProfileHistoryDTO profileHistory, Long departmentId, String code, TransactionTypeDTO transactionType) {
        try {
            LocalDateTime timeMarkerValue = calculatingTime.convertTimeMarker(timeConfig);
            if (listData.size() >= 1) {

                int year = LocalDateTime.now().getYear();
                int month = LocalDateTime.now().getMonthValue();
                int day = LocalDateTime.now().getDayOfMonth();
                // thời gian buổi trưa 11h30' - mốc để tính ngoài giờ hành chính
                LocalDateTime lunchTime1 = LocalDateTime.of(year, month, day,
                        11,
                        30);
                // thời gian buổi trưa 13h30' - mốc để tính ngoài giờ hành chính
                LocalDateTime lunchTime2 = LocalDateTime.of(year, month, day,
                        13,
                        30);
                // thời gian buổi chiều 17h00 - mốc để tính ngoài giờ hành chính
                // LocalDateTime endDay = LocalDateTime.of(year, month, day, 17,
                // 0);
                for (int i = 0; i < listData.size(); i++) {
                    // hồ sơ chờ đầu tiên update bởi profile
                    if (i == 0) {
                        // hồ sơ chờ đầu tiên
                        ProfileDTO first = listData.get(i);
                        LocalDateTime fromFirst = first.getTimeReceived_CM();
                        LocalDateTime toFirst = first.getProcessDate();
                        LocalDateTime timeReceivedOfSecond = null;
                        // kiểm tra profile đã kết thúc chưa
                        // thời gian kết thúc của hồ sơ có sau thời gian xử lý không
                        // không có thì set = thời gian nhận của profileHistory

                        if (!DataUtils.isNullOrEmpty(profile.getEndTime())) {
                            int hour = profile.getEndTime().getHour();
                            int minutes = profile.getEndTime().getMinute();

                            // kết thúc trong ngày thì ko làm gì cả
                            if (profile.getEndTime().isAfter(timeMarkerValue)) {
                                // hồ sơ kết thúc ngoài giờ hành chính từ 17h trở đi
                                // hồ sơ chờ tiếp sau sẽ update thời gian nhận tính từ 8h sáng hôm sau
                                // LocalDateTime tomorrow = profile.getEndTime().plusDays(1);
                                // timeReceivedOfSecond = LocalDateTime.of(tomorrow.getYear(),
                                // tomorrow.getMonth(),
                                // tomorrow.getDayOfMonth(), 8, 0);
                            }
                            // kết thúc trong khoảng 11h30 - 13h30
                            else if (lunchTime1.isBefore(profile.getEndTime())
                                    && lunchTime2.isAfter(profile.getEndTime())) {
                                // if (minutes > 30) {
                                // hồ sơ kết thúc ngoài giờ hành chính từ 11h30 trở đi đến trước 13h30
                                // hồ sơ chờ tiếp sau sẽ update thời gian nhận tính từ 13h30 cùng ngày
                                LocalDateTime today = profile.getEndTime();
                                timeReceivedOfSecond = LocalDateTime.of(today.getYear(), today.getMonth(),
                                        today.getDayOfMonth(), 13, 30);
                                // }
                            } else {
                                // hồ sơ kết thúc trong giờ hành chính
                                timeReceivedOfSecond = profileHistory.getTimeReceived();
                            }
                        } else {
                            // hồ sơ chưa kết thúc
                            timeReceivedOfSecond = profileHistory.getTimeReceived();
                        }

                        // loại giao dịch của mỗi hồ sơ - bản ghi
                        TransactionTypeDTO transaction = transactionTypeService
                                .findById(Long.parseLong(first.getType().toString()));

                        // lưu kết quả của timeReceived và processTime;
                        Map<String, Object> mapResultNew = new HashMap<>();

                        // tính lại thời gian xử lý của hồ sơ/bản ghi thứ nhất
                        // nếu thời gian thực nhận mà trước timeMarker thì update trong ngày
                        if (first.getRealTimeReceivedCM().isBefore(timeMarkerValue)) {
                            mapResultNew = calculatingTime.calculatingDateFromRealTimeReceived(
                                    timeReceivedOfSecond,
                                    transaction.getStandardTimeCM(),
                                    transaction.getStandardTimeChecker(),
                                    first.getAdditionalTime(),
                                    first.getNumberOfPO(), first.getNumberOfBill(),
                                    transaction.getType());
                        } else {

                            mapResultNew = calculatingTime.calculatingDateFromRealTimeReceived(
                                    timeReceivedOfSecond,
                                    transaction.getStandardTimeCM(),
                                    transaction.getStandardTimeChecker(),
                                    first.getAdditionalTime(),
                                    first.getNumberOfPO(), first.getNumberOfBill(),
                                    transaction.getType());

                            // // nếu update hồ sơ từ pending (chưa giải quyết) và additional (cần bổ sung),
                            // // finished (kết thúc) thì tính update các hồ sơ tiếp theo trong ngày
                            // if (profile.getState().equals(ProfileStateEnum.ADDITIONAL.getValue())
                            // || profile.getState().equals(ProfileStateEnum.PENDING.getValue())
                            // || profile.getState().equals(ProfileStateEnum.FINISHED.getValue())) {
                            // mapResultNew = calculatingTime.calculatingDateFromRealTimeReceived(
                            // timeReceivedOfSecond,
                            // transaction.getStandardTimeCM(),
                            // transaction.getStandardTimeChecker(),
                            // first.getAdditionalTime(),
                            // first.getNumberOfPO(), first.getNumberOfBill(),
                            // transaction.getType());
                            // } else {
                            // mapResultNew = calculatingTime.calculatingDateFromTimeReceived(
                            // timeReceivedOfSecond,
                            // transaction.getStandardTimeCM(),
                            // transaction.getStandardTimeChecker(),
                            // first.getAdditionalTime(),
                            // first.getNumberOfPO(), first.getNumberOfBill(),
                            // transaction.getType());
                            // }
                        }

                        LocalDateTime date = (LocalDateTime) mapResultNew.get("processTime");

                        first.setTimeReceived_CM(timeReceivedOfSecond);
                        first.setProcessDate(date);
                        first.setLastUpdatedDate(LocalDateTime.now());

                        // update state for first waiting profile
                        first.setState(ProfileStateEnum.PROCESSING.getValue());

                        // save history
                        ProfileHistoryDTO his = new ProfileHistoryDTO();
                        switch (transactionType.getType()) {
                            case 1:
                                // at QTTD
                                if (code.equals("QTTD")) {

                                    his.setStaffId(first.getStaffId_CM());
                                }
                                // at GDKH
                                if (code.equals("GDKH")) {
                                    if (user.getUsername().contains("admin")) {
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
                        // hồ sơ thứ 2 trở đi
                        ProfileDTO first = listData.get(i - 1);
                        ProfileDTO second = listData.get(i);
                        // processDate: hours, minutes
                        LocalDateTime fromFirst = second.getTimeReceived_CM();
                        LocalDateTime toFirst = second.getProcessDate();
                        LocalDateTime timeReceivedOfSecond = first.getProcessDate();

                        // loại giao dịch của mỗi hồ sơ - bản ghi -> hồ sơ thứ 2 (i)
                        TransactionTypeDTO transaction = transactionTypeService
                                .findById(Long.parseLong(second.getType().toString()));
                        // lưu kết quả của timeReceived và processTime;
                        Map<String, Object> mapResultNew = new HashMap<>();
                        // tính lại thời gian xử lý của hồ sơ/bản ghi tiếp sau

                        mapResultNew = calculatingTime.calculatingDateFromRealTimeReceived(
                                timeReceivedOfSecond,
                                transaction.getStandardTimeCM(),
                                transaction.getStandardTimeChecker(),
                                second.getAdditionalTime(),
                                second.getNumberOfPO(), second.getNumberOfBill(),
                                transaction.getType());

                        // // nếu update hồ sơ từ pending (chưa giải quyết) và additional (cần bổ sung),
                        // // finished (kết thúc) thì tính update các hồ sơ tiếp theo trong ngày
                        // if (profile.getState().equals(ProfileStateEnum.ADDITIONAL.getValue())
                        // || profile.getState().equals(ProfileStateEnum.PENDING.getValue())
                        // || profile.getState().equals(ProfileStateEnum.FINISHED.getValue())) {
                        // mapResultNew = calculatingTime.calculatingDateFromRealTimeReceived(
                        // timeReceivedOfSecond,
                        // transaction.getStandardTimeCM(),
                        // transaction.getStandardTimeChecker(),
                        // second.getAdditionalTime(),
                        // second.getNumberOfPO(), second.getNumberOfBill(),
                        // transaction.getType());
                        // } else {
                        // mapResultNew = calculatingTime.calculatingDateFromTimeReceived(
                        // timeReceivedOfSecond,
                        // transaction.getStandardTimeCM(),
                        // transaction.getStandardTimeChecker(),
                        // second.getAdditionalTime(),
                        // second.getNumberOfPO(), second.getNumberOfBill(),
                        // transaction.getType());
                        // }

                        LocalDateTime date = (LocalDateTime) mapResultNew.get("processTime");

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

    @Override
    public List<ProfileDTO> getProfileDashboard(Map<String, Object> paramSearch) {
        // TODO Auto-generated method stub
        try {
            List<ProfileDTO> listData = repository.getProfileDashboard(paramSearch);
            return listData;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * return -1 = error
     */
    @Override
    public long countProfile(List<Integer> listState) {
        // TODO Auto-generated method stub
        try {
            return repository.count(listState);
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return -1;

        }
    }

    /**
     * return -1 = error
     */
    @Override
    public Integer countProfileInday() {
        // TODO Auto-generated method stub
        try {
            Integer result = null;
            System.out.println(timeConfig);
            // if (timeConfig instanceof Double) {
            int intPart = (int) timeConfig.doubleValue();
            int minutes = (int) Math.round((timeConfig.doubleValue() - intPart) * 60);
            if (minutes > 0) {
                result = repository.countProfileInday(Integer.valueOf(intPart), Integer.valueOf(minutes)).size();
            } else {
                result = repository.countProfileInday(Integer.valueOf(timeConfig.intValue()), Integer.valueOf(0))
                        .size();

            }
            // } else {
            // result = repository.countProfileInday(Integer.valueOf(timeConfig.intValue()),
            // Integer.valueOf(0))
            // .size();
            // }
            System.out.println("-------------------" + result);
            return result;
        } catch (Exception e) {
            // TODO: handle exception
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return -1;
        }
    }

    @Override
    public Integer countProfileInDayByState(Integer state) {
        // TODO Auto-generated method stub
        try {
            Integer result = null;
            // if (timeConfig instanceof Double) {
            int intPart = (int) timeConfig.doubleValue();
            int minutes = (int) Math.round((timeConfig.doubleValue() - intPart) * 60);
            if (minutes > 0) {
                result = repository
                        .countProfileInDayByState(Integer.valueOf(intPart), Integer.valueOf(minutes), state).size();
            } else {
                result = repository
                        .countProfileInDayByState(Integer.valueOf(timeConfig.intValue()), Integer.valueOf(0), state)
                        .size();

            }
            // } else {
            // result = repository
            // .countProfileInDayByState(Integer.valueOf(timeConfig.intValue()),
            // Integer.valueOf(0), state)
            // .size();
            // }
            System.out.println("-------------------" + result);
            return result;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return -1;
        }
    }

    @Override
    public List<DashboardDTO> profileExpected() {
        // TODO Auto-generated method stub
        try {
            // luong 1
            List<Integer> type1 = Arrays.asList(new Integer[] { 0, 1, 2, 3, 8, 9 });
            // luong 2
            List<Integer> type2 = Arrays.asList(new Integer[] { 0, 1, 3, 8, 9 });
            // luong 3
            List<Integer> type3 = Arrays.asList(new Integer[] {
                    0, 1, 3, 8, 9
            });

            List<DashboardDTO> dashboards = new ArrayList<>();

            // if (timeConfig instanceof Double) {
            int intPart = (int) timeConfig.doubleValue();
            int minutes = (int) Math.round((timeConfig.doubleValue() - intPart) * 60);

            return dashboards;

        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Integer count() {
        // TODO Auto-generated method stub
        return Integer.valueOf((int) repository.count());
    }

    /**
     * hàm dùng cho các card còn lại: gọi theo trạng thái, mã vai trò, luồng
     */
    @Override
    public List<ProfileDTO> countProfileInDayByListState(List<Integer> state, String code,
            List<Integer> transactionType, Map<String, Object> parameters) {
        // TODO Auto-generated method stub
        try {
            List<ProfileDTO> listData = new ArrayList<>();
            listData = repository.countProfileInDayByListState(state, transactionType, code, parameters);

            return listData;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    @Override
    public Integer countByStateAndType(Integer state, List<Integer> transactionType) {
        // TODO Auto-generated method stub
        try {
            Integer count = repository.countByStateAndType(state, transactionType);
            return count;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    @Override
    public Integer countInDayByStateAndUsername(Integer state, String username, List<Integer> transactionType) {
        // TODO Auto-generated method stub
        try {
            Integer count = repository.countInDayByStateAndUsername(state, username, transactionType);
            return count;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<ProfileDTO> countProfileByListState(List<Integer> state, String code, List<Integer> transactionType,
            Map<String, Object> parameters) {
        // TODO Auto-generated method stub
        try {
            List<ProfileDTO> listData = new ArrayList<>();
            // List<Profile> profiles = repository.countProfileByListState(state,
            // transactionType, code, parameters);
            listData = repository.countProfileByListState(state, transactionType, code, parameters);
            // if(DataUtils.notNullOrEmpty(profiles)) {
            // listData = mapper.toDtoBean(repository.countProfileByListState(state,
            // transactionType, code, parameters));
            // }
            return listData;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return null;
        }
    }

}
