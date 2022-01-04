package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.CustomerDTO;
import com.eztech.fitrans.dto.response.ErrorCodeEnum;
import com.eztech.fitrans.exception.InputInvalidException;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.locale.Translator;
import com.eztech.fitrans.model.Customer;
import com.eztech.fitrans.repo.CustomerRepository;
import com.eztech.fitrans.service.CustomerService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

  private static final BaseMapper<Customer, CustomerDTO> mapper = new BaseMapper<>(Customer.class,
      CustomerDTO.class);
  @Autowired
  private CustomerRepository repository;

  @Override
  public CustomerDTO save(CustomerDTO item) {
    validate(item);
    Customer entity;
    if (!DataUtils.nullOrZero(item.getId())) {
      CustomerDTO dto = findById(item.getId());
      if (dto == null) {
        throw new ResourceNotFoundException("Customer " + item.getId() + " not found");
      }
      dto.setCif(item.getCif());
      dto.setType(item.getType());
      dto.setName(item.getName());
      dto.setAddress(item.getAddress());
      dto.setTel(item.getTel());
      dto.setStatus(item.getStatus());
      entity = mapper.toPersistenceBean(dto);
    } else {
      entity = mapper.toPersistenceBean(item);
    }

    return mapper.toDtoBean(repository.save(entity));
  }

  @Override
  public void deleteById(Long id) {
    CustomerDTO dto = findById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("Customer " + id + " not found");
    }
    repository.deleteById(id);
  }

  @Override
  public CustomerDTO findById(Long id) {
    Optional<Customer> optional = repository.findById(id);
    if (optional.isPresent()) {
      return mapper.toDtoBean(optional.get());
    }
    return null;
  }

  @Override
  public List<CustomerDTO> findAll() {
    List<Customer> listData = repository.findAll();
    List<CustomerDTO> listrtn = mapper.toDtoBean(listData);
    listrtn.stream()
            .forEach(item -> item.fillTransient());
    return listrtn;
  }

  @Override
  public List<CustomerDTO> search(Map<String, Object> mapParam) {
    List<Customer> listData = repository.search(mapParam, Customer.class);
    List<CustomerDTO> listrtn = mapper.toDtoBean(listData);
    listrtn.stream()
            .forEach(item -> item.fillTransient());
    return listrtn;

  }

  @Override
  public Long count(Map<String, Object> mapParam) {
    return repository.count(mapParam);
  }

  public void validate(CustomerDTO item) {
    if (DataUtils.isNullOrEmpty(item.getCif())) {
      throw new InputInvalidException(ErrorCodeEnum.ER0003, Translator.toMessage(Constants.MessageParam.CIF));
    }

    if (DataUtils.notNullOrEmpty(item.getCif()) && item.getCif().length() > 50) {
      throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.CIF), 50);
    }

    if (DataUtils.isNullOrEmpty(item.getName())) {
      throw new InputInvalidException(ErrorCodeEnum.ER0003, Translator.toMessage(Constants.MessageParam.CUSTOMER_NAME));
    }

    if (DataUtils.notNullOrEmpty(item.getName()) && item.getName().length() > 100) {
      throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.CUSTOMER_NAME), 100);
    }

    if (DataUtils.notNullOrEmpty(item.getAddress()) && item.getAddress().length() > 512) {
      throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.CUSTOMER_ADDRESS), 512);
    }

    if (DataUtils.notNullOrEmpty(item.getTel()) && item.getTel().length() > 25) {
      throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.CUSTOMER_TEL), 25);
    }

    boolean checkExit = repository.checkExits(item.getId(),item.getCif());
    if (checkExit) {
      throw new InputInvalidException(ErrorCodeEnum.ER0009, Translator.toMessage(Constants.MessageParam.CIF));
    }
  }

}
