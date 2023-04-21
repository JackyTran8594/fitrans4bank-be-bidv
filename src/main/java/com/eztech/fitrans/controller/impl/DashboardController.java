package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.UserApi;
import com.eztech.fitrans.dto.request.ChatMessage;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.dto.response.dashboard.DashboardDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.DashboardService;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.service.UserService;
import com.eztech.fitrans.util.DataUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController extends BaseController {

  @Autowired
  private ProfileService service;

  @Autowired
  private DashboardService dashboardService;

  @GetMapping("/profileDetailCM")
  public DashboardDTO profileDetail_CM(@RequestParam Map<String, Object> params) {
    String code = params.get("code").toString();
    Integer cardType = Integer.parseInt(params.get("cardType").toString());
    DashboardDTO dashboard = new DashboardDTO();
    List<Integer> transactionType = Arrays.asList(new Integer[] { 1, 2 });

    switch (cardType) {
      case 2:
        // dự kiến xử lý
        List<Integer> stateExpect = Arrays.asList(new Integer[] { 0, 1 });
        Map<String, Object> paramsExpect = new HashMap<String, Object>();
        List<ProfileDTO> profileExpectList = service
            .countProfileInDayByListState(stateExpect, code,
                transactionType, paramsExpect);
        dashboard.profileExpect.profiles = profileExpectList.size();
        dashboard.profileExpect.profilesAll = profileExpectList;
        break;

      case 3:
      case 4:
        // đang xử lý (bao gồm cả quá hạn và chưa quá hạn)
        List<Integer> stateProcessing = Arrays.asList(new Integer[] { 5 });
        Map<String, Object> paramsProcessing = new HashMap<String, Object>();
        List<ProfileDTO> profileProcessingList = service
            .countProfileInDayByListState(stateProcessing, code,
                transactionType,
                paramsProcessing);
        dashboard.profileProcessing.profilesAll = profileProcessingList;
        dashboard.profileProcessing.profilesExpired = profileProcessingList.stream()
            .filter(x -> LocalDateTime.now().isAfter(x.processDate))
            .collect(Collectors.toList());
        dashboard.profileProcessing.profilesNonExpired = profileProcessingList.stream()
            .filter(x -> LocalDateTime.now().isBefore(x.processDate))
            .collect(Collectors.toList());
        break;
      case 5:
        // đã hoàn thành trong ngày (bao gồm ca quá hạn và chưa quá hạn)
        List<Integer> stateFinished = Arrays.asList(new Integer[] { 7 });
        Map<String, Object> paramsFinished = new HashMap<String, Object>();
        List<ProfileDTO> profileToDayFinishedList = service
            .countProfileInDayByListState(stateFinished, code,
                transactionType,
                paramsFinished);
        dashboard.profileToDayFinished.profilesAll = profileToDayFinishedList;
        dashboard.profileToDayFinished.profilesExpired = profileToDayFinishedList.stream()
            .filter(x -> x.endTime.isAfter(x.processDate))
            .collect(Collectors.toList());
        dashboard.profileToDayFinished.profilesNonExpired = profileToDayFinishedList.stream()
            .filter(x -> x.endTime.isBefore(x.processDate))
            .collect(Collectors.toList());
        break;
      case 6:
        // đã hoàn thành lũy kế
        dashboard.profileFinished.profiles = service.countByStateAndType(7, transactionType);
        List<Integer> stateFinishedAll = Arrays.asList(new Integer[] { 7 });
        Map<String, Object> paramsFinishedAll = new HashMap<String, Object>();
        List<ProfileDTO> profileFinishedList = service.countProfileByListState(stateFinishedAll,
            code,
            transactionType, paramsFinishedAll);
        dashboard.profileFinished.profilesAll = profileFinishedList;
        dashboard.profileFinished.profilesExpired = profileFinishedList.stream()
            .filter(x -> x.endTime.isAfter(x.processDate))
            .collect(Collectors.toList());
        dashboard.profileFinished.profilesNonExpired = profileFinishedList.stream()
            .filter(x -> x.endTime.isBefore(x.processDate))
            .collect(Collectors.toList());

        break;

      case 7:
        // hồ sơ trả lại trong ngày
        List<Integer> stateReturn = Arrays.asList(new Integer[] { 6 });
        Map<String, Object> paramsReturn = new HashMap<String, Object>();
        List<ProfileDTO> profileReturnList = service.countProfileInDayByListState(stateReturn,
            code,
            transactionType, paramsReturn);
        dashboard.profileReturn.profiles = profileReturnList.size();
        dashboard.profileReturn.profilesAll = profileReturnList;
        break;

      default:
        break;
    }

    return dashboard;
  }

  @GetMapping("/profileDetailCT")
  public DashboardDTO profileDetail_CT(@RequestParam Map<String, Object> params) {
    DashboardDTO dashboard = new DashboardDTO();
    List<Integer> transactionType = Arrays.asList(new Integer[] { 1, 3 });
    String code = params.get("code").toString();
    Integer cardType = Integer.parseInt(params.get("cardType").toString());
    switch (cardType) {
      case 2:
        // dự kiến xử lý chưa tới rổ chung : 0,1 - QLKH chưa bàn giao ; 4,5 - QTTD đang
        // và chờ xử lý + time_received_ct = NUll;
        // 6: trả hồ sơ
        List<Integer> stateExpect = Arrays.asList(new Integer[] { 0, 1, 4, 5, 6 });
        Map<String, Object> paramsExpect = new HashMap<String, Object>();
        paramsExpect.put("time_received_ct", "NULL");
        List<ProfileDTO> profileExptect = service
            .countProfileInDayByListState(stateExpect, code,
                transactionType,
                paramsExpect);
        // dashboard.profileExpect.profilesAll = profileExptect.stream()
        //     .sorted((a, b) -> a.getTimeReceived_CT().compareTo(b.getTimeReceived_CT())).collect(Collectors.toList());

        dashboard.profileExpect.profilesAll = profileExptect;
        
        dashboard.profileExpect.profilesInDay = (DataUtils.notNullOrEmpty(profileExptect))
            ? profileExptect.size()
            : 0;
        break;
      case 3:
        // đang ở rổ chung trong ngày
        List<Integer> stateReceived = Arrays.asList(new Integer[] { 2 });
        Map<String, Object> paramsRecevied = new HashMap<String, Object>();
        List<ProfileDTO> profileExptectReceived = service
            .countProfileInDayByListState(stateReceived, code,
                transactionType,
                paramsRecevied);
        dashboard.profileReceived.profiles = (DataUtils.notNullOrEmpty(profileExptectReceived))
            ? profileExptectReceived.size()
            : 0;
        dashboard.profileReceived.profilesAll = profileExptectReceived;
        break;
      case 4:
      case 5:
        // đang xử lý (bao gồm ca quá hạn và chưa quá hạn)
        List<Integer> stateProcessing = Arrays.asList(new Integer[] { 5 });
        Map<String, Object> paramsProcessing = new HashMap<String, Object>();
        dashboard.profileProcessing.profilesAll = service
            .countProfileInDayByListState(stateProcessing, code,
                transactionType,
                paramsProcessing);
        break;
      case 6:
        // đã hoàn thành trong ngày (bao gồm ca quá hạn và chưa quá hạn)
        List<Integer> stateFinished = Arrays.asList(new Integer[] { 7 });
        Map<String, Object> paramsFinished = new HashMap<String, Object>();
        dashboard.profileToDayFinished.profilesAll = service
            .countProfileInDayByListState(stateFinished, code,
                transactionType,
                paramsFinished);

        break;
      case 7:
        // đã hoàn thành lũy kế
        dashboard.profileFinished.profiles = service.countByStateAndType(7, transactionType);
        List<Integer> stateFinishedAll = Arrays.asList(new Integer[] { 7 });
        Map<String, Object> paramsFinishedAll = new HashMap<String, Object>();
        dashboard.profileFinished.profilesAll = service.countProfileByListState(
            stateFinishedAll,
            code, transactionType, paramsFinishedAll);
        break;
      case 8:
        // hồ sơ trả lại trong ngày
        List<Integer> stateReturn = Arrays.asList(new Integer[] { 6 });
        Map<String, Object> paramsReturn = new HashMap<String, Object>();
        List<ProfileDTO> profileReturnList = service.countProfileInDayByListState(stateReturn,
            code,
            transactionType, paramsReturn);
        dashboard.profileReturn.profiles = profileReturnList.size();
        dashboard.profileReturn.profilesAll = profileReturnList;
        break;
      default:
        break;
    }
    return dashboard;
  }

  @GetMapping("/dashboard/profileDetailCusMan")
  public DashboardDTO profileDetail_CusMan(@RequestParam Map<String, Object> params) {
    DashboardDTO dashboard = new DashboardDTO();
    List<Integer> transactionType = Arrays.asList(new Integer[] { 1, 2 });
    return dashboard;

  }

}