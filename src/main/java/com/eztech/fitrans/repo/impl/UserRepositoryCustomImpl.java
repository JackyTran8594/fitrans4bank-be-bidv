package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.model.UserEntity;
import com.eztech.fitrans.repo.UserRepositoryCustom;
import com.eztech.fitrans.util.DataUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepositoryCustomImpl extends BaseCustomRepository<UserEntity> implements
        UserRepositoryCustom {
    @Override
    public List search(Map searchDTO, Class aClass) {
        Map<String, Object> parameters = new HashMap<>();
        String sql = buildQuery(searchDTO, parameters, false);
        return getResultList(sql, Constants.ResultSetMapping.USER_ENTITY_DTO, parameters);
    }

    @Override
    public Long count(Map searchDTO) {
        Map<String, Object> parameters = new HashMap<>();
        String sql = buildQuery(searchDTO, parameters, true);
        return getCountResult(sql, parameters);
    }

    @Override
    public Integer updateStatus(Long id, String status, String lastUpdatedBy, LocalDateTime lastUpdateDate) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        sb.append("UPDATE user_entity SET status =:status, last_updated_by = :updateBy,last_updated_date=:updateDate WHERE id = :id ");
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
        sb.append("SELECT COUNT(*) FROM user_entity WHERE 1=1 ");
        if (DataUtils.notNull(id)) {
            sb.append(" AND id != :id ");
            parameters.put("id", id);
        }
        if (DataUtils.notNullOrEmpty(code)) {
            sb.append(" AND username = :username ");
            parameters.put("username", code.trim().toLowerCase());
        }
        sb.append(" AND status > 0");
        return getCountResult(sb.toString(), parameters) > 0L;
    }

    @Override
    public String buildQuery(Map<String, Object> paramSearch, Map<String, Object> parameters, boolean count) {
        StringBuilder sb = new StringBuilder();
        if (count) {
            sb.append("SELECT COUNT(id) \n")
                    .append("FROM user_entity os\n")
                    .append("WHERE 1=1 ");
        } else {
            sb.append("SELECT os.id,os.username,os.email,os.full_name,os.position,os.department_id,os.status,os.last_updated_by,os.last_updated_date,d.code, d.name \n")
                    .append("FROM user_entity os left join department d on os.department_id = d.id AND d.status = 'ACTIVE' \n")
                    .append("WHERE 1=1 ");
        }

        if (paramSearch.containsKey("id")) {
            sb.append(" AND os.id = :id ");
            parameters.put("id", DataUtils.parseToLong(paramSearch.get("id")));
        }

        if (paramNotNullOrEmpty(paramSearch, "txtSearch")) {
            sb.append(" AND (UPPER(os.username) LIKE :txtSearch OR UPPER(os.email) LIKE :txtSearch OR UPPER(os.full_name) LIKE :txtSearch) ");
            parameters.put("txtSearch", formatLike((String) paramSearch.get("txtSearch")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "username")) {
            sb.append(" AND os.username LIKE :username ");
            parameters.put("username", formatLike((String) paramSearch.get("username")).toLowerCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "email")) {
            sb.append(" AND os.email LIKE :email ");
            parameters.put("email", formatLike((String) paramSearch.get("email")).toLowerCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "fullName")) {
            sb.append(" AND UPPER(os.full_name) LIKE :fullName ");
            parameters.put("fullName", formatLike((String) paramSearch.get("fullName")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "status")) {
            sb.append(" AND os.status = :status ");
            parameters.put("status", paramSearch.get("status"));
        }

        if (!count) {
            if (paramSearch.containsKey("sort")) {
                sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY os.username ASC  "));
            } else {
                sb.append(" ORDER BY os.id desc ");
            }
        }

        if (!count && paramNotNullOrEmpty(paramSearch, "pageSize") && !"0".equalsIgnoreCase(String.valueOf(paramSearch.get("pageSize")))) {
            sb.append(" OFFSET :offset ROWS ");
            sb.append(" FETCH NEXT :limit ROWS ONLY ");
            parameters.put("offset", offetPaging(DataUtils.parseToInt(paramSearch.get("pageNumber")), DataUtils.parseToInt(paramSearch.get("pageSize"))));
            parameters.put("limit", DataUtils.parseToInt(paramSearch.get("pageSize")));
        }
        return sb.toString();
    }
}
