package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.ProfileDTO;
import java.util.List;
import java.util.Map;

public interface ProfileService {

  ProfileDTO save(ProfileDTO product);

  void deleteById(Long id);

  ProfileDTO findById(Long id);

  ProfileDTO detailById(Long id);

  List<ProfileDTO> findAll();

  List<ProfileDTO> search(Map<String, Object> mapParam);

  Long count(Map<String, Object> mapParam);
}
