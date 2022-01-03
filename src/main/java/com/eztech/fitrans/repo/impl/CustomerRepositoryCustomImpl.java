package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.model.Customer;
import com.eztech.fitrans.repo.CustomerRepositoryCustom;
import com.eztech.fitrans.util.DataUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerRepositoryCustomImpl extends BaseCustomRepository<Customer> implements
        CustomerRepositoryCustom {

    @Override
    public List search(Map searchDTO, Class aClass) {
        Map<String, Object> parameters = new HashMap<>();
        String sql = buildQuery(searchDTO, parameters, false);
        return getResultList(sql, Customer.class, parameters);
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
                "UPDATE customer SET status =:status, last_updated_by = :updateBy,last_updated_date=:updateDate WHERE id = :id ");
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
        sb.append("SELECT COUNT(*) FROM customer WHERE 1=1 ");
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
                    .append("FROM customer os\n")
                    .append("WHERE 1=1 ");
        } else {
            sb.append(
                    "SELECT os.* \n")
                    .append(
                            "FROM customer os \n")
                    .append("WHERE 1=1 ");
        }

        if (paramSearch.containsKey("id")) {
            sb.append(" AND os.id = :id ");
            parameters.put("id", DataUtils.parseToLong(paramSearch.get("id")));
        }

        if (paramNotNullOrEmpty(paramSearch, "txtSearch")) {
            sb.append(" AND (UPPER(cif) LIKE :txtSearch OR UPPER(name) LIKE :txtSearch) ");
            parameters.put("txtSearch", formatLike((String) paramSearch.get("txtSearch")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "cif")) {
            sb.append(" AND UPPER(cif) LIKE :cif ");
            parameters.put("cif", formatLike((String) paramSearch.get("cif")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "name")) {
            sb.append(" AND UPPER(name) LIKE :name ");
            parameters.put("name", formatLike((String) paramSearch.get("name")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "address")) {
            sb.append(" AND UPPER(address) LIKE :address ");
            parameters.put("address", formatLike((String) paramSearch.get("address")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "type")) {
            sb.append(" AND type = :type ");
            parameters.put("type", paramSearch.get("type"));
        }

        if (paramNotNullOrEmpty(paramSearch, "status")) {
            sb.append(" AND os.status = :status ");
            parameters.put("status", paramSearch.get("status"));
        }

        if (!count) {
            if (paramSearch.containsKey("sort")) {
                sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY os.id DESC  "));
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
