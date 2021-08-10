package com.eztech.fitrans.ecommerce.service;

import com.eztech.fitrans.ecommerce.DTO.CountryDTO;
import com.eztech.fitrans.ecommerce.repository.CountryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class CountryService {
    private ModelMapper modelMapper;
    private CountryRepository countryRepository;

    @Autowired
    CountryService(CountryRepository countryRepository,
                   ModelMapper modelMapper) {
        this.countryRepository = countryRepository;
        this.modelMapper = modelMapper;
    }

    public List<CountryDTO> getList() {
        return countryRepository.findAllByOrderByNameAsc().stream()
                .map(country -> modelMapper.map(country, CountryDTO.class))
                .collect(toList());
    }
}
