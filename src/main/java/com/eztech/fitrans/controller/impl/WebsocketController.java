package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.dto.request.ChatMessage;
import com.eztech.fitrans.dto.response.OptionSetDTO;
import com.eztech.fitrans.dto.response.OptionSetValueDTO;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.dashboard.DashboardDTO;
import com.eztech.fitrans.event.ScheduledTasks;
import com.eztech.fitrans.model.OptionSetValue;
import com.eztech.fitrans.service.DashboardService;
import com.eztech.fitrans.service.OptionSetService;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.util.CalculatingTime;
import com.eztech.fitrans.util.DataUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class WebsocketController {
        @Autowired
        @Qualifier("ayncTaskExecutor")
        private TaskExecutor threadPoolTaskExecutor;

        @Autowired
        private ScheduledTasks scheduledTasks;

        @Autowired
        private ProfileService service;

        @Autowired
        private OptionSetService optionSetService;

        @Autowired
        private DashboardService dashboardService;

        @Value("${app.timeConfig}")
        private Double timeConfig;

        @Autowired
        private CalculatingTime calculatingTime;

        // Web gui message init --> Tra ve list data hồ sơ cho web hiển thị dashboard
        // qua topic /topic/profiles
        @MessageMapping("/init")
        @SendTo("/topic/profiles")
        public List<ProfileDTO> init(ChatMessage user) throws Exception {
                List<ProfileDTO> listData = service.dashboard();
                return listData;
        }

        @MessageMapping("/initTest")
        @SendTo("/topic/test")
        public String initTest(ChatMessage user) throws Exception {
                return "test";
        }

        // hồ sơ của giao dịch khách hàng - GDKH
        @MessageMapping("/init_profiles_CT")
        @SendTo("/topic/profiles_CT")
        public DashboardDTO profiles_CT(ChatMessage user) throws Exception {
                DashboardDTO dashboard = new DashboardDTO();
                List<Integer> transactionType = Arrays.asList(new Integer[] { 1, 3 });
                // dự kiến xử lý chưa tới rổ chung : 0,1 - QLKH chưa bàn giao ; 4,5 - QTTD đang
                // và chờ xử lý + time_received_ct = NUll;
                // 6: trả hồ sơ
                List<Integer> stateExpect = Arrays.asList(new Integer[] { 0, 1, 4, 5, 6 });
                Map<String, Object> paramsExpect = new HashMap<String, Object>();
                paramsExpect.put("time_received_ct", "NULL");
                List<ProfileDTO> profileExptect = service
                                .countProfileInDayByListState(stateExpect, user.getCode(), transactionType,
                                                paramsExpect);
                dashboard.profileExpect.profilesAll = profileExptect;
                dashboard.profileExpect.profilesInDay = (DataUtils.notNullOrEmpty(profileExptect))
                                ? profileExptect.size()
                                : 0;

                // đang ở rổ chung trong ngày
                List<Integer> stateReceived = Arrays.asList(new Integer[] { 2 });
                Map<String, Object> paramsRecevied = new HashMap<String, Object>();
                List<ProfileDTO> profileExptectReceived = service
                                .countProfileInDayByListState(stateReceived, user.getCode(), transactionType,
                                                paramsRecevied);
                dashboard.profileReceived.profiles = (DataUtils.notNullOrEmpty(profileExptectReceived))
                                ? profileExptectReceived.size()
                                : 0;
                dashboard.profileReceived.profilesAll = profileExptectReceived;

                // đang xử lý (bao gồm ca quá hạn và chưa quá hạn)
                List<Integer> stateProcessing = Arrays.asList(new Integer[] { 5 });
                Map<String, Object> paramsProcessing = new HashMap<String, Object>();
                dashboard.profileProcessing.profilesAll = service
                                .countProfileInDayByListState(stateProcessing, user.getCode(), transactionType,
                                                paramsProcessing);
                dashboard.profileProcessing.profiles = dashboard.profileProcessing.profilesAll.size();

                // đã hoàn thành trong ngày (bao gồm ca quá hạn và chưa quá hạn)
                List<Integer> stateFinished = Arrays.asList(new Integer[] { 7 });
                Map<String, Object> paramsFinished = new HashMap<String, Object>();
                dashboard.profileToDayFinished.profilesAll = service
                                .countProfileInDayByListState(stateFinished, user.getCode(), transactionType,
                                                paramsFinished);
                dashboard.profileToDayFinished.profiles = dashboard.profileToDayFinished.profilesAll.size();

                // đã hoàn thành lũy kế
                dashboard.profileFinished.profiles = service.countByStateAndType(7, transactionType);
                List<Integer> stateFinishedAll = Arrays.asList(new Integer[] { 7 });
                Map<String, Object> paramsFinishedAll = new HashMap<String, Object>();
                dashboard.profileFinished.profiles = service.countProfileByListState(stateFinishedAll,
                                user.getCode(), transactionType, paramsFinishedAll).size();
                // dashboard.profileFinished.profilesAll =
                // service.countProfileByListState(stateFinishedAll,
                // user.getCode(), transactionType, paramsFinishedAll);

                // hồ sơ trả lại trong ngày
                List<Integer> stateReturn = Arrays.asList(new Integer[] { 6 });
                Map<String, Object> paramsReturn = new HashMap<String, Object>();
                List<ProfileDTO> profileReturnList = service.countProfileInDayByListState(stateReturn, user.getCode(),
                                transactionType, paramsReturn);
                dashboard.profileReturn.profiles = profileReturnList.size();
                dashboard.profileReturn.profilesAll = profileReturnList;

                // tổng hồ sơ GDKH đã nhận: state = 2,5,7
                List<Integer> state = Arrays.asList(new Integer[] { 2, 5, 7 });
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("real_time_received_ct", "NOTNULL");
                dashboard.totalProfile.profilesInDay = (DataUtils.notNullOrEmpty(
                                service.countProfileInDayByListState(state, user.getCode(), transactionType, params)))
                                                ? service.countProfileInDayByListState(state, user.getCode(),
                                                                transactionType, params).size()
                                                : 0;
                // lũy kế
                dashboard.totalProfile.profiles = (DataUtils.notNullOrEmpty(
                                service.countProfileByListState(state, user.getCode(), transactionType, params)))
                                                ? service.countProfileByListState(state, user.getCode(),
                                                                transactionType, params).size()
                                                : 0;
                System.out.println(dashboard);

                // danh sách cán bộ giao dịch khách hàng đang xử lý hồ sơ
                List<Integer> stateCTProcessing = Arrays.asList(new Integer[] { 5 });
                Map<String, Object> paramsListCTProcessing = new HashMap<String, Object>();
                dashboard.profileListCTProcessing = dashboardService.profileInDayByListStateCT(stateCTProcessing,
                                user.getCode(), transactionType, paramsListCTProcessing);

                // danh sách cán bộ giao dịch khách hàng để hồ sơ tồn trước đó => trạng thái là
                // đã nhận
                List<Integer> stateCTExist = Arrays.asList(new Integer[] { 2 });
                Map<String, Object> paramsListCTExist = new HashMap<String, Object>();
                dashboard.profileListCTExist = dashboardService.profileInDayByListStateCT(stateCTExist, user.getCode(),
                                transactionType, paramsListCTExist);

                return dashboard;
        }

        // hồ sơ của quản trị tín dụng
        @MessageMapping("/init_profiles_CM")
        @SendTo("/topic/profiles_CM")
        public DashboardDTO profiles_CM(ChatMessage user) throws Exception {
                DashboardDTO dashboard = new DashboardDTO();
                List<Integer> transactionType = Arrays.asList(new Integer[] { 1, 2 });
                // dự kiến xử lý
                List<Integer> stateExpect = Arrays.asList(new Integer[] { 0, 1 });
                Map<String, Object> paramsExpect = new HashMap<String, Object>();
                // paramsExpect.put("time_received_cm", "NULL");
                List<ProfileDTO> profileExpectList = service
                                .countProfileInDayByListState(stateExpect, user.getCode(), transactionType,
                                                paramsExpect);
                dashboard.profileExpect.profiles = profileExpectList.size();
                // dashboard.profileExpect.profilesAll = profileExpectList;

                // đang xử lý (bao gồm cả quá hạn và chưa quá hạn)
                List<Integer> stateProcessing = Arrays.asList(new Integer[] { 5 });
                Map<String, Object> paramsProcessing = new HashMap<String, Object>();
                LocalDateTime currentDateTime = LocalDateTime.now();
                List<ProfileDTO> profileProcessingList = service
                                .countProfileInDayByListState(stateProcessing, user.getCode(), transactionType,
                                                paramsProcessing);
                dashboard.profileProcessing.profiles = profileProcessingList.size();
                dashboard.profileProcessing.numberOfProfilesExpired = profileProcessingList.stream()
                                .filter(x -> x.processDate.isAfter(currentDateTime)).collect(Collectors.toList())
                                .size();
                dashboard.profileToDayFinished.numberOfProfilesNonExpired = profileProcessingList.stream()
                                .filter(x -> x.processDate.isBefore(currentDateTime)).collect(Collectors.toList())
                                .size();

                // dashboard.profileProcessing.profilesAll = profileProcessingList;
                // dashboard.profileProcessing.profilesExpired = profileProcessingList.stream()
                // .filter(x ->
                // LocalDateTime.now().isAfter(x.processDate)).collect(Collectors.toList());
                // dashboard.profileProcessing.profilesNonExpired =
                // profileProcessingList.stream()
                // .filter(x ->
                // LocalDateTime.now().isBefore(x.processDate)).collect(Collectors.toList());
                // System.out.println(Math.toIntExact(profileProcessingList.stream().filter(x ->
                // LocalDateTime.now().isBefore(x.processDate)).count()));

                // đã hoàn thành trong ngày (bao gồm ca quá hạn và chưa quá hạn)
                List<Integer> stateFinished = Arrays.asList(new Integer[] { 7 });
                Map<String, Object> paramsFinished = new HashMap<String, Object>();
                List<ProfileDTO> profileToDayFinishedList = service
                                .countProfileInDayByListState(stateFinished, user.getCode(), transactionType,
                                                paramsFinished);
                dashboard.profileToDayFinished.profiles = profileToDayFinishedList.size();
                dashboard.profileToDayFinished.numberOfProfilesExpired = profileToDayFinishedList.stream()
                                .filter(x -> x.endTime.isAfter(x.processDate)).collect(Collectors.toList()).size();
                dashboard.profileToDayFinished.numberOfProfilesNonExpired = profileToDayFinishedList.stream()
                                .filter(x -> x.endTime.isBefore(x.processDate)).collect(Collectors.toList()).size();
                // dashboard.profileToDayFinished.profilesAll = profileToDayFinishedList;
                // dashboard.profileToDayFinished.profilesExpired =
                // profileToDayFinishedList.stream()
                // .filter(x -> x.endTime.isAfter(x.processDate)).collect(Collectors.toList());
                // dashboard.profileToDayFinished.profilesNonExpired =
                // profileToDayFinishedList.stream()
                // .filter(x -> x.endTime.isBefore(x.processDate)).collect(Collectors.toList());

                // đã hoàn thành lũy kế
                dashboard.profileFinished.profiles = service.countByStateAndType(7, transactionType);
                List<Integer> stateFinishedAll = Arrays.asList(new Integer[] { 7 });
                Map<String, Object> paramsFinishedAll = new HashMap<String, Object>();
                List<ProfileDTO> profileFinishedList = service.countProfileByListState(stateFinishedAll, user.getCode(),
                                transactionType, paramsFinishedAll);
                dashboard.profileFinished.profiles = profileFinishedList.size();

                // dashboard.profileFinished.profilesAll = profileFinishedList;
                // dashboard.profileFinished.profilesExpired = profileFinishedList.stream()
                // .filter(x -> x.endTime.isAfter(x.processDate)).collect(Collectors.toList());
                // dashboard.profileFinished.profilesNonExpired = profileFinishedList.stream()
                // .filter(x -> x.endTime.isBefore(x.processDate)).collect(Collectors.toList());

                // hồ sơ trả lại trong ngày
                List<Integer> stateReturn = Arrays.asList(new Integer[] { 6 });
                Map<String, Object> paramsReturn = new HashMap<String, Object>();
                List<ProfileDTO> profileReturnList = service.countProfileInDayByListState(stateReturn, user.getCode(),
                                transactionType, paramsReturn);
                dashboard.profileReturn.profiles = profileReturnList.size();
                // dashboard.profileReturn.profilesAll = profileReturnList;

                // tổng hồ sơ QTTD đã nhận
                List<Integer> state = Arrays.asList(new Integer[] { 4, 5, 6, 7, 8, 9 });
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("real_time_received_cm", "NOTNULL");
                dashboard.totalProfile.profilesInDay = service
                                .countProfileInDayByListState(state, user.getCode(), transactionType, params).size();
                // lũy kế
                dashboard.totalProfile.profiles = service
                                .countProfileByListState(state, user.getCode(), transactionType, params).size();

                // danh sách cán bộ quản trị tín dụng đang xử lý hồ sơ
                List<Integer> stateCMProcessing = Arrays.asList(new Integer[] { 5 });
                Map<String, Object> paramsListCMProcessing = new HashMap<String, Object>();
                dashboard.profileListCMProcessing = dashboardService.profileInDayByListStateCM(stateCMProcessing,
                                user.getCode(), transactionType, paramsListCMProcessing);
                dashboard.profileListCMProcessing.forEach(x -> {
                        x.profileProcessed = service.countInDayByStateAndUsername(7, x.username, transactionType);
                });

                // danh sách cán bộ quản trị tín dụng để hồ sơ tồn trước đó
                List<Integer> stateCMExist = Arrays.asList(new Integer[] { 4 });
                Map<String, Object> paramsListCMExist = new HashMap<String, Object>();
                dashboard.profileListCMExist = dashboardService.profileInDayByListStateCM(stateCMExist, user.getCode(),
                                transactionType, paramsListCMExist);

                return dashboard;
        }

        // hồ sơ của quản lý khách hàng: member
        @MessageMapping("/init_profiles_CusMan_Member")
        @SendTo("/topic/profiles_CusMan_Member")
        public DashboardDTO profiles_CusMan_Member(ChatMessage user) throws Exception {
                DashboardDTO dashboard = new DashboardDTO();
                List<Integer> transactionType = Arrays.asList(new Integer[] { 1, 3 });

                // hồ sơ trả lại
                List<Integer> stateReturn = Arrays.asList(new Integer[] { 6 });
                Map<String, Object> paramsReturn = new HashMap<String, Object>();
                // toàn bộ hồ sơ
                List<ProfileDTO> profileReturnList = service.countProfileInDayByListState(stateReturn, user.getCode(),
                                transactionType, paramsReturn);

                // hồ sơ trả lại trong ngày
                paramsReturn.put("isToday", true);
                paramsReturn.put("departmentId", user.getDepartmentId());
                List<ProfileDTO> profileReturnListInDay = service.countProfileInDayByListState(stateReturn,
                                user.getCode(),
                                transactionType, paramsReturn);
                dashboard.profileReturn.profilesInday = (DataUtils.notNull(profileReturnList)
                                && profileReturnList.size() > 0) ? profileReturnList.size() : 0;
                dashboard.profileReturn.profileExistInDay = (DataUtils.notNull(profileReturnListInDay)
                                && profileReturnListInDay.size() > 0) ? profileReturnListInDay.size() : 0;
                dashboard.profileReturn.profileExist = dashboard.profileReturn.profilesInday
                                - dashboard.profileReturn.profileExistInDay;

                // hồ sơ đã tạo
                // tất cả các state
                List<Integer> stateCreate = Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
                Map<String, Object> paramsCreate = new HashMap<String, Object>();
                // toàn bộ hồ sơ
                // List<ProfileDTO> profileCreatedList =
                // service.countProfileInDayByListState(stateCreate, user.getCode(),
                // transactionType, paramsCreate);

                // hồ sơ đã tạo trong ngày
                paramsCreate.put("isToday", true);
                paramsCreate.put("departmentId", user.getDepartmentId());
                List<ProfileDTO> profileCreatedListInDay = service.countProfileInDayByListState(stateCreate,
                                user.getCode(),
                                transactionType, paramsCreate);

                // hồ sơ đã hoàn thành trong ngày
                List<Integer> stateFinish = Arrays.asList(new Integer[] { 7 });
                List<ProfileDTO> profilesFinishedInDay = service.countProfileInDayByListState(stateFinish,
                                user.getCode(),
                                transactionType, paramsCreate);
                dashboard.profileFinished.profilesInday = (DataUtils.notNull(profileCreatedListInDay)
                                && profileCreatedListInDay.size() > 0) ? profileCreatedListInDay.size() : 0;
                dashboard.profileFinished.numberOfProfilesFinished = (DataUtils.notNull(profilesFinishedInDay)
                                && profilesFinishedInDay.size() > 0) ? profilesFinishedInDay.size() : 0;
                dashboard.profileFinished.numberOfProfilesNotFinished = profileCreatedListInDay.size()
                                - dashboard.profileFinished.numberOfProfilesFinished;

                // hồ sơ tạo ngoài giờ trong ngày của QTTD
                LocalDateTime timeMarkerConfig = calculatingTime.convertTimeMarker(timeConfig);
                dashboard.profileCreated.profileCreatedOutOfTime_CM = profileCreatedListInDay.stream()
                                .filter(x -> x.getCreatedDate().isAfter(timeMarkerConfig)).collect(Collectors.toList())
                                .size();
                // hồ sơ tạo ngoài giờ trong ngày của GDKH
                OptionSetDTO optionSetDTO = optionSetService.detailByCode("KHUNG_THOI_GIAN");
                List<OptionSetValueDTO> optionSetValueDTO = optionSetDTO.getOptionSetValueDTOList().stream()
                                .filter(x -> x.getName().equals("T4")).collect(Collectors.toList());

                if (DataUtils.notNull(optionSetValueDTO) && optionSetValueDTO.size() > 0) {
                        Double timeConfig_CT = Double.parseDouble(optionSetValueDTO.get(0).getValue());
                        LocalDateTime timeMarkerConfig_CT = calculatingTime.convertTimeMarker(timeConfig_CT);
                        if (DataUtils.notNull(timeMarkerConfig_CT)) {
                                dashboard.profileCreated.profileCreatedOutOfTime_CT = profileCreatedListInDay.stream()
                                                .filter(x -> x.getCreatedDate().isAfter(timeMarkerConfig_CT))
                                                .collect(Collectors.toList())
                                                .size();
                        }

                }

                return dashboard;
        }

        // hồ sơ của quản lý khách hàng: boss
        @MessageMapping("/init_profiles_CusMan_Boss")
        @SendTo("/topic/profiles_CusMan_Boss")
        public DashboardDTO profiles_CusMan_Boss(ChatMessage user) throws Exception {
                DashboardDTO dashboard = new DashboardDTO();
                List<Integer> transactionType = Arrays.asList(new Integer[] { 1, 2, 3 });

                Map<String, Object> paramsCreate = new HashMap<String, Object>();

                paramsCreate.put("isToday", true);
                paramsCreate.put("departmentId", user.getDepartmentId());
                // hồ sơ chưa bàn giao
                List<Integer> stateNotTransfer = Arrays.asList(new Integer[] { 0, 1 });
                List<ProfileDTO> profileCreatedListInDay = service.countProfileInDayByListState(stateNotTransfer,
                                user.getCode(),
                                transactionType, paramsCreate);
                dashboard.profileNotReceived.profilesInday = profileCreatedListInDay.size();

                // hồ sơ chờ xử lý
                List<Integer> stateWaiting = Arrays.asList(new Integer[] { 4 });
                List<ProfileDTO> profileWaitingListInDay = service.countProfileInDayByListState(stateWaiting,
                                user.getCode(),
                                transactionType, paramsCreate);
                dashboard.profileWaiting.profilesInday = profileWaitingListInDay.size();

                // hồ sơ đang xử lý
                List<Integer> stateProcessing = Arrays.asList(new Integer[] { 5 });
                List<ProfileDTO> profileProcessingListInDay = service.countProfileInDayByListState(stateProcessing,
                                user.getCode(),
                                transactionType, paramsCreate);
                dashboard.profileProcessing.profilesInday = profileProcessingListInDay.size();

                // hồ sơ trả lại trong ngày - hồ sơ cần bổ sung
                List<Integer> stateReturn = Arrays.asList(new Integer[] { 6 });
                List<ProfileDTO> profileReturnListInDay = service.countProfileInDayByListState(stateReturn,
                                user.getCode(),
                                transactionType, paramsCreate);
                dashboard.profileReturn.profilesInday = (DataUtils.notNull(profileReturnListInDay)
                                && profileReturnListInDay.size() > 0) ? profileReturnListInDay.size() : 0;

                // hồ sơ đã hoàn thành trong ngày
                List<Integer> stateFinish = Arrays.asList(new Integer[] { 7 });
                List<ProfileDTO> profilesFinishedInDay = service.countProfileInDayByListState(stateFinish,
                                user.getCode(),
                                transactionType, paramsCreate);
                dashboard.profileToDayFinished.profilesInday = profilesFinishedInDay.size();

                // danh sách hồ sơ phòng quản trị tín dụng đang xử lý
                List<Integer> stateCMProcessing = Arrays.asList(new Integer[] { 5 });
                List<Integer> transactionTypeCM = Arrays.asList(new Integer[] { 1, 2 });
                dashboard.profileListCMProcessing = dashboardService.profileInDayByListStateCM(stateCMProcessing,
                                "QTTD", transactionTypeCM, paramsCreate);
                // dashboard.profileListCMProcessing.forEach(x -> {
                // x.profileProcessed = service.countInDayByStateAndUsername(7, x.username,
                // transactionType);
                // });

                // danh sách hồ sơ của từng cán bộ phòng KHDN trong ngày
                List<Integer> stateCreate = Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
                paramsCreate.put("position", user.getPosition());
                dashboard.profileListCusManInDay = dashboardService.profileInDayByListStateCusMan(stateCreate,
                                user.getCode(), transactionType, user.getDepartmentId() ,paramsCreate);

                return dashboard;
        }

}