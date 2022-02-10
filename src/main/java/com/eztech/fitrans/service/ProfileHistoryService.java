package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import java.util.List;
import java.util.Map;

public interface ProfileHistoryService {

  ProfileHistoryDTO save(ProfileHistoryDTO product);

  void deleteById(Long id);

  ProfileHistoryDTO findById(Long id);

  ProfileHistoryDTO detailById(Long id);

  List<ProfileHistoryDTO> findAll();

  List<ProfileHistoryDTO> search(Map<String, Object> mapParam);

  Long count(Map<String, Object> mapParam);


}
