package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.constants.ProfileTypeEnum;
import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.DepartmentDTO;
import com.eztech.fitrans.dto.response.MessageDTO;
import com.eztech.fitrans.dto.response.OptionSetValueDTO;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import com.eztech.fitrans.dto.response.TransactionTypeDTO;
import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.dto.response.dashboard.DashboardDTO;
import com.eztech.fitrans.dto.response.dashboard.ProfileListDashBoardDTO;
import com.eztech.fitrans.dto.response.report.ReportProfileDTO;
import com.eztech.fitrans.event.ScheduledTasks;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.Profile;
import com.eztech.fitrans.model.ProfileHistory;
import com.eztech.fitrans.model.ReportProfileView;
import com.eztech.fitrans.repo.ActionLogRepository;
import com.eztech.fitrans.repo.ProfileRepository;
import com.eztech.fitrans.repo.ReportRepository;
import com.eztech.fitrans.service.DepartmentService;
import com.eztech.fitrans.service.OptionSetService;
import com.eztech.fitrans.service.ProfileHistoryService;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.service.ReportService;
import com.eztech.fitrans.service.TransactionTypeService;
import com.eztech.fitrans.service.UserService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.CalculatingTime;
import com.eztech.fitrans.util.DataUtils;
import com.eztech.fitrans.util.ExcelFileWriter;
import com.eztech.fitrans.util.ReadAndWriteDoc;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    private static final BaseMapper<ReportProfileView, ReportProfileDTO> mapper = new BaseMapper<>(
            ReportProfileView.class,
            ReportProfileDTO.class);

    private static Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Value("${app.timeConfig}")
    private Double timeConfig;

    @Autowired
    private ProfileRepository repository;

    @Qualifier("ReportRepository")
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private OptionSetService optionSetService;

    @Autowired
    private CalculatingTime calculatingTime;

    @Override
    public List<ReportProfileDTO> search(Map<String, Object> mapParam) {
        // TODO Auto-generated method stub
        List<ReportProfileView> data = reportRepository.search(mapParam, false);
        List<ReportProfileDTO> dtos = mapper.toDtoBean(data);
        dtos = dtos.stream().map(value -> {
            value.setStateEnum(ProfileStateEnum.of(value.getState()).getName());
            // luồng 1
            if (value.getTransactionType().equals(1)) {
                if (!DataUtils.isNullOrEmpty(value.getTimeReceived_CT())) {
                    value.setEndTimeCM(value.getTimeReceived_CT());
                    value.setProcessDateCT(setProcessDateForCT(value.getTimeReceived_CT()));
                }
                if (!DataUtils.isNullOrEmpty(value.getEndTime())) {
                    value.setEndTimeCT(value.getEndTime());
                }
            }
            // luồng 2
            if (value.getTransactionType().equals(2)) {
                if (!DataUtils.isNullOrEmpty(value.getEndTime())) {
                    value.setEndTimeCM(value.getEndTime());
                }
            }
            // luồng 3
            if (value.getTransactionType().equals(3)) {
                if (!DataUtils.isNullOrEmpty(value.getTimeReceived_CT())) {
                    value.setEndTimeCM(value.getTimeReceived_CT());
                    value.setProcessDateCT(setProcessDateForCT(value.getTimeReceived_CT()));
                }
                if (!DataUtils.isNullOrEmpty(value.getEndTime())) {
                    value.setEndTimeCT(value.getEndTime());
                }
            }
            return value;
        }).collect(Collectors.toList());
        return dtos;
    }

    @Override
    public List<ReportProfileDTO> exportExcel(Map<String, Object> mapParam) {
        // TODO Auto-generated method stub
        try {
            List<ReportProfileView> data = reportRepository.search(mapParam, false);
            List<ReportProfileDTO> dtos = mapper.toDtoBean(data);
            dtos = dtos.stream().map(value -> {

                value.setStateEnum(ProfileStateEnum.of(value.getState()).getName());
                // luồng 1
                if (value.getTransactionType().equals(1)) {
                    if (!DataUtils.isNullOrEmpty(value.getTimeReceived_CT())) {
                        value.setEndTimeCM(value.getTimeReceived_CT());
                        value.setProcessDateCT(setProcessDateForCT(value.getTimeReceived_CT()));
                    }
                    if (!DataUtils.isNullOrEmpty(value.getEndTime())) {
                        value.setEndTimeCT(value.getEndTime());
                    }

                }
                // luồng 2
                if (value.getTransactionType().equals(2)) {
                    if (!DataUtils.isNullOrEmpty(value.getEndTime())) {
                        value.setEndTimeCM(value.getEndTime());
                    }
                }
                // luồng 3
                if (value.getTransactionType().equals(3)) {
                    if (!DataUtils.isNullOrEmpty(value.getTimeReceived_CT())) {
                        value.setEndTimeCM(value.getTimeReceived_CT());
                        value.setProcessDateCT(setProcessDateForCT(value.getTimeReceived_CT()));
                    }
                    if (!DataUtils.isNullOrEmpty(value.getEndTime())) {
                        value.setEndTimeCT(value.getEndTime());
                    }
                }

                return value;
            }).collect(Collectors.toList());

            return dtos;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    // set datetime thời gian xử lý của giao dịch khách hàng
    private LocalDateTime setProcessDateForCT(LocalDateTime timeReceived) {
        List<OptionSetValueDTO> optionSetValueDTOs = optionSetService.listByCode("KHUNG_THOI_GIAN");
        Double v0 = Double
                .valueOf(optionSetValueDTOs.stream().filter(x -> x.getName().equals("T0")).collect(Collectors.toList())
                        .get(0).getValue().toString());
        Double v1 = Double
                .valueOf(optionSetValueDTOs.stream().filter(x -> x.getName().equals("T1")).collect(Collectors.toList())
                        .get(0).getValue().toString());
        Double v2 = Double
                .valueOf(optionSetValueDTOs.stream().filter(x -> x.getName().equals("T2")).collect(Collectors.toList())
                        .get(0).getValue().toString());
        Double v3 = Double
                .valueOf(optionSetValueDTOs.stream().filter(x -> x.getName().equals("T3")).collect(Collectors.toList())
                        .get(0).getValue().toString());
        Double v4 = Double
                .valueOf(optionSetValueDTOs.stream().filter(x -> x.getName().equals("T4")).collect(Collectors.toList())
                        .get(0).getValue().toString());

        LocalDateTime T0 = calculatingTime.convertTimeMarkerWithTimeReceived(v0, timeReceived);
        LocalDateTime T1 = calculatingTime.convertTimeMarkerWithTimeReceived(v1, timeReceived);
        LocalDateTime T2 = calculatingTime.convertTimeMarkerWithTimeReceived(v2, timeReceived);
        LocalDateTime T3 = calculatingTime.convertTimeMarkerWithTimeReceived(v3, timeReceived);
        LocalDateTime T4 = calculatingTime.convertTimeMarkerWithTimeReceived(v4, timeReceived);

        LocalDateTime processDate = null;

        // T0 => today - thời gian nhận nhỏ hơn T0 -> hoàn thành trước T2
        if(timeReceived.isBefore(T0)) {
            processDate = T2;
        }
        // [T0;T1] => today - thời gian nhận từ T0 - T1 -> hoàn thành trước T3
        if(timeReceived.isBefore(T1) && timeReceived.isAfter(T0)) {
            processDate = T3;
        }
        // (T1;...] => next day
        if(timeReceived.isAfter(T1)) {
            // 1 (Sunday) to 7 (Saturday).
            // friday = 6
            if(timeReceived.getDayOfWeek().equals(6)) {
                processDate = T2.plusDays(3);
            } else if(timeReceived.getDayOfWeek().equals(7)) {
                processDate = T2.plusDays(2);
            } else {
                processDate = T2.plusDays(1);
            }

        }

        return processDate;
    }

}
