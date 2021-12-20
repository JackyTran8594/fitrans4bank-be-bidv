package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.constants.Constants;
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
          "SELECT os.id,os.customer_id,os.staff_id,os.type,os.priority,os.process_date,os.last_updated_by,os.last_updated_date,os.status,c.cif,c.name as customer_name, s.name as staff_name \n")
          .append(
              "FROM profile os left join customer c on os.customer_id = c.id AND c.`status` = 'ACTIVE' \n")
          .append(" left join staff s on os.staff_id = s.id AND s.`status` = 'ACTIVE' ")
          .append("WHERE 1=1 ");
    }

    if (paramSearch.containsKey("id")) {
      sb.append(" AND os.id = :id ");
      parameters.put("id", DataUtils.parseToLong(paramSearch.get("id")));
    }

    if (paramNotNullOrEmpty(paramSearch, "cif")) {
      sb.append(" AND c.cif LIKE :cif ");
      parameters.put("cif", formatLike((String) paramSearch.get("cif")).toLowerCase());
    }

    if (paramSearch.containsKey("customerId")) {
      sb.append(" AND os.customer_id = :customerId ");
      parameters.put("customerId", DataUtils.parseToLong(paramSearch.get("customerId")));
    }

    if (paramSearch.containsKey("staffId")) {
      sb.append(" AND os.staff_id = :staffId ");
      parameters.put("staffId", DataUtils.parseToLong(paramSearch.get("staffId")));
    }

    if (paramNotNullOrEmpty(paramSearch, "status")) {
      sb.append(" AND os.status = :status ");
      parameters.put("status", paramSearch.get("status"));
    }
    if (paramSearch.containsKey("sort")) {
      sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY os.id DESC  "));
    }

    if (!count && paramNotNullOrEmpty(paramSearch, "pageSize") && !"0"
        .equalsIgnoreCase(String.valueOf(paramSearch.get("pageSize")))) {
      sb.append(" LIMIT :offset,:limit");
      parameters.put("offset", offetPaging(DataUtils.parseToInt(paramSearch.get("pageNumber")),
          DataUtils.parseToInt(paramSearch.get("pageSize"))));
      parameters.put("limit", DataUtils.parseToInt(paramSearch.get("pageSize")));
    }
    return sb.toString();
  }
}
