package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.model.ActionLog;
import com.eztech.fitrans.model.Role;
import com.eztech.fitrans.repo.ActionLogRepositoryCustom;
import com.eztech.fitrans.util.DataUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionLogRepositoryCustomImpl extends BaseCustomRepository<ActionLog> implements
        ActionLogRepositoryCustom {

    @Override
    public List search(Map searchDTO, Class aClass) {
        Map<String, Object> parameters = new HashMap<>();
        String sql = buildQuery(searchDTO, parameters, false);
        return getResultList(sql, ActionLog.class, parameters);
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
                "UPDATE action_log SET status =:status, last_updated_by = :updateBy,last_updated_date=:updateDate WHERE id = :id ");
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
                    .append("FROM action_log os\n")
                    .append("WHERE 1=1 ");
        } else {
            sb.append("SELECT os.* \n")
                    .append("FROM action_log os\n")
                    .append("WHERE 1=1 ");
        }

        if (paramSearch.containsKey("id")) {
            sb.append(" AND os.id = :id ");
            parameters.put("id", DataUtils.parseToLong(paramSearch.get("id")));
        }

        if (paramNotNullOrEmpty(paramSearch, "username")) {
            sb.append(" AND UPPER(os.username) LIKE :username ");
            parameters.put("username", formatLike((String) paramSearch.get("username")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "processTimeFrom")) {
            sb.append(" AND os.process_time >= :processTimeFrom ");
            parameters.put("processTimeFrom", paramSearch.get("processTimeFrom"));
        }

        if (paramNotNullOrEmpty(paramSearch, "processTimeTo")) {
            sb.append(" AND os.process_time <= :processTimeTo ");
            parameters.put("processTimeTo", paramSearch.get("processTimeTo") + " 23:59:59.999");
        }

        if (paramNotNullOrEmpty(paramSearch, "status")) {
            sb.append(" AND os.status = :status ");
            parameters.put("status", paramSearch.get("status"));
        }

        if (paramNotNullOrEmpty(paramSearch, "httpStatus")) {
            sb.append(" AND os.http_status = :httpStatus ");
            parameters.put("httpStatus", paramSearch.get("httpStatus"));
        }

        if (paramNotNullOrEmpty(paramSearch, "responseCode")) {
            sb.append(" AND os.response_code = :responseCode ");
            parameters.put("responseCode", paramSearch.get("responseCode"));
        }

        if (!count) {
            if (paramSearch.containsKey("sort")) {
                sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY os.process_time DESC  "));
            } else {
                sb.append(" ORDER BY os.id desc ");
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
}
