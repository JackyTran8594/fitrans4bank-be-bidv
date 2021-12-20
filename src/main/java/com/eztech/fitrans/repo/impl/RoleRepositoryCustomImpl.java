package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.model.Role;
import com.eztech.fitrans.repo.RoleRepositoryCustom;
import com.eztech.fitrans.util.DataUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleRepositoryCustomImpl extends BaseCustomRepository<Role> implements
    RoleRepositoryCustom {

  @Override
  public List search(Map searchDTO, Class aClass) {
    Map<String, Object> parameters = new HashMap<>();
    String sql = buildQuery(searchDTO, parameters, false);
    return getResultList(sql, Role.class, parameters);
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
        "UPDATE role SET status =:status, last_updated_by = :updateBy,last_updated_date=:updateDate WHERE id = :id ");
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
    sb.append("SELECT COUNT(*) FROM role WHERE 1=1 ");
    if (DataUtils.notNull(id)) {
      sb.append(" AND id != :id ");
      parameters.put("id", id);
    }
    if (DataUtils.notNullOrEmpty(code)) {
      sb.append(" AND UPPER(code) = :code ");
      parameters.put("code", code.trim().toUpperCase());
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
          .append("FROM role os\n")
          .append("WHERE 1=1 ");
    } else {
      sb.append("SELECT os.* \n")
          .append("FROM role os\n")
          .append("WHERE 1=1 ");
    }

    if (paramSearch.containsKey("id")) {
      sb.append(" AND os.id = :id ");
      parameters.put("id", DataUtils.parseToLong(paramSearch.get("id")));
    }

    if (paramNotNullOrEmpty(paramSearch, "code")) {
      sb.append(" AND UPPER(os.code) LIKE :code ");
      parameters.put("code", formatLike((String) paramSearch.get("code")).toUpperCase());
    }

    if (paramNotNullOrEmpty(paramSearch, "name")) {
      sb.append(" AND UPPER(os.name) LIKE :name ");
      parameters.put("name", formatLike((String) paramSearch.get("name")).toUpperCase());
    }

    if (paramNotNullOrEmpty(paramSearch, "status")) {
      sb.append(" AND os.status = :status ");
      parameters.put("status", paramSearch.get("status"));
    }

    if (paramNotNullOrEmpty(paramSearch, "description")) {
      sb.append(" AND UPPER(os.description) LIKE :description ");
      parameters
          .put("description", formatLike((String) paramSearch.get("description")).toUpperCase());
    }

    if (paramSearch.containsKey("sort")) {
      sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY os.code ASC  "));
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
