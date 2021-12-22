package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.dto.response.CustomerDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
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
}
