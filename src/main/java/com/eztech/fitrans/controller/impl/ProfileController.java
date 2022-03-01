package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.controller.ProfileApi;
import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.DepartmentDTO;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import com.eztech.fitrans.dto.response.ProfileListDTO;
import com.eztech.fitrans.dto.response.TransactionTypeDTO;
import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.ProfileHistory;
import com.eztech.fitrans.service.DepartmentService;
import com.eztech.fitrans.service.ProfileHistoryService;
import com.eztech.fitrans.service.ProfileListService;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.service.TransactionTypeService;
import com.eztech.fitrans.service.UserService;
import com.eztech.fitrans.util.ReadAndWriteDoc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/profiles")
public class ProfileController extends BaseController implements ProfileApi {

  @Autowired
  private ProfileService service;

  @Autowired
  private UserService userService;

  @Autowired
  private ProfileHistoryService historyService;

  @Autowired
  private DepartmentService departmentService;

  @Autowired
  private ProfileListService profileListService;

  @Autowired
  private TransactionTypeService transactionTypeService;

  private static Logger logger = LoggerFactory.getLogger(ProfileController.class);

  @Autowired
  private ReadAndWriteDoc readandwrite;

  @Override
  @GetMapping("")
  public Page<ProfileDTO> getList(
      @RequestParam Map<String, Object> mapParam,
      @RequestParam int pageNumber,
      @RequestParam int pageSize) {
    if (pageNumber > 0) {
      pageNumber = pageNumber - 1;
    }
    mapParam.put("pageNumber", pageNumber);
    mapParam.put("pageSize", pageSize);
    Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
    List<ProfileDTO> listData = service.search(mapParam);
    Long total = service.count(mapParam);
    return new PageImpl<>(listData, pageable, total);
  }

  @Override
  @GetMapping("/{id}")
  public ProfileDTO getById(@PathVariable(value = "id") Long id) {
    ProfileDTO dto = service.detailById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("Profile " + id + " not found");
    }
    return dto;
  }

  @Override
  @PostMapping("")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ProfileDTO create(@RequestBody ProfileDTO item) {
    return service.save(item);
  }

  @Override
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ProfileDTO update(@PathVariable(value = "id") Long id, @RequestBody ProfileDTO item) {
    item.setId(id);
    return service.save(item);
  }

  @Override
  @DeleteMapping("/{id}")
  public Boolean delete(@PathVariable(value = "id") Long id) {
    service.deleteById(id);
    return true;
  }

  @PostMapping("/exportDoc")
  public ResponseEntity<InputStreamResource> exportDoc(@RequestParam Map<String, Object> mapParam,
      @RequestBody ProfileDTO item) throws FileNotFoundException {

    String username = mapParam.get("username").toString();
    MediaType mediaType = MediaType.APPLICATION_JSON;
    Map<String, ProfileListDTO> mapParams = new HashMap<>();
    String[] categoryId = item.categoryProfile.split(",");
    for (String str : categoryId) {
      ProfileListDTO profileListDTO = profileListService.findById(Long.parseLong(str));
      if (profileListDTO != null) {
        mapParams.put(profileListDTO.id.toString(), profileListDTO);
      }
    }

    File file = readandwrite.ExportDocFile(item, username, mapParams);
    InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
        .contentType(mediaType)
        .contentLength(file.length())
        .body(inputStreamResource);

  }

  @GetMapping("/historyProfile/{id}")
  public List<ProfileHistoryDTO> getHistory(@PathVariable(value = "id") Long id) {
    List<ProfileHistoryDTO> listData = historyService.findAll();
    return listData;
  }

  @PostMapping("/reivewProfile")
  public ProfileDTO reviewProfile(@RequestBody ProfileDTO item) {
    item.setId(item.id);
    return service.save(item);
  }

  @PostMapping("/returnProfile")
  public ProfileDTO returnProfile(@RequestBody ProfileDTO item) {
    return service.save(item);
  }

  @PostMapping("/transferProfile")
  public ProfileDTO transferProfile(@RequestBody ProfileDTO item) {
    return service.save(item);
  }

  @PostMapping("/assignProfile")
  public ProfileDTO assignProfile(@RequestBody ProfileDTO item) {
    return service.save(item);
  }

  @PostMapping("/confirmProfile")
  public Boolean confirmProfile(@RequestBody ConfirmRequest item) {
    ProfileDTO profile = service.findById(item.getProfileId());
    // List<ProfileDTO> profiles = service.
    UserDTO user = userService.findByUsername(item.getUsername());
    DepartmentDTO department = departmentService.findByCode(item.getCode());
    ProfileHistoryDTO profileHistory = new ProfileHistoryDTO();
    TransactionTypeDTO transactionType = transactionTypeService.findById(Long.parseLong(profile.getType().toString()));

    profileHistory.setProfileId(item.getProfileId());
    profileHistory.setDepartmentId(department.getId());
    profileHistory.setTimeReceived(LocalDateTime.now());
    profileHistory.setStaffId(user.getId());

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // check account admin or not
    if (item.username.toLowerCase().contains("admin")) {
      profile.setState(ProfileStateEnum.DELEVERIED.getValue());

      // check department
      if (department.getName() == "QTTD") {
        if (profile.timeReceived_CM == null) {
          profile.setTimeReceived_CM(LocalDateTime.now());
        }
      } else if (department.getName() == "GDKH") {
        if (profile.timeReceived_CT == null) {
          profile.setTimeReceived_CT(LocalDateTime.now());
        }
      }

    } else {
      Map<String, Object> params = new HashMap<>();
      Long count = null;
      params.put("state", ProfileStateEnum.PROCESSING.getValue());
      // checking: transaction is finished
      if (item.getIsFinished()) {
        profile.setState(ProfileStateEnum.FINISHED.getValue());
        profileHistory.setState(ProfileStateEnum.FINISHED.getValue());
        profile.setEndTime(LocalDateTime.now());
        // update first row is processing
        params.put("state", ProfileStateEnum.WAITING.getValue());
        ProfileDTO firstItem = service.search(params).get(0);
        firstItem.setState(ProfileStateEnum.PROCESSING.getValue());
        service.save(firstItem);

      } else {
        if (department.getName() == "QTTD") {
          params.put("staffId_CM", user.getId());
          count = service.count(params);
          if (count == 1) {
            profile.setState(ProfileStateEnum.WAITING.getValue());
            profileHistory.setState(ProfileStateEnum.WAITING.getValue());
            LocalDateTime processTime = profileHistory.timeReceived
                .plusMinutes(transactionType.getStandardTimeCM() + transactionType.getStandardTimeChecker());
            Integer additionalTime = 0;

            // check transaction type
            switch (profile.getType()) {
              case 1:
                if (profile.getNumberOfPO() >= 2) {
                  additionalTime = additionalTime + 5 * profile.getNumberOfPO();
                }
                if (profile.getNumberOfBill() >= 2) {
                  additionalTime = additionalTime + 1 * profile.getNumberOfBill();
                }
                break;
              case 2:
                if (profile.getNumberOfPO() >= 2) {
                  additionalTime = additionalTime + 5 * profile.getNumberOfPO();
                }
                if (profile.getNumberOfBill() >= 2) {
                  additionalTime = additionalTime + 1 * profile.getNumberOfBill();
                }
                break;
              default:
                break;
            }
            // if (profile.getType() == 1) {

            // }

            processTime = processTime.plusMinutes(additionalTime);

            profile.setProcessDate(processTime);

          } else if (count == 0) {
            profile.setState(ProfileStateEnum.PROCESSING.getValue());
            profileHistory.setState(ProfileStateEnum.PROCESSING.getValue());
          }
        } else if (department.getName() == "GDKH") {
          params.put("staffId_CT", user.getId());
          count = service.count(params);
          if (count == 1) {
            profile.setState(ProfileStateEnum.WAITING.getValue());
            profileHistory.setState(ProfileStateEnum.WAITING.getValue());
          } else if (count == 0) {
            profile.setState(ProfileStateEnum.PROCESSING.getValue());
            profileHistory.setState(ProfileStateEnum.PROCESSING.getValue());
          }

        }

      }

    }
    service.save(profile);
    historyService.save(profileHistory);
    return true;
  }

  @PostMapping("/isFinished")
  public Boolean isFinished(@RequestBody ConfirmRequest item) {
    return true;

  }

  @GetMapping("/getInfo")
  public List<ProfileHistoryDTO> getInfoByIdAndState(@RequestParam Map<String, Object> params) {
    Long id = Long.valueOf(params.get("id").toString());
    Integer state = Integer.valueOf(params.get("state").toString());
    List<ProfileHistoryDTO> profilesHistory = historyService.findByIdAndState(id, state);
    return profilesHistory;
  }

}