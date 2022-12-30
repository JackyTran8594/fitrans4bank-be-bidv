package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.dto.request.ChatMessage;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.dashboard.DashboardDTO;
import com.eztech.fitrans.event.ScheduledTasks;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.util.DataUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        // dự kiến xử lý chưa tới rổ chung
        List<Integer> stateExpect = Arrays.asList(new Integer[] { 0, 1 });
        Map<String, Object> paramsExpect = new HashMap<String, Object>();
        paramsExpect.put("time_received_ct", "NULL");
        List<ProfileDTO> profileExptect = service
        .countProfileInDayByListState(stateExpect, user.getCode(), transactionType, paramsExpect);
        dashboard.profileExpect.profilesAll = profileExptect;
        dashboard.profileExpect.profilesInDay = (DataUtils.notNullOrEmpty(profileExptect)) ? profileExptect.size() : 0;

        // đang ở rổ chung trong ngày
        List<Integer> stateReceived = Arrays.asList(new Integer[] { 2 });
        Map<String, Object> paramsRecevied = new HashMap<String, Object>();

        dashboard.profileReceived.profiles = (DataUtils.notNullOrEmpty(service
        .countProfileInDayByListState(stateReceived, user.getCode(), transactionType, paramsRecevied))) ? service
        .countProfileInDayByListState(stateReceived, user.getCode(), transactionType, paramsRecevied).size() : 0 ;


        // đang xử lý (bao gồm ca quá hạn và chưa quá hạn)
        List<Integer> stateProcessing = Arrays.asList(new Integer[] { 5 });
        Map<String, Object> paramsProcessing = new HashMap<String, Object>();
        dashboard.profileProcessing.profilesAll = service
                .countProfileInDayByListState(stateProcessing, user.getCode(), transactionType, paramsProcessing);

        // đã hoàn thành trong ngày (bao gồm ca quá hạn và chưa quá hạn)
        List<Integer> stateFinished = Arrays.asList(new Integer[] { 7 });
        Map<String, Object> paramsFinished = new HashMap<String, Object>();
        dashboard.profileToDayFinished.profilesAll = service
                .countProfileInDayByListState(stateFinished, user.getCode(), transactionType, paramsFinished);

        // đã hoàn thành lũy kế
        dashboard.profileFinished.profiles = service.countByState(7);

        // hồ sơ trả lại trong ngày
        dashboard.profileReturn.profiles = service.countProfileInDayByState(6);

        // tổng hồ sơ GDKH đã nhận
        List<Integer> state = Arrays.asList(new Integer[] { 4, 5, 6, 7, 8, 9 });
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("real_time_received_ct", "NOTNULL");
        dashboard.totalProfile.profilesInDay = (DataUtils.notNullOrEmpty(service.countProfileInDayByListState(state, user.getCode(), transactionType, params))) 
        ? service.countProfileInDayByListState(state, user.getCode(), transactionType, params).size() : 0;
        // lũy kế
        dashboard.totalProfile.profiles = (DataUtils.notNullOrEmpty(service.countProfileByListState(state, user.getCode(), transactionType, params))) 
        ? service.countProfileByListState(state, user.getCode(), transactionType, params).size() : 0;
        System.out.println(dashboard);
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
//         paramsExpect.put("time_received_cm", "NULL");
         dashboard.profileExpect.profiles = service
                 .countProfileInDayByListState(stateExpect, user.getCode(), transactionType, paramsExpect).size();
       
         // đang xử lý (bao gồm cả quá hạn và chưa quá hạn)
         List<Integer> stateProcessing = Arrays.asList(new Integer[] { 5 });
         Map<String, Object> paramsProcessing = new HashMap<String, Object>();
         List<ProfileDTO> profileprofileProcessingList = service
                 .countProfileInDayByListState(stateProcessing, user.getCode(), transactionType, paramsProcessing);
         dashboard.profileProcessing.profilesAll = profileprofileProcessingList;
         dashboard.profileProcessing.profilesExpired = Math.toIntExact(profileprofileProcessingList.stream().filter(x -> LocalDateTime.now().isAfter(x.processDate)).count());
         dashboard.profileProcessing.profilesNonExpired = profileprofileProcessingList.size() - dashboard.profileProcessing.profilesExpired;
         System.out.println(Math.toIntExact(profileprofileProcessingList.stream().filter(x -> LocalDateTime.now().isBefore(x.processDate)).count()));

         // đã hoàn thành trong ngày (bao gồm ca quá hạn và chưa quá hạn)
         List<Integer> stateFinished = Arrays.asList(new Integer[] { 7 });
         Map<String, Object> paramsFinished = new HashMap<String, Object>();
         List<ProfileDTO> profileFinishedList = service
                 .countProfileInDayByListState(stateFinished, user.getCode(), transactionType, paramsFinished);
         dashboard.profileToDayFinished.profilesAll = profileFinishedList;
         dashboard.profileToDayFinished.profilesExpired = Math.toIntExact(profileFinishedList.stream().filter(x -> x.endTime.isAfter(x.processDate)).count());
         dashboard.profileToDayFinished.profilesNonExpired = profileFinishedList.size() - dashboard.profileToDayFinished.profilesExpired;
         System.out.println(Math.toIntExact(profileFinishedList.stream().filter(x -> x.endTime.isBefore(x.processDate)).count()));

         // đã hoàn thành lũy kế
         dashboard.profileFinished.profiles = service.countByState(7);
 
         // hồ sơ trả lại trong ngày
         dashboard.profileReturn.profiles = service.countProfileInDayByState(6);
 
         // tổng hồ sơ QTTD đã nhận
         List<Integer> state = Arrays.asList(new Integer[] { 4, 5, 6, 7, 8, 9 });
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("real_time_received_cm", "NOTNULL");
         dashboard.totalProfile.profilesInDay = service.countProfileInDayByListState(state, user.getCode(), transactionType, params).size();
         //lũy kế
         dashboard.totalProfile.profiles = service.countProfileByListState(state, user.getCode(), transactionType, params).size();
         return dashboard;
     }

}