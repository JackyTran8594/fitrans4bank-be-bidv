package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import java.util.List;
import java.util.Map;

public interface ProfileHistoryService {

  ProfileHistoryDTO save(ProfileHistoryDTO product);

  ProfileHistoryDTO findById(Long id);

  List<ProfileHistoryDTO> findAll();

  ProfileHistoryDTO findByIdAndState(Long id, Integer state);



}
