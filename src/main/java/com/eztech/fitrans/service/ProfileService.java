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

public interface ProfileService {

    ProfileDTO save(ProfileDTO profile);

    ProfileDTO saveHistory(ConfirmRequest confirmRequest);

    ProfileDTO transferInternal(ConfirmRequest confirmRequest);

    void deleteById(Long id);

    ProfileDTO findById(Long id);

    ProfileDTO detailByIdAndState(Long id, Integer state);

    List<ProfileDTO> findAll();

    List<ProfileDTO> search(Map<String, Object> mapParam);

    Long count(Map<String, Object> mapParam);

    List<ProfileDTO> dashboard();

    List<ProfileDTO> getProfileDashboard(Map<String, Object> paramSearch);

    ProfileDTO confirmProfile(ConfirmRequest req);

    Boolean deleteList(List<Long> ids);

    MessageDTO checkScanAgain(ConfirmRequest item);

    MessageDTO checkTransfer(ConfirmRequest item);

    MessageDTO checkIsReturn(ConfirmRequest item);

    MessageDTO priorityProfile(ConfirmRequest req);

    long countProfile(List<Integer> listState);

    Integer countProfileInday();

    Integer count();

    Integer countProfileInDayByState(Integer state);

    Integer countByStateAndType(Integer state, List<Integer> transactionType);

    /**
     * hàm dùng cho đếm số lượng hò sơ theo state đối với QTTD
     * @param state
     * @param username
     * @param transactionType
     * @return
     */
    Integer countInDayByStateAndUsername(Integer state, String username, List<Integer> transactionType);

    List<ProfileDTO> countProfileByListState(List<Integer> state, String code, List<Integer> transactionType, Map<String, Object> parameters);

    List<ProfileDTO> countProfileInDayByListState(List<Integer> state, String code, List<Integer> transactionType, Map<String, Object> parameters);

    List<DashboardDTO> profileExpected();




}
