package com.eztech.fitrans.service;

import com.eztech.fitrans.dto.response.OptionSetValueDTO;

public interface OptionSetValueService {

    OptionSetValueDTO save(OptionSetValueDTO product);

    void deleteById(Long id);

    OptionSetValueDTO findById(Long id);

}
