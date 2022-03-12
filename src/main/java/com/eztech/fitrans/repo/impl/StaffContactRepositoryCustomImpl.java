package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.model.StaffContact;
import com.eztech.fitrans.repo.StaffContactRepositoryCustom;
import com.eztech.fitrans.util.DataUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffContactRepositoryCustomImpl extends BaseCustomRepository<StaffContact>
        implements StaffContactRepositoryCustom {
    @Override
    public List search(Map searchDTO, Class aClass) {
        Map<String, Object> parameters = new HashMap<>();
        String sql = buildQuery(searchDTO, parameters, false);
        return getResultList(sql, Constants.ResultSetMapping.STAFF_CONTACT_DTO, parameters);
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
        sb.append(
                "UPDATE staff_contact SET status =:status, last_updated_by = :updateBy,last_updated_date=:updateDate WHERE id = :id ");
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
        String selectSql = "SELECT staffCM.cif, staffCM.id, staffCM.customer_id,  staffCM.status, staffCM.created_by, staffCM.created_date, staffCM.last_updated_by, staffCM.last_updated_date, staffCM.note, staffCM.staff_id_cm, staffCT.staff_id_ct, staffCustomer.staff_id_customer, staffCM.full_name as staffNameCM, staffCustomer.full_name as staffNameCustomer, staffCT.full_name as staffNameCT \n";
        String staffCMstr_select = "(SELECT sc.cif, sc.id, sc.customer_id, sc.staff_id_cm, sc.status, sc.created_by, sc.created_date, sc.last_updated_by, sc.last_updated_date, sc.note, us.full_name \n";
        String staffCMstr_from = "FROM [test].[dbo].[staff_contact] sc left JOIN dbo.user_entity us on sc.staff_id_cm = us.id) as staffCM \n";
        String staffCTstr_select = "( SELECT sc.cif, sc.id, sc.staff_id_ct, us.full_name \n";
        String staffCTstr_from = " FROM [test].[dbo].[staff_contact] sc left JOIN dbo.user_entity us on sc.staff_id_ct = us.id) as staffCT on staffCM.id = staffCT.id \n";
        String staffCustomer_select = "(SELECT sc.cif, sc.id, sc.staff_id_customer, us.full_name \n";
        String staffCustomer_from = " FROM [test].[dbo].[staff_contact] sc left join dbo.user_entity us on sc.staff_id_customer = us.id) as staffCustomer on staffCustomer.id = staffCM.id \n";
        if (count) {
            sb.append("SELECT COUNT(staffCM.id) \n")
                    .append("FROM \n")
                    .append(staffCMstr_select)
                    .append(staffCMstr_from)
                    .append("LEFT JOIN \n")
                    .append(staffCTstr_select)
                    .append(staffCTstr_from)
                    .append("LEFT JOIN \n")
                    .append(staffCustomer_select)
                    .append(staffCustomer_from)
                    .append("WHERE 1=1");
        } else {

            sb.append(selectSql)
                    .append("FROM \n")
                    .append(staffCMstr_select)
                    .append(staffCMstr_from)
                    .append("LEFT JOIN \n")
                    .append(staffCTstr_select)
                    .append(staffCTstr_from)
                    .append("LEFT JOIN \n")
                    .append(staffCustomer_select)
                    .append(staffCustomer_from)
                    .append("WHERE 1=1");

        }

        if (paramSearch.containsKey("cif")) {
            sb.append(" AND staffCM.cif = :cif ");
            parameters.put("cif", paramSearch.get("cif"));
        }

        if (paramNotNullOrEmpty(paramSearch, "txtSearch")) {
            sb.append(
                    " AND (UPPER(staffCM.cif) LIKE :txtSearch OR UPPER(staffCM.staff_id_cm) LIKE :txtSearch " +
                            "OR UPPER(staffCM.customer_id) LIKE :txtSearch " +
                            "OR UPPER(staffCM.cif) LIKE :txtSearch OR UPPER(staffCM.full_name) LIKE :txtSearch " +
                            "OR UPPER(staffCustomer.full_name) LIKE :txtSearch " +
                            "OR UPPER(staffCT.full_name) LIKE :txtSearch) ");
            parameters.put("txtSearch", formatLike((String) paramSearch.get("txtSearch")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "cif")) {
            sb.append(" AND UPPER(staffCM.cif) LIKE :cif ");
            parameters.put("cif", formatLike((String) paramSearch.get("cif")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "staffIdCM")) {
            sb.append(" AND staffCM.staff_id_cm = :staffIdCM ");
            parameters.put("staffIdCM", paramSearch.get("staffIdCM"));
        }

        if (paramNotNullOrEmpty(paramSearch, "staffIdCM")) {
            sb.append(" AND os.staff_id_cm = :staffIdCM ");
            parameters.put("staffIdCM", paramSearch.get("staffIdCM"));
        }

        if (paramNotNullOrEmpty(paramSearch, "staffIdCT")) {
            sb.append(" AND staffCT.staff_id_ct = :staffIdCT ");
            parameters.put("staffIdCT", paramSearch.get("staffIdCT"));
        }

        if (paramNotNullOrEmpty(paramSearch, "staffIdCustomer")) {
            sb.append(" AND staffCustomer.staff_id_customer = :staffIdCustomer ");
            parameters.put("staffIdCustomer", paramSearch.get("staffIdCustomer"));
        }

        if (paramNotNullOrEmpty(paramSearch, "customerId")) {
            sb.append(" AND staffCM.customer_id = :customerId ");
            parameters.put("customerId", paramSearch.get("customerId"));
        }

        if (paramNotNullOrEmpty(paramSearch, "status")) {
            sb.append(" AND staffCM.status = :status ");
            parameters.put("status", paramSearch.get("status"));
        }

        if (!count) {
            if (paramSearch.containsKey("sort")) {
                sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY staffCM.id DESC  "));
            } else {
                sb.append(" ORDER BY staffCM.id desc ");
            }
        }

        if (!count && paramNotNullOrEmpty(paramSearch, "pageSize")
                && !"0".equalsIgnoreCase(String.valueOf(paramSearch.get("pageSize")))) {
            sb.append(" OFFSET :offset ROWS ");
            sb.append(" FETCH NEXT :limit ROWS ONLY ");
            parameters.put("offset", offetPaging(DataUtils.parseToInt(paramSearch.get("pageNumber")),
                    DataUtils.parseToInt(paramSearch.get("pageSize"))));
            parameters.put("limit", DataUtils.parseToInt(paramSearch.get("pageSize")));
        }
        return sb.toString();
    }
}
