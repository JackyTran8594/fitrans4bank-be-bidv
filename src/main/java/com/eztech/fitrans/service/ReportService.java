package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.request.ChatMessage;
import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.MessageDTO;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.dashboard.DashboardDTO;

import com.eztech.fitrans.dto.response.dashboard.ProfileListDashBoardDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ReportService {

    List<ProfileDTO> search(Map<String, Object> mapParam);

    
}
