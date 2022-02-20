package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import com.eztech.fitrans.model.ProfileHistory;
import com.eztech.fitrans.repo.ProfileHistoryRepositoryCustom;
import com.eztech.fitrans.util.DataUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileHistoryRepositoryCustomImpl extends BaseCustomRepository<ProfileHistory> implements
    ProfileHistoryRepositoryCustom {

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
    String sql_select = "SELECT p.id,p.staff_id,p.state, p.time_received, p.created_by,p.created_date,p.last_updated_by,p.last_updated_date,p.status, us.full_name as staff_name \n";
    String sql_from = "FROM profile_history p left join user_entity us on us.id = p.staff_id AND s.status = 'ACTIVE' \n";
    if (count) {
      sb.append("SELECT COUNT(p.id) \n")
          .append(
              sql_from)
          .append("WHERE 1=1 ");
    } else {
      sb.append(
          sql_select)
          .append(
              sql_from)
          .append("WHERE 1=1 ");
    }

    if (paramSearch.containsKey("id")) {
      sb.append(" AND p.id = :id ");
      parameters.put("id", DataUtils.parseToLong(paramSearch.get("id")));
    }

    if (paramSearch.containsKey("profileId")) {
      sb.append(" AND p.profile_id = :profileId ");
      parameters.put("profileId", DataUtils.parseToLong(paramSearch.get("profileId")));
    }

    if (paramSearch.containsKey("staffId")) {
      sb.append(" AND p.staff_id = :staffId ");
      parameters.put("staffId", DataUtils.parseToLong(paramSearch.get("staffId")));
    }

    if (paramNotNullOrEmpty(paramSearch, "status")) {
      sb.append(" AND p.status = :status ");
      parameters.put("status", paramSearch.get("status"));
    }

    if (!count) {
      if (paramSearch.containsKey("sort")) {
        sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY p.id DESC  "));
      } else {
        sb.append(" ORDER BY p.id desc ");
      }
    }

    if (!count && paramNotNullOrEmpty(paramSearch, "pageSize") && !"0"
        .equalsIgnoreCase(String.valueOf(paramSearch.get("pageSize")))) {
      sb.append(" OFFSET :offset ROWS ");
      sb.append(" FETCH NEXT :limit ROWS ONLY ");
      parameters.put("offset", offetPaging(DataUtils.parseToInt(paramSearch.get("pageNumber")),
          DataUtils.parseToInt(paramSearch.get("pageSize"))));
      parameters.put("limit", DataUtils.parseToInt(paramSearch.get("pageSize")));
    }
    return sb.toString();
  }

  @Override
  public ProfileHistoryDTO deteilByIdAndState(Long id, Integer state) {
    // TODO Auto-generated method stub
    Map<String, Object> parameters = new HashMap<>();
    String sql = "SELECT p.id,p.staff_id,p.state, p.time_received, p.created_by,p.created_date,p.last_updated_by,p.last_updated_date,p.status, us.full_name as staff_name \n"
        +
        "FROM profile_history p left join user_entity us on us.id = p.staff_id AND s.status = 'ACTIVE' \n";
    parameters.put("id", id);
    parameters.put("state", state);
    ProfileHistoryDTO profileHistory = getSingleResult(sql, Constants.ResultSetMapping.PROFILE_HISTORY_DTO, parameters);
    return profileHistory;
  }

}
