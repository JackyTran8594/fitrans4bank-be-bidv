package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.model.StaffContact;
import com.eztech.fitrans.repo.StaffContactRepositoryCustom;
import com.eztech.fitrans.util.DataUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffContactRepositoryCustomImpl extends BaseCustomRepository<StaffContact> implements StaffContactRepositoryCustom {
    @Override
    public List search(Map searchDTO, Class aClass) {
        Map<String, Object> parameters = new HashMap<>();
        String sql = buildQuery(searchDTO, parameters, false);
        return getResultList(sql, StaffContact.class, parameters);
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
        sb.append("UPDATE staff_contact SET status =:status, last_updated_by = :updateBy,last_updated_date=:updateDate WHERE id = :id ");
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
        sb.append("SELECT COUNT(*) FROM staff_contact WHERE 1=1 ");
        if (DataUtils.notNull(id)) {
            sb.append(" AND id != :id ");
            parameters.put("id", id);
        }
        if (DataUtils.notNullOrEmpty(code)) {
            sb.append(" AND UPPER(cif) = :code ");
            parameters.put("code", code.trim().toUpperCase());
        }
        sb.append(" AND status = 'ACTIVE'");
        return getCountResult(sb.toString(), parameters) > 0L;
    }

    @Override
    public String buildQuery(Map<String, Object> paramSearch, Map<String, Object> parameters, boolean count) {
        StringBuilder sb = new StringBuilder();
        if (count) {
            sb.append("SELECT COUNT(id) \n")
                    .append("FROM staff_contact os\n")
                    .append("WHERE 1=1 ");
        } else {
            sb.append("SELECT os.* \n")
                    .append("FROM staff_contact os\n")
                    .append("WHERE 1=1 ");
        }

        if (paramSearch.containsKey("id")) {
            sb.append(" AND os.id = :id ");
            parameters.put("id", DataUtils.parseToLong(paramSearch.get("id")));
        }

        if (paramNotNullOrEmpty(paramSearch, "txtSearch")) {
            sb.append(" AND (UPPER(os.cif) LIKE :txtSearch OR UPPER(os.staff_id_cm) LIKE :txtSearch) ");
            parameters.put("txtSearch", formatLike((String) paramSearch.get("txtSearch")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "cif")) {
            sb.append(" AND UPPER(os.cif) LIKE :cif ");
            parameters.put("cif", formatLike((String) paramSearch.get("cif")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "staffIdCM")) {
            sb.append(" AND os.staff_id_cm = :staffIdCM ");
            parameters.put("staffIdCM", paramSearch.get("staffIdCM"));
        }

        if (paramNotNullOrEmpty(paramSearch, "staffIdCM")) {
            sb.append(" AND os.staff_id_cm = :staffIdCM ");
            parameters.put("staffIdCM", paramSearch.get("staffIdCM"));
        }


        if (paramNotNullOrEmpty(paramSearch, "staffIdCT")) {
            sb.append(" AND os.staff_id_ct = :staffIdCT ");
            parameters.put("staffIdCT", paramSearch.get("staffIdCT"));
        }

        if (paramNotNullOrEmpty(paramSearch, "staffIdCustomer")) {
            sb.append(" AND os.staff_id_customer = :staffIdCustomer ");
            parameters.put("staffIdCustomer", paramSearch.get("staffIdCustomer"));
        }

        if (paramNotNullOrEmpty(paramSearch, "customerId")) {
            sb.append(" AND os.customer_id = :customerId ");
            parameters.put("customerId", paramSearch.get("customerId"));
        }

        if (paramNotNullOrEmpty(paramSearch, "status")) {
            sb.append(" AND os.status = :status ");
            parameters.put("status", paramSearch.get("status"));
        }

        if (!count) {
            if (paramSearch.containsKey("sort")) {
                sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY os.code DESC  "));
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
