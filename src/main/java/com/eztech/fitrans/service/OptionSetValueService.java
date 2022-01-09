package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.OptionSetValueDTO;

public interface OptionSetValueService {

    OptionSetValueDTO save(OptionSetValueDTO product);

    void deleteById(Long id);

    void deleteByOptionSet(Long optionSetId);

    OptionSetValueDTO findById(Long id);

}
