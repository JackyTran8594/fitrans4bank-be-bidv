package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.MessageDTO;
import com.eztech.fitrans.dto.response.ProfileDTO;

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

  List<ProfileDTO> getProfileDashboard(int topNumber);

  ProfileDTO confirmProfile(ConfirmRequest req);

  void deleteList(List<Long> ids);

  MessageDTO checkScanAgain(ConfirmRequest item);

  MessageDTO checkTransfer(ConfirmRequest item);

  MessageDTO checkIsReturn(ConfirmRequest item);

  MessageDTO priorityProfile(ConfirmRequest req);


}
