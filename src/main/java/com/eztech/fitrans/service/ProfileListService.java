package com.eztech.fitrans.service;

import java.util.List;
import java.util.Map;

import com.eztech.fitrans.dto.response.ProfileListDTO;

public interface ProfileListService {

    ProfileListDTO save(ProfileListDTO ProfileList);

    void deleteById(Long id);

    ProfileListDTO findById(Long id);

    List<ProfileListDTO> findAll();

    List<ProfileListDTO> search(Map<String, Object> mapParam);

    Long count(Map<String, Object> mapParam);

    
}
