package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.PriorityCardDTO;
import com.eztech.fitrans.dto.response.dashboard.ProfileListDashBoardDTO;
import com.eztech.fitrans.model.PriorityCard;
import com.eztech.fitrans.model.ProfileListDashBoard;
import com.eztech.fitrans.repo.DashboardRepository;
import com.eztech.fitrans.service.DashboardService;
import com.eztech.fitrans.util.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private static final BaseMapper<ProfileListDashBoard, ProfileListDashBoardDTO> mapper = new BaseMapper<>(ProfileListDashBoard.class, ProfileListDashBoardDTO.class);

    private static Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Qualifier("DashboardRepository")
    @Autowired
    private DashboardRepository repository;

    @Override
    public List<ProfileListDashBoardDTO> profileInDayByListStateCM(List<Integer> state, String code, List<Integer> transactionType, Map<String, Object> parameters) {
        try {
            List<ProfileListDashBoard> listData = new ArrayList<>();
            listData = repository.profileInDayByListStateCM(state, transactionType, code, parameters);
            return mapper.toDtoBean(listData);
        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<ProfileListDashBoardDTO> profileInDayByListStateCT(List<Integer> state, String code, List<Integer> transactionType, Map<String, Object> parameters) {
        List<ProfileListDashBoard> listData = new ArrayList<>();
        listData = repository.profileInDayByListStateCT(state, transactionType, code, parameters);
        return mapper.toDtoBean(listData);
    }

    @Override
    public List<ProfileListDashBoardDTO> profileInDayByListStateCusMan(List<Integer> state, String code,
            List<Integer> transactionType,  Integer departmentId, Map<String, Object> parameters) {
        // TODO Auto-generated method stub
        List<ProfileListDashBoard> listData = new ArrayList<>();
        listData = repository.profileInDayByListStateCusMan(state, transactionType, code, departmentId, parameters);
        return mapper.toDtoBean(listData);
    }

}
