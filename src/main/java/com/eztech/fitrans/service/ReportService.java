package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.request.ChatMessage;
import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.MessageDTO;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.dashboard.DashboardDTO;

import com.eztech.fitrans.dto.response.dashboard.ProfileListDashBoardDTO;
import com.eztech.fitrans.dto.response.report.ReportProfileDTO;

import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ReportService {

    List<ReportProfileDTO> search(Map<String, Object> mapParam);
    List<ReportProfileDTO> exportExcel(Map<String, Object> mapParam);
}
