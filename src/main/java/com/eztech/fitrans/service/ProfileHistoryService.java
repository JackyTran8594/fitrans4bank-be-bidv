package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import java.util.List;
import java.util.Map;

public interface ProfileHistoryService {

  ProfileHistoryDTO save(ProfileHistoryDTO product);

  ProfileHistoryDTO findById(Long id);

  List<ProfileHistoryDTO> findAll();

  List<ProfileHistoryDTO> findByIdAndState(Long id, List<Integer> state);

  List<ProfileHistoryDTO> profileHistoryDetail(Long id);

  void deleteByProfileId(Long id);

  void deleteListByProfileId(List<Long> ids);

}
