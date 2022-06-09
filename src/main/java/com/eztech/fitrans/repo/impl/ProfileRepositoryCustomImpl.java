package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.constants.PositionTypeEnum;
import com.eztech.fitrans.constants.UserTypeEnum;
import com.eztech.fitrans.constants.Constants.Department;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.model.Profile;
import com.eztech.fitrans.repo.ProfileRepositoryCustom;
import com.eztech.fitrans.util.DataUtils;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileRepositoryCustomImpl extends BaseCustomRepository<Profile> implements
        ProfileRepositoryCustom {

    @Value("${app.dashboard.checkTime:0}")
    private Integer checkTime;

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

        String sql_select = "SELECT p.*, " +
                "u.full_name as staff_name_last, c.name as customer_name," +
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type, trans.transaction_detail as transaction_detail, trans.additional_time_max as additional_time_max, c.type as customer_type  \n";

        if (count) {
            sb.append("SELECT COUNT(p.id) \n")
                    .append(
                            "FROM profile p join customer c on p.customer_id = c.id  \n")
                    .append("left join profile_history his on p.id = his.profile_id \n")
                    .append("left join user_entity u on his.staff_id = u.id \n")
                    .append("left join user_entity uc on p.staff_id = uc.id \n")
                    .append("left join user_entity ucm on p.staff_id_cm = ucm.id  \n")
                    .append("left join user_entity uct on p.staff_id_ct = uct.id  \n")
                    .append("join transaction_type trans on trans.id = p.type \n")
                    .append("WHERE 1=1 AND his.time_received = (select MAX(his.time_received) from profile_history his where his.profile_id = p.id)");
        } else {
            sb.append(sql_select)
                    .append(
                            "FROM profile p left join customer c on p.customer_id = c.id  \n")
                    .append("left join profile_history his on p.id = his.profile_id \n")
                    .append("left join user_entity u on his.staff_id = u.id  \n")
                    .append("left join user_entity uc on p.staff_id = uc.id \n")
                    .append("left join user_entity ucm on p.staff_id_cm = ucm.id  \n")
                    .append("left join user_entity uct on p.staff_id_ct = uct.id  \n")
                    .append("join transaction_type trans on trans.id = p.type \n")
                    .append("WHERE 1=1 AND his.time_received = (select MAX(his.time_received) from profile_history his where his.profile_id = p.id)");

        }

        String sql_search_name = "AND (p.cif LIKE :txtSearch OR c.name LIKE :txtSearch OR u.full_name LIKE :txtSearch OR uct.full_name LIKE :txtSearch OR ucm.full_name LIKE :txtSearch "
                +
                "OR uc.full_name LIKE :txtSearch OR p.return_reason LIKE :txtSearch OR p.review_note LIKE :txtSearch OR p.note LIKE :txtSearch ";
        String sql_search_value = "OR CAST(p.value AS varchar(100)) LIKE :txtSearch  OR CAST(p.type AS varchar(100))  LIKE :txtSearch  OR CAST(p.number_of_bill AS varchar(100))  LIKE :txtSearch "
                + "OR CAST(p.number_of_po AS varchar(100)) LIKE :txtSearch OR CAST(p.state AS varchar(100)) LIKE :txtSearch) \n";

        if (paramSearch.containsKey("code")) {
            String deparmentCode = paramSearch.get("code").toString();

            if (!DataUtils.isNullOrEmpty(deparmentCode)) {
                // default null from client
                String username = paramSearch.containsKey("username") ? paramSearch.get("username").toString() : "";

                if (!DataUtils.isNullOrEmpty(username)
                        && !username.toLowerCase().trim().contains(UserTypeEnum.ADMIN.getName())) {
                    String position = paramSearch.containsKey("position") ? paramSearch.get("position").toString() : "";
                    String sql_filter = "";

                    switch (deparmentCode) {
                        case "QTTD":
                            String sql_qttd = " AND trans.type IN (1,2) ";
                            sql_filter = " AND p.state NOT IN (0) ";
                            sb.append(sql_qttd)
                                    .append(sql_filter);
                            if (PositionTypeEnum.CHUYENVIEN.getName().equals(position)) {
                                // QTTD view theo username đối với chuyên viên

                                if (paramSearch.containsKey("username")) {
                                    if (!DataUtils.isNullOrEmpty(paramSearch.get("username"))) {
                                        sb.append(" AND  ucm.username = :username");
                                        parameters.put("username",
                                                paramSearch.get("username").toString()
                                                        .toLowerCase());
                                    }

                                }
                            } else {
                                if (paramSearch.containsKey("usernameByCode")) {
                                    if (!DataUtils.isNullOrEmpty(paramSearch.get("usernameByCode"))) {
                                        sb.append(" AND ucm.username like :usernameByCode");
                                        parameters.put("usernameByCode",
                                                formatLike((String) paramSearch.get("usernameByCode").toString()
                                                        .toLowerCase()));
                                    }

                                }
                            }

                            break;
                        case "GDKH":
                            // transaction - type : GDKH có 2 luồng
                            // GDKH nhìn thấy hết, bỏ phân công
                            String sql_gdkh = " AND trans.type IN (1,3) ";
                            sql_filter = " AND p.state NOT IN (0) ";
                            sb.append(sql_gdkh)
                                    .append(sql_filter);
                            // .append(sql_username);
                            if (paramSearch.containsKey("usernameByCode")) {
                                if (!DataUtils.isNullOrEmpty(paramSearch.get("usernameByCode"))) {
                                    sb.append(" AND  uct.username like :usernameByCode");
                                    parameters.put("usernameByCode",
                                            formatLike((String) paramSearch.get("usernameByCode").toString()
                                                    .toLowerCase()));
                                }

                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        if (paramSearch.containsKey("txtSearch")) {
            if (!DataUtils.isNullOrEmpty(paramSearch.get("txtSearch"))) {
                sb.append(sql_search_name).append(sql_search_value);
                parameters.put("txtSearch", formatLike((String) paramSearch.get("txtSearch").toString().toLowerCase()));
            }

        }

        if (paramSearch.containsKey("fromDate")) {
            if (!DataUtils.isNullOrEmpty(paramSearch.get("fromDate"))) {
                sb.append(" AND p.created_date >= convert(date,:fromDate)");
                parameters.put("fromDate", paramSearch.get("fromDate").toString().toLowerCase());
            }

        }

        if (paramSearch.containsKey("toDate")) {
            if (!DataUtils.isNullOrEmpty(paramSearch.get("toDate"))) {
                sb.append(" AND p.created_date <= convert(date,:toDate)");
                parameters.put("toDate", paramSearch.get("toDate").toString().toLowerCase());
            }

        }

        if (paramSearch.containsKey("state")) {
            if (!DataUtils.isNullOrEmpty(paramSearch.get("state"))) {
                sb.append(" AND p.state = :state");
                parameters.put("state", DataUtils.parseToInt(paramSearch.get("state").toString()));
            }

        }

        if (paramSearch.containsKey("dashboard")) {
            // sb.append(
            // " AND ((p.state = 7 AND p.process_date >= DATEADD(minute, -5,
            // CURRENT_TIMESTAMP)) OR p.state NOT IN (7)) ORDER BY p.process_date ASC OFFSET
            // 0 ROWS FETCH NEXT 20 ROWS ONLY");

            // bỏ p.state NOT IN (7)
            // sb.append(
            // " AND ((p.state IN (4,5,7) AND p.process_date >= DATEADD(minute, -5,
            // CURRENT_TIMESTAMP))) ORDER BY p.process_date ASC OFFSET :offset ROWS FETCH
            // NEXT 20 ROWS ONLY");
            // parameters.put("offset",
            // DataUtils.parseToInt(paramSearch.get("dashboard").toString()));

            sb.append(
                    " AND p.state IN (4,5,7) ORDER BY p.process_date ASC OFFSET :offset ROWS FETCH NEXT 20 ROWS ONLY");
            parameters.put("offset", DataUtils.parseToInt(paramSearch.get("dashboard").toString()));

            // sb.append(" AND p.process_date >= DATEADD(minute, -5, CURRENT_TIMESTAMP)
            // ORDER BY p.process_date ASC OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY");
            // sb.append(" AND p.process_date <= CURRENT_TIMESTAMP ORDER BY p.process_date
            // ASC OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY");
        } else {
            if (!count) {
                if (paramSearch.containsKey("sort")) {
                    sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY p.time_received_cm ASC"));
                } else {
                    sb.append(" ORDER BY p.time_received_cm ASC ");
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
        }

        return sb.toString();
    }

    @Override
    public ProfileDTO detailByIdAndState(Long id, Integer state) {
        Map<String, Object> parameters = new HashMap<>();

        String sql = "SELECT p.*, " +
                "u.full_name as staff_name_last, c.name as customer_name," +
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type,  trans.transaction_detail as transaction_detail, trans.additional_time_max as additional_time_max, c.type as customer_type \n"
                +
                "FROM profile p left join customer c on p.customer_id = c.id \n" +
                "left join profile_history his on p.id = his.profile_id \n" +
                "left join user_entity u on his.staff_id = u.id AND u.status = 'ACTIVE'\n" +
                "left join user_entity uc on p.staff_id = uc.id AND uc.status = 'ACTIVE' \n" +
                "left join user_entity ucm on p.staff_id_cm = ucm.id AND ucm.status = 'ACTIVE' \n" +
                "left join user_entity uct on p.staff_id_ct = uct.id AND uct.status = 'ACTIVE' \n" +
                "left join transaction_type trans on trans.id = p.type \n" +
                "where p.id = :id AND p.state = :state AND his.time_received = (select MAX(his.time_received) from profile_history his where his.profile_id = p.id)"
        // + " ORDER BY his.time_received ASC"
        ;
        parameters.put("id", id);
        parameters.put("state", state);
        ProfileDTO profileDTO = getSingleResult(sql, Constants.ResultSetMapping.PROFILE_DTO, parameters);
        return profileDTO;
    }

    @Override
    public ProfileDTO detailById(Long id) {
        Map<String, Object> parameters = new HashMap<>();

        String sql = "SELECT p.*, " +
                "u.full_name as staff_name_last, c.name as customer_name," +
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type,  trans.transaction_detail as transaction_detail, trans.additional_time_max as additional_time_max, c.type as customer_type   \n"
                +
                "FROM profile p left join customer c on p.customer_id = c.id \n" +
                "left join profile_history his on p.id = his.profile_id \n" +
                "left join user_entity u on his.staff_id = u.id AND u.status = 'ACTIVE'\n" +
                "left join user_entity uc on p.staff_id = uc.id AND uc.status = 'ACTIVE' \n" +
                "left join user_entity ucm on p.staff_id_cm = ucm.id AND ucm.status = 'ACTIVE' \n" +
                "left join user_entity uct on p.staff_id_ct = uct.id AND uct.status = 'ACTIVE' \n" +
                "join transaction_type trans on trans.id = p.type \n" +
                "where p.id = :id";
        parameters.put("id", id);
        // parameters.put("state", state);
        ProfileDTO profileDTO = getSingleResult(sql, Constants.ResultSetMapping.PROFILE_DTO, parameters);
        return profileDTO;
    }

    @Override
    public List<ProfileDTO> listDashboard() {
        Map<String, Object> parameters = new HashMap<>();

        String sql = "SELECT p.*, " +
                "u.full_name as staff_name_last, c.name as customer_name," +
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type, trans.transaction_detail as transaction_detail,  trans.additional_time_max as additional_time_max, c.type as customer_type    \n"
                +
                "FROM profile p left join customer c on p.customer_id = c.id \n" +
                "left join profile_history his on p.id = his.profile_id \n" +
                "left join user_entity u on his.staff_id = u.id \n" +
                "left join user_entity uc on p.staff_id = uc.id \n" +
                "left join user_entity ucm on p.staff_id_cm = ucm.id AND ucm.status = 'ACTIVE' \n" +
                "left join user_entity uct on p.staff_id_ct = uct.id AND uct.status = 'ACTIVE' \n" +
                "left join transaction_type trans on trans.id = p.type \n WHERE 1 = 1 "
                + "AND p.state IN (4,5)";

        if (checkTime != 0) {
            sql += " AND p.created_date >= DATEADD(MINUTE,-" + checkTime + ",GETDATE()) ";
        }
        sql += " AND his.time_received = (select MAX(his.time_received) from profile_history his where his.profile_id = p.id) ";
        return getResultList(sql, Constants.ResultSetMapping.PROFILE_DTO, parameters);
    }

    @Override
    public List<ProfileDTO> getProfileWithParams(Map<String, Object> params, Boolean isAsc) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        String sql = "SELECT p.*, " +
                "u.full_name as staff_name_last, c.name as customer_name," +
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type, trans.transaction_detail as transaction_detail,  trans.additional_time_max as additional_time_max, c.type as customer_type   \n"
                +
                "FROM profile p left join customer c on p.customer_id = c.id \n" +
                "left join profile_history his on p.id = his.profile_id \n" +
                "left join user_entity u on his.staff_id = u.id \n" +
                "left join user_entity uc on p.staff_id = uc.id \n" +
                "left join user_entity ucm on p.staff_id_cm = ucm.id AND ucm.status = 'ACTIVE' \n" +
                "left join user_entity uct on p.staff_id_ct = uct.id AND uct.status = 'ACTIVE' \n" +
                "join transaction_type trans on trans.id = p.type \n";

        sb.append(sql).append(
                "WHERE 1=1 AND his.time_received = (select MAX(his.time_received) from profile_history his where his.profile_id = p.id)");
        if (params.containsKey("staffId")) {
            sb.append("AND p.staff_id = :staffId ");
            parameters.put("staffId", DataUtils.parseToLong(params.get("staffId")));
        }

        if (params.containsKey("staffId_CT")) {
            if (params.get("staffId_CT").toString().trim().toUpperCase().equals("NULL")) {
                sb.append(" AND p.staff_id_ct IS NULL");
            } else {
                sb.append(" AND p.staff_id_ct = :staffId_CT");
                parameters.put("staffId_CT", DataUtils.parseToLong(params.get("staffId_CT")));

            }
            // sb.append("AND p.staff_id_ct = :staffId_CT ");
            // parameters.put("staffId_CT",
            // DataUtils.parseToLong(params.get("staffId_CT")));
        }

        if (params.containsKey("staffId_CM")) {

            if (params.get("staffId_CM").toString().trim().toUpperCase().equals("NULL")) {
                sb.append(" AND p.staff_id_cm IS NULL");
            } else {
                sb.append(" AND p.staff_id_cm = :staffId_CM");
                parameters.put("staffId_CM", DataUtils.parseToLong(params.get("staffId_CM")));

            }
        }
        if (params.containsKey("code")) {
            if (!DataUtils.isNullOrEmpty(params.get("code").toString())) {
                if (params.get("code").toString().trim().toUpperCase().equals("QTTD")) {
                    sb.append(" AND trans.type IN (1,2) ");
                }
                if (params.get("code").toString().trim().toUpperCase().equals("GDKH")) {
                    sb.append(" AND trans.type IN (1,3) ");
                }
            }
        }
        if (params.containsKey("state")) {
            sb.append(" AND p.state = :state ");
            parameters.put("state", DataUtils.parseToInt(params.get("state")));
        }

        if (params.containsKey("profileId")) {
            sb.append(" AND p.id = :profileId");
            parameters.put("profileId", DataUtils.parseToLong(params.get("profileId")));
        }

        if (isAsc) {
            sb.append(" ORDER BY p.process_date ASC");
        } else {
            sb.append(" ORDER BY p.process_date DESC");
        }

        return getResultList(sb.toString(), Constants.ResultSetMapping.PROFILE_DTO, parameters);

    }

}
