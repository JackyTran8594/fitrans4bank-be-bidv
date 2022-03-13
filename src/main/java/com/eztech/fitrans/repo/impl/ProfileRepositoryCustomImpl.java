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
    String sql_select = "SELECT p.id,p.customer_id,p.staff_id,p.type,p.priority,p.process_date, p.time_received_ct, p.time_received_cm, p.end_time, "
        +
        "p.staff_id_cm, p.staff_id_ct, p.number_of_bill, p.number_of_po, p.value, p.return_reason, p.category_profile, p.created_by,p.created_date,p.last_updated_by,"
        +
        "p.last_updated_date,p.status,p.state, p.profile_process_state, p.review, p.notify_by_email ,p.cif,c.name as customer_name, u.full_name  as staff_name, p.review_note, "
        +
        "p.note, p.additional_time,  ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type \n";
    if (count) {
      sb.append("SELECT COUNT(p.id) \n")
          .append(
              "FROM profile p left join customer c on p.customer_id = c.id AND c.status = 'ACTIVE' \n")
          .append("left join user_entity u on p.staff_id = u.id AND u.status = 'ACTIVE' \n")
          .append("left join user_entity ucm on p.staff_id_cm = ucm.id AND ucm.status = 'ACTIVE' \n")
          .append("left join user_entity uct on p.staff_id_ct = uct.id AND uct.status = 'ACTIVE' \n")
          .append("left join transaction_type trans on trans.id = p.type \n")
          .append("WHERE 1=1 ");
    } else {

      sb.append(sql_select)
          .append(
              "FROM profile p left join customer c on p.customer_id = c.id AND c.status = 'ACTIVE' \n")
          .append("left join user_entity u on p.staff_id = u.id AND u.status = 'ACTIVE' \n")
          .append("left join user_entity ucm on p.staff_id_cm = ucm.id AND ucm.status = 'ACTIVE' \n")
          .append("left join user_entity uct on p.staff_id_ct = uct.id AND uct.status = 'ACTIVE' \n")
          .append("left join transaction_type trans on trans.id = p.type \n")
          .append("WHERE 1=1 ");

    }

    String sql_search_name = "AND (p.cif LIKE :txtSearch OR c.name LIKE :txtSearch OR u.full_name LIKE :txtSearch OR uct.full_name LIKE :txtSearch OR ucm.full_name LIKE :txtSearch "
        +
        "OR p.return_reason LIKE :txtSearch OR p.review_note LIKE :txtSearch OR p.note LIKE :txtSearch ";
    String sql_search_value = "OR CAST(p.value AS varchar(100)) LIKE :txtSearch  OR CAST(p.type AS varchar(100))  LIKE :txtSearch  OR CAST(p.number_of_bill AS varchar(100))  LIKE :txtSearch "
        + "OR CAST(p.number_of_po AS varchar(100)) LIKE :txtSearch OR CAST(p.state AS varchar(100)) LIKE :txtSearch) \n";

    if (paramSearch.containsKey("code")) {
      String deparmentCode = paramSearch.get("code").toString();

      if (!DataUtils.isNullOrEmpty(deparmentCode)) {
        // default null from client
        String username = paramSearch.get("username").toString();
        if (!DataUtils.isNullOrEmpty(username) && !username.toLowerCase().trim().equals("admin")) {
          String position = paramSearch.get("position").toString();
          switch (deparmentCode) {
            case "QTTD":
              String sql_qttd = "AND trans.type IN (1,2)";
              if (position.toUpperCase().contains("CHUYENVIEN")) {
                String sql_filter = " AND p.state NOT IN (0,1)";
                String sql_username = " AND u.username = :username ";
                sb.append(sql_qttd)
                    .append(sql_filter)
                    .append(sql_username);
                parameters.put("username", username);
              } else {
                String sql_filter = " AND p.state NOT IN (0) ";
                sb.append(sql_qttd)
                    .append(sql_filter);
              }
              break;
            case "GDKH":
              // transaction - type : GDKH có 2 luồng
              String sql_gdkh = " AND trans.type IN (1,3) ";
              if (position.toUpperCase().contains("CHUYENVIEN")) {
                String sql_filter = " AND p.state NOT IN (0,1) ";
                String sql_username = " AND u.username = :username ";
                sb.append(sql_gdkh)
                    .append(sql_filter)
                    .append(sql_username);
                parameters.put("username", username);
              } else {
                String sql_filter = " AND p.state NOT IN (0) ";
                sb.append(sql_gdkh)
                    .append(sql_filter);
              }
              break;
            default:
              break;
          }

        }
      }

    }
    if (paramSearch.containsKey("txtSearch")) {
      sb.append(sql_search_name).append(sql_search_value);
      parameters.put("txtSearch", formatLike((String) paramSearch.get("txtSearch").toString().toLowerCase()));
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
  public ProfileDTO detailById(Long id) {
    Map<String, Object> parameters = new HashMap<>();

    String sql = "SELECT p.id,p.customer_id,p.staff_id,p.type,p.priority,p.process_date,p.time_received_ct," +
        "p.time_received_cm, p.end_time, p.staff_id_cm, p.staff_id_ct," +
        "p.number_of_bill, p.number_of_po, p.value, p.return_reason, " +
        "p.category_profile," +
        "p.created_by,p.created_date,p.last_updated_by,p.last_updated_date,p.status,p.state," +
        "p.profile_process_state, p.review, p.notify_by_email,p.cif,c.name as customer_name," +
        "u.full_name as staff_name, p.review_note, p.note, p.additional_time , ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type  \n"
        +
        "FROM profile p left join customer c on p.customer_id = c.id AND c.status = 'ACTIVE' \n" +
        "left join user_entity u on p.staff_id = u.id AND u.status = 'ACTIVE' \n" +
        "left join user_entity ucm on p.staff_id_cm = ucm.id AND ucm.status = 'ACTIVE' \n" +
        "left join user_entity uct on p.staff_id_ct = uct.id AND uct.status = 'ACTIVE' \n" +
        "left join transaction_type trans on trans.id = p.type \n" +
        "where p.id = :id ";
    parameters.put("id", id);
    ProfileDTO profileDTO = getSingleResult(sql, Constants.ResultSetMapping.PROFILE_DTO, parameters);
    return profileDTO;
  }

  @Override
  public List<ProfileDTO> listDashboard() {
    Map<String, Object> parameters = new HashMap<>();
    String sql = "SELECT p.id,p.customer_id,p.staff_id,p.type,p.priority,p.process_date, p.time_received_ct, p.time_received_cm, p.end_time, "
        +
        "p.staff_id_cm, p.staff_id_ct, p.number_of_bill, p.number_of_po, p.value, p.return_reason, p.category_profile, p.created_by,"
        +
        "p.created_date,p.last_updated_by,p.last_updated_date,p.status,p.state, p.profile_process_state, p.review, p.notify_by_email ,"
        +
        "p.cif,c.name as customer_name, u.full_name as staff_name, p.review_note, p.note, p.additional_time,  ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type \n"
        +
        "FROM profile p left join customer c on p.customer_id = c.id AND c.status = 'ACTIVE' \n" +
        "left join user_entity u on p.staff_id = u.id AND u.status = 'ACTIVE' \n" +
        "left join user_entity ucm on p.staff_id_cm = ucm.id AND ucm.status = 'ACTIVE' \n" +
        "left join user_entity uct on p.staff_id_ct = uct.id AND uct.status = 'ACTIVE' \n" +
        "left join transaction_type trans on trans.id = p.type \n" +
        " where 1=1 ";
    return getResultList(sql, Constants.ResultSetMapping.PROFILE_DTO, parameters);
  }

  @Override
  public List<ProfileDTO> getProfileWithParams(Map<String, Object> params) {
    Map<String, Object> parameters = new HashMap<>();
    StringBuilder sb = new StringBuilder();
    String sql = "SELECT * FROM profile p WHERE 1=1" + "left join user_entity u on p.staff_id = u.id AND u.status = 'ACTIVE' \n";
    sb.append(sql);
    if (params.containsKey("state")) {
      sb.append(" AND p.state = :state");
      parameters.put("txtSearch", DataUtils.parseToInt(params.get("state")));
    }
    if (params.containsKey("username")) {
      sb.append(" AND u.username = :username");
      parameters.put("username", formatLike((String) params.get("username").toString().toLowerCase()));
    }

    if (params.containsKey("staffId_CT")) {
      sb.append(" AND p.staff_id_ct = :staffId_CT ");
      parameters.put("staffId_CT", DataUtils.parseToLong(params.get("staffId_CT")));
    }

    if (params.containsKey("staffId_CM")) {
      sb.append(" AND p.staff_id_cm = :staffId_CM ");
      parameters.put("staffId_CM", DataUtils.parseToLong(params.get("staffId_CM")));
    }

    return getResultList(sql, ProfileDTO.class, parameters);
  }

}
