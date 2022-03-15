package com.eztech.fitrans.service.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.constants.ExcelSection;
import com.eztech.fitrans.dto.ExcelFieldDTO;
import com.eztech.fitrans.dto.response.CustomerDTO;
import com.eztech.fitrans.dto.response.ErrorCodeEnum;
import com.eztech.fitrans.exception.BusinessException;
import com.eztech.fitrans.exception.CustomerImportException;
import com.eztech.fitrans.exception.InputInvalidException;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.locale.Translator;
import com.eztech.fitrans.model.Customer;
import com.eztech.fitrans.repo.CustomerRepository;
import com.eztech.fitrans.service.CustomerService;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;
import com.eztech.fitrans.util.ExcelFieldMapper;
import com.eztech.fitrans.util.ExcelFileReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.eztech.fitrans.constants.Constants.ACTIVE;
import static com.eztech.fitrans.constants.Constants.MsgKey.MS0001;
import static com.eztech.fitrans.util.DataUtils.replaceAll;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    public static final String VALIDATE_FILE_IMPORT = "validate file import";
    public static final String FILE_IMPORT = "File import";

    private static final BaseMapper<Customer, CustomerDTO> mapper = new BaseMapper<>(Customer.class,
            CustomerDTO.class);
    @Autowired
    private CustomerRepository repository;

    @Override
    @Transactional
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
            entity.setStatus(ACTIVE);
        }

        return mapper.toDtoBean(repository.save(entity));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        CustomerDTO dto = findById(id);
        if (dto == null) {
            throw new ResourceNotFoundException("Customer " + id + " not found");
        }
        validateDelete(id);
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteById(List<Long> ids) {
        if(DataUtils.notNullOrEmpty(ids)){
            for(Long id: ids) {
                validateDelete(id);
            }
            repository.delete(ids);
        }
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

    public List<CustomerDTO> findByCif(String cif) {
        List<Customer> customerEntity = repository.findByCifContains(cif.trim());
        List<CustomerDTO> customerDTO = mapper.toDtoBean(customerEntity);
        customerDTO.stream().forEach(customer -> customer.fillTransient());
        return customerDTO;
    }

    @Override
    @Transactional
    public List<CustomerDTO> importFile(MultipartFile file) throws Exception {
        try {
            List<CustomerDTO> listData = readFileImport(file);
            boolean isValid = validatePolicyTypeImport(listData);
            if (!isValid) {
                //Co lỗi validate file import
                throw new CustomerImportException(VALIDATE_FILE_IMPORT, listData);
            }
            for (CustomerDTO dto : listData) {
                save(dto);
            }
            return listData;
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage(), ex);
            throw new BusinessException(ErrorCodeEnum.ER0005, "File import");
        }
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
            throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.CUSTOMER_NAME),
                    100);
        }

        if (DataUtils.notNullOrEmpty(item.getAddress()) && item.getAddress().length() > 512) {
            throw new InputInvalidException(ErrorCodeEnum.ER0010,
                    Translator.toMessage(Constants.MessageParam.CUSTOMER_ADDRESS), 512);
        }

        if (DataUtils.notNullOrEmpty(item.getTel()) && item.getTel().length() > 25) {
            throw new InputInvalidException(ErrorCodeEnum.ER0010, Translator.toMessage(Constants.MessageParam.CUSTOMER_TEL),
                    25);
        }

        boolean checkExit = repository.checkExits(item.getId(), item.getCif());
        if (checkExit) {
            throw new InputInvalidException(ErrorCodeEnum.ER0009, Translator.toMessage(Constants.MessageParam.CIF));
        }
    }

    /**
     * Đọc file excel import
     *
     * @param file
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    private List<CustomerDTO> readFileImport(MultipartFile file) throws IOException, InvalidFormatException {
        Workbook workbook = null;
        if (file == null) {
            workbook = ExcelFileReader.readExcelFromResource("demo\\customer.xlsx");
        } else {
            workbook = ExcelFileReader.readExcel(file);
        }
        //Read file excel
        List<CustomerDTO> listModule = readDataExcel(workbook);
        return listModule;
    }

    private List<CustomerDTO> readDataExcel(Workbook workbook) {
        Sheet sheetModule = workbook.getSheetAt(0);
        Map<String, List<ExcelFieldDTO[]>> excelRowValuesMap = ExcelFileReader.getExcelRowValues(sheetModule, Constants.TemplateExcel.TEMPLATE_IMPORT_CUSTOMER, 1);
        List<CustomerDTO> customerDTOList = ExcelFieldMapper.getPojos(excelRowValuesMap.get(ExcelSection.CUSTOMER.getValue()), CustomerDTO.class);
        customerDTOList.forEach(item -> {
            item.setTel(replaceAll(item.getTel(), "\\.0", ""));
            item.setStatus(ACTIVE);
            log.debug(DataUtils.objectToJson(item));
        });
        return customerDTOList;
    }

    private boolean validatePolicyTypeImport(List<CustomerDTO> customerDTOList) {
        if (DataUtils.isNullOrEmpty(customerDTOList)) {
            return true;
        }
        boolean check = true;
        for (CustomerDTO dto : customerDTOList) {
            try {
                validate(dto);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                dto.setErrorMsg(ex.getMessage());
                check = false;
            }
        }
        return check;
    }

    private void validateDelete(Long id) {
        Long count = repository.countProfileByCustomer(id);
        if (0L < count) {
            throw new BusinessException(ErrorCodeEnum.ER9999, "Khách hàng đang được sử dụng bởi đầu mối!");
        }

        count = repository.countProfileByCustomer(id);
        if (0L < count) {
            throw new BusinessException(ErrorCodeEnum.ER9999, "Khách hàng đang được sử dụng bởi hồ sơ!");
        }
    }

}
