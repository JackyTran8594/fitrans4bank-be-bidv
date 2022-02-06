package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.model.Profile;
import com.eztech.fitrans.repo.ProfileRepositoryCustom;
import com.eztech.fitrans.util.DataUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileRepositoryCustomImpl extends BaseCustomRepository<Profile> implements
    ProfileRepositoryCustom {

  @Override
  public List search(Map searchDTO, Class aClass) {
    Map<String, Object> parameters = new HashMap<>();
    String sql = buildQuery(searchDTO, parameters, false);
    return getResultList(sql, Constants.ResultSetMapping.PROFILE_DTO, parameters);
  }

  @Override
  public Long count(Map searchDTO) {
    Map<String, Object> parameters = new HashMap<>();
    String sql = buildQuery(searchDTO, parameters, true);
    return getCountResult(sql, parameters);
  }

  @Override
  public Integer updateStatus(Long id, String status, String lastUpdatedBy,
      LocalDateTime lastUpdateDate) {
    StringBuilder sb = new StringBuilder();
    Map<String, Object> parameters = new HashMap<>();
    sb.append(
        "UPDATE profile SET status =:status, last_updated_by = :updateBy,last_updated_date=:updateDate WHERE id = :id ");
    parameters.put("id", id);
    parameters.put("status", status);
    parameters.put("updateBy", lastUpdatedBy);
    parameters.put("updateDate", lastUpdateDate);
    return executeUpdate(sb.toString(), parameters);
  }

  @Override
  public Boolean checkExits(Long id, String code) {
    StringBuilder sb = new StringBuilder();
    Map<String, Object> parameters = new HashMap<>();
    sb.append("SELECT COUNT(*) FROM profile WHERE 1=1 ");
    if (DataUtils.notNull(id)) {
      sb.append(" AND id != :id ");
      parameters.put("id", id);
    }
    if (DataUtils.notNullOrEmpty(code)) {
      sb.append(" AND cif = :cif ");
      parameters.put("cif", code.trim().toLowerCase());
    }
    sb.append(" AND status > 0");
    return getCountResult(sb.toString(), parameters) > 0L;
  }

  @Override
  public String buildQuery(Map<String, Object> paramSearch, Map<String, Object> parameters,
      boolean count) {
    StringBuilder sb = new StringBuilder();
    if (count) {
      sb.append("SELECT COUNT(id) \n")
          .append("FROM profile os\n")
          .append("WHERE 1=1 ");
    } else {
      sb.append(
          "SELECT p.id,p.customer_id,p.staff_id,p.type,p.priority,p.process_date, p.time_received_ct, p.time_received_cm, p.end_time, p.staff_id_cm, p.staff_id_ct, p.number_of_bill, p.number_of_po, p.value, p.return_reason, p.created_by,p.created_date,p.last_updated_by,p.last_updated_date,p.status,p.state, p.rate, p.notify_by_email ,c.cif,c.name as customer_name, s.name as staff_name \n")
          .append(
              "FROM profile p left join customer c on p.customer_id = c.id AND c.status = 'ACTIVE' \n")
          .append(" left join staff s on p.staff_id = s.id AND s.status = 'ACTIVE' ")
          .append("WHERE 1=1 ");
    }

    if (paramSearch.containsKey("id")) {
      sb.append(" AND p.id = :id ");
      parameters.put("id", DataUtils.parseToLong(paramSearch.get("id")));
    }

    if (paramNotNullOrEmpty(paramSearch, "cif")) {
      sb.append(" AND c.cif LIKE :cif ");
      parameters.put("cif", formatLike((String) paramSearch.get("cif")).toLowerCase());
    }

    if (paramSearch.containsKey("customerId")) {
      sb.append(" AND p.customer_id = :customerId ");
      parameters.put("customerId", DataUtils.parseToLong(paramSearch.get("customerId")));
    }

    if (paramSearch.containsKey("staffId")) {
      sb.append(" AND p.staff_id = :staffId ");
      parameters.put("staffId", DataUtils.parseToLong(paramSearch.get("staffId")));
    }

    if (paramNotNullOrEmpty(paramSearch, "status")) {
      sb.append(" AND p.status = :status ");
      parameters.put("status", paramSearch.get("status"));
    }

    if(paramNotNullOrEmpty(paramSearch, "rate")) {
      sb.append(" AND p.rate = :rate ");
      parameters.put("rate", paramSearch.get("rate"));
    }

    if (!count) {
      if (paramSearch.containsKey("sort")) {
        sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY p.id DESC  "));
      }else{
        sb.append(" ORDER BY p.id desc ");
      }
    }

    if (!count && paramNotNullOrEmpty(paramSearch, "pageSize") && !"0"
        .equalsIgnoreCase(String.valueOf(paramSearch.get("pageSize")))) {
      sb.append(" OFFSET :offset ROWS ");
      sb.append(" FETCH NEXT :limit ROWS ONLY ");
      parameters.put("offset", offetPaging(DataUtils.parseToInt(paramSearch.get("pageNumber")), DataUtils.parseToInt(paramSearch.get("pageSize"))));
      parameters.put("limit", DataUtils.parseToInt(paramSearch.get("pageSize")));
    }
    return sb.toString();
  }

  @Override
  public ProfileDTO detailById(Long id) {
    Map<String, Object> parameters = new HashMap<>();
    String sql = "SELECT p.id,p.customer_id,p.staff_id,p.type,p.priority,p.process_date,p.time_received_ct, p.time_received_cm, p.end_time, p.staff_id_cm, p.staff_id_ct, p.number_of_bill, p.number_of_po, p.value, p.return_reason, p.created_by,p.created_date,p.last_updated_by,p.last_updated_date,p.status,p.state,p.rate, p.notify_by_email,c.cif,c.name as customer_name, s.name as staff_name " +
            "FROM profile p left join customer c on p.customer_id = c.id AND c.status = 'ACTIVE' " +
            " left join staff s on p.staff_id = s.id AND s.status = 'ACTIVE' " +
            " where p.id = :id ";
    parameters.put("id",id);
    ProfileDTO profileDTO = getSingleResult(sql,Constants.ResultSetMapping.PROFILE_DTO,parameters);
    return profileDTO;
  }
}
