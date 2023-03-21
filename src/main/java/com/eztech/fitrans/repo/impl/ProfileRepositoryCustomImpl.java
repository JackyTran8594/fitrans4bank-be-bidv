package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.constants.PositionTypeEnum;
import com.eztech.fitrans.constants.UserTypeEnum;
import com.eztech.fitrans.constants.Constants.Department;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.dashboard.ProfileListDashBoardDTO;
import com.eztech.fitrans.model.Profile;
import com.eztech.fitrans.repo.ProfileRepositoryCustom;
import com.eztech.fitrans.util.CalculatingTime;
import com.eztech.fitrans.util.DataUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileRepositoryCustomImpl extends BaseCustomRepository<Profile> implements
        ProfileRepositoryCustom {

    @Value("${app.dashboard.checkTime:0}")
    private Integer checkTime;

    @Value("${app.timeConfig:0}")
    private Double timeConfig;

    @Autowired
    private CalculatingTime calculatingTime;

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
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type, trans.transaction_detail as transaction_detail, trans.additional_time_max as additional_time_max, c.type as customer_type, his.time_received as time_received_history  \n";

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
        String departmentCode = null;
        if (paramSearch.containsKey("code")) {
            departmentCode = paramSearch.get("code").toString();

            if (!DataUtils.isNullOrEmpty(departmentCode)) {
                // default null from client
                String username = paramSearch.containsKey("username") ? paramSearch.get("username").toString() : "";

                if (!DataUtils.isNullOrEmpty(username)
                        && !username.toLowerCase().trim().contains(UserTypeEnum.ADMIN.getName())) {
                    String position = paramSearch.containsKey("position") ? paramSearch.get("position").toString() : "";
                    String sql_filter = "";

                    switch (departmentCode) {
                        case "QLKH":
                            if (PositionTypeEnum.CHUYENVIEN.getName().equals(position)) {
                                // QHKH view theo username đối với chuyên viên
                                if (paramSearch.containsKey("username")) {
                                    if (!DataUtils.isNullOrEmpty(paramSearch.get("username"))) {
                                        sb.append(" AND uc.username = :username");
                                        parameters.put("username",
                                                paramSearch.get("username").toString()
                                                        .toLowerCase());
                                    }

                                }
                            }
                            // view theo phong - username by code doi voi truong phong/giam doc
                            else {
                                if (paramSearch.containsKey("departmentId")) {
                                    if (!DataUtils.isNullOrEmpty(paramSearch.get("departmentId"))) {
                                        sb.append(" AND uc.department_id = :departmentId ");
                                        Integer departmentId = DataUtils.parseToInt(paramSearch.get("departmentId"));
                                        parameters.put("departmentId", departmentId);
                                    }

                                }
                                // if (paramSearch.containsKey("usernameByCode")) {
                                //     if (!DataUtils.isNullOrEmpty(paramSearch.get("usernameByCode"))) {
                                //         sb.append(" AND uc.username like :usernameByCode ");
                                //         parameters.put("usernameByCode",
                                //                 formatLike((String) paramSearch.get("usernameByCode").toString()
                                //                         .toLowerCase()));
                                //     }

                                // }
                            }
                            break;
                        case "QTTD":
                            String sql_qttd = " AND trans.type IN (1,2) ";
                            sql_filter = " AND p.state NOT IN (0) ";

                            sb.append(sql_qttd);
                            sb.append(sql_filter);
                            if (PositionTypeEnum.CHUYENVIEN.getName().equals(position)) {
                                // QTTD view theo username đối với chuyên viên

                                if (paramSearch.containsKey("username")) {
                                    if (!DataUtils.isNullOrEmpty(paramSearch.get("username"))) {
                                        sb.append(" AND ucm.username = :username");
                                        parameters.put("username",
                                                paramSearch.get("username").toString()
                                                        .toLowerCase());
                                    }

                                }
                            } else {
                                if (paramSearch.containsKey("usernameByCode")) {
                                    // if (!DataUtils.isNullOrEmpty(paramSearch.get("usernameByCode"))) {
                                    // sb.append(" AND ucm.username like :usernameByCode");
                                    // parameters.put("usernameByCode",
                                    // formatLike((String) paramSearch.get("usernameByCode").toString()
                                    // .toLowerCase()));
                                    // }

                                }
                            }

                            break;
                        case "GDKH":
                            // transaction - type : GDKH có 2 luồng
                            // GDKH nhìn thấy hết, bỏ phân công
                            String sql_gdkh = " AND trans.type IN (1,3) ";

                            // bổ sung filter = 1,3,4,8,9 cho GDKH
                            sql_filter = " AND p.state NOT IN (0,1,3,4,8,9) ";
                            sb.append(sql_gdkh)
                                    .append(sql_filter);

                            // if (paramSearch.containsKey("usernameByCode")) {
                            //     if (!DataUtils.isNullOrEmpty(paramSearch.get("usernameByCode"))) {
                            //         sb.append(" AND uct.username like :usernameByCode ");
                            //         parameters.put("usernameByCode",
                            //                 formatLike((String) paramSearch.get("usernameByCode").toString()
                            //                         .toLowerCase()));
                            //     }

                            // }
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
                sb.append(" AND convert(date,p.created_date) >= convert(date,:fromDate)");
                parameters.put("fromDate", paramSearch.get("fromDate").toString());

            }

        }

        if (paramSearch.containsKey("toDate")) {
            if (!DataUtils.isNullOrEmpty(paramSearch.get("toDate"))) {
                sb.append(" AND convert(date,p.created_date) <= convert(date,:toDate)");
                parameters.put("toDate", paramSearch.get("toDate").toString());

            }

        }

        if (paramSearch.containsKey("defaultState")) {
            if (paramSearch.get("defaultState").equals("true")) {
                sb.append(" AND p.state IN (4,5) ");
                // if(!DataUtils.isNullOrEmpty(departmentCode)) {
                // // qlkh xem tat
                // if(!departmentCode.equals("QLKH")) {

                // }
                // }

            } else {
                if (paramSearch.containsKey("state")) {
                    if (!DataUtils.isNullOrEmpty(paramSearch.get("state").toString())) {
                        Integer state = DataUtils.parseToInt(paramSearch.get("state"));
                        if (state > 0) {
                            sb.append(" AND p.state = :state ");
                            parameters.put("state", state);
                        }
                    }

                }
                // if (paramSearch.containsKey("listState")) {
                // if (!DataUtils.isNullOrEmpty(paramSearch.get("listState").toString())) {
                // Integer state = DataUtils.parseToInt(paramSearch.get("state"));
                // if (state > 0) {
                // sb.append(" AND p.state IN :state ");
                // parameters.put("state", state);
                // }
                // }
                //
                // }
            }
        }

        // if (paramSearch.containsKey("state")) {
        // if (!DataUtils.isNullOrEmpty(paramSearch.get("state"))) {
        // sb.append(" AND p.state = :state");
        // parameters.put("state",
        // DataUtils.parseToInt(paramSearch.get("state").toString()));
        // }

        // }

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
                    " AND p.state IN (4,5) ORDER BY p.process_date ASC OFFSET :offset ROWS FETCH NEXT 20 ROWS ONLY");
            parameters.put("offset", DataUtils.parseToInt(paramSearch.get("dashboard").toString()));

            // sb.append(" AND p.process_date >= DATEADD(minute, -5, CURRENT_TIMESTAMP)
            // ORDER BY p.process_date ASC OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY");
            // sb.append(" AND p.process_date <= CURRENT_TIMESTAMP ORDER BY p.process_date
            // ASC OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY");
        } else {
            if (!count) {
                if (paramSearch.containsKey("sort")) {
                    if (departmentCode.equals(Department.QLKH)) {
                        sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY p.created_date DESC"));

                    } else {
                        sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY p.time_received_cm ASC"));
                    }

                } else {
                    if (departmentCode.equals(Department.QLKH)) {
                        sb.append(" ORDER BY p.created_date DESC ");
                    } else {
                        sb.append(" ORDER BY p.time_received_cm ASC ");

                    }
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
        System.out.println("----------QUERY:" + sb.toString());
        return sb.toString();
    }

    @Override
    public ProfileDTO detailByIdAndState(Long id, Integer state) {
        Map<String, Object> parameters = new HashMap<>();

        String sql = "SELECT p.*, " +
                "u.full_name as staff_name_last, c.name as customer_name," +
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type,  trans.transaction_detail as transaction_detail, trans.additional_time_max as additional_time_max, c.type as customer_type, his.time_received as time_received_history \n"
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
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type,  trans.transaction_detail as transaction_detail, trans.additional_time_max as additional_time_max, c.type as customer_type, his.time_received as time_received_history   \n"
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
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type, trans.transaction_detail as transaction_detail,  trans.additional_time_max as additional_time_max, c.type as customer_type, his.time_received as time_received_history    \n"
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
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type, trans.transaction_detail as transaction_detail,  trans.additional_time_max as additional_time_max, c.type as customer_type, his.time_received as time_received_history   \n"
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

        }

        if (params.containsKey("staffId_CM")) {

            if (params.get("staffId_CM").toString().trim().toUpperCase().equals("NULL")) {
                sb.append(" AND p.staff_id_cm IS NULL");
            } else {
                sb.append(" AND p.staff_id_cm = :staffId_CM");
                parameters.put("staffId_CM", DataUtils.parseToLong(params.get("staffId_CM")));

            }

            if (params.containsKey("ignoreId")) {
                sb.append(" AND p.id <> :ignoreId");
                parameters.put("ignoreId", DataUtils.parseToLong(params.get("ignoreId")));
            }
        }

        if (params.containsKey("timeReceived_CT")) {
            if (params.get("timeReceived_CT").toString().trim().toUpperCase().equals("NULL")) {
                sb.append(" AND p.time_received_ct IS NULL");
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

        // giành cho bàn giao trước 16h cùng ngày
        if (params.containsKey("isToday")) {
            if (!DataUtils.isNullOrEmpty(params.get("isToday"))) {
                // tìm những bản ghi trong ngày
                LocalDateTime timeMarkerValue = calculatingTime.convertTimeMarker(timeConfig);
                parameters.put("datetimeConfig", DataUtils.localDateTimeToStringSQL(timeMarkerValue));
                if (params.get("isToday").equals(true)) {
                    String sql_time1 = " AND CAST(p.real_time_received_cm AS DATE) = CAST(CURRENT_TIMESTAMP AS DATE) ";
                    // String sql_time2 = " AND DATEPART(HOUR, p.real_time_received_cm) < " +
                    // timeConfig + " ";
                    String sql_time2 = " AND  p.real_time_received_cm < CONVERT(datetime,:datetimeConfig)";
                    sb.append(sql_time1 + sql_time2);
                } else {
                    // tìm những bản ghi sang ngày hôm sau nhưng bàn giao trong ngày)
                    // String sql_time1 = " AND CAST(p.time_received_cm AS DATE) >
                    // CAST(CURRENT_TIMESTAMP AS DATE) AND DATEPART(HOUR, p.real_time_received_cm)
                    // >= "
                    // + timeConfig + " ";
                    String sql_time1 = " AND CAST(p.time_received_cm AS DATE) > CAST(CURRENT_TIMESTAMP AS DATE) AND ( CAST(p.real_time_received_cm AS DATE) = CAST(CURRENT_TIMESTAMP AS DATE) AND  p.real_time_received_cm >= CONVERT(datetime,:datetimeConfig) ) ";
                    sb.append(sql_time1);
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

    @Override
    public List<ProfileDTO> getProfileDashboard(Map<String, Object> paramSearch) {
        // TODO Auto-generated method stub
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String selectTop = "SELECT TOP(:topNumber) ";
        String select = "SELECT ";
        String sql = " p.*, " +
                "u.full_name as staff_name_last, c.name as customer_name," +
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type, trans.transaction_detail as transaction_detail,  trans.additional_time_max as additional_time_max, c.type as customer_type, his.time_received as time_received_history   \n"
                +
                "FROM profile p left join customer c on p.customer_id = c.id \n" +
                "left join profile_history his on p.id = his.profile_id \n" +
                "left join user_entity u on his.staff_id = u.id \n" +
                "left join user_entity uc on p.staff_id = uc.id \n" +
                "left join user_entity ucm on p.staff_id_cm = ucm.id AND ucm.status = 'ACTIVE' \n" +
                "left join user_entity uct on p.staff_id_ct = uct.id AND uct.status = 'ACTIVE' \n" +
                "join transaction_type trans on trans.id = p.type \n";

        String where = "WHERE 1=1 AND p.state IN (4,5) AND his.time_received = (select MAX(his.time_received) from profile_history his where his.profile_id = p.id) \n";
        String order = "ORDER BY p.process_date ASC";

        if (paramSearch.containsKey("isOffset")) {
            if (paramSearch.get("isOffset").toString().equals("true")) {
                // offset
                if (paramSearch.containsKey("offset")) {

                    parameters.put("offset", DataUtils.parseToInt(paramSearch.get("offset")));
                    sb.append(select)
                            .append(sql)
                            .append(where)
                            .append(order)
                            .append(" OFFSET :offset ROWS ")
                            .append(" FETCH NEXT 20 ROWS ONLY ");

                }
            } else {
                // top row
                if (paramSearch.containsKey("topNumber")) {
                    parameters.put("topNumber", DataUtils.parseToInt(paramSearch.get("topNumber")));
                    sb.append(selectTop).append(sql)
                            .append(where)
                            .append(order);
                }
            }
        }

        return getResultList(sb.toString(), Constants.ResultSetMapping.PROFILE_DTO, parameters);
    }

    @Override
    public List<Profile> countProfileInday(Integer time, Integer minutes) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        String select = "SELECT * \n";
        String from = "FROM profile \n";
        String where = "WHERE 1=1 AND CAST(created_date AS DATE) = CAST(GETDATE() AS DATE)";
        String where1 = null;
        String where2 = null;
        String where3 = null;
        String where4 = null;
        sb.append(select + from + where);

        return getResultList(sb.toString(), Profile.class, parameters);
    }

    @Override
    public List<Profile> countProfileInDayByState(Integer time, Integer minutes, Integer state) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        String select = "SELECT * \n";
        String from = "FROM profile \n";
        String where1 = null;
        String where2 = null;
        String where6 = "WHERE 1=1 AND state = :state";
        String where3 = null;
        String where4 = null;
        sb.append(select + from + where6);
        parameters.put("time", time);
        parameters.put("state", state);
        if (minutes > 0) {
            where1 = " AND ((DATEPART(HOUR, real_time_received_cm) < :time AND DATEPART(MINUTE, real_time_received_cm) < :minutes AND CAST(real_time_received_cm AS DATE) = CAST(GETDATE() AS DATE))";
            where2 = " OR (DATEPART(HOUR, real_time_received_ct) < :time AND DATEPART(MINUTE, real_time_received_ct) < :minutes AND CAST(real_time_received_ct AS DATE) = CAST(GETDATE() AS DATE)))";
            where3 = " OR ((DATEPART(HOUR, real_time_received_cm) >= :time AND DATEPART(MINUTE, real_time_received_cm) >= :minutes AND CAST(real_time_received_cm AS DATE) = CAST(GETDATE() - 1 AS DATE))";
            where4 = " OR (DATEPART(HOUR, real_time_received_ct) >= :time AND DATEPART(MINUTE, real_time_received_ct) >= :minutes AND CAST(real_time_received_ct AS DATE) = CAST(GETDATE() - 1 AS DATE)))";
            parameters.put("minutes", minutes);
            sb.append(where1 + where2 + where3 + where4);
        } else {
            where1 = " AND ((DATEPART(HOUR, real_time_received_cm) < :time AND CAST(real_time_received_cm AS DATE) = CAST(GETDATE() AS DATE))";
            where2 = " OR (DATEPART(HOUR, real_time_received_ct) < :time AND CAST(real_time_received_ct AS DATE) = CAST(GETDATE() AS DATE)))";
            where3 = " OR ((DATEPART(HOUR, real_time_received_cm) > :time AND CAST(real_time_received_cm AS DATE) = CAST(GETDATE() - 1 AS DATE))";
            where4 = " OR (DATEPART(HOUR, real_time_received_ct) > :time AND CAST(real_time_received_ct AS DATE) = CAST(GETDATE() - 1 AS DATE)))";
            sb.append(where1 + where2 + where3 + where4);
        }
        return getResultList(sb.toString(), Profile.class, parameters);
    }

    @Override
    public List<ProfileDTO> countProfileInDayByListState(List<Integer> state,
            List<Integer> transactionType, String code, Map<String, Object> params) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        String select = "SELECT p.*, ";

        String column = " u.full_name as staff_name_last, c.name as customer_name," +
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type, "
                +
                "trans.transaction_detail as transaction_detail,  trans.additional_time_max as additional_time_max, c.type as customer_type, "
                +
                "his.time_received as time_received_history   \n";

        String from = "FROM profile AS p \n";

        String join1 = "LEFT JOIN transaction_type AS trans ON p.type = trans.id \n";
        String join2 = "LEFT JOIN customer AS c ON p.customer_id = c.id \n";
        String join3 = "LEFT JOIN  user_entity uc on p.staff_id = uc.id \n";
        String join4 = "LEFT JOIN  user_entity ucm on p.staff_id_cm = ucm.id \n";
        String join5 = "LEFT JOIN  user_entity uct on p.staff_id_ct = uct.id \n";
        String join6 = "LEFT JOIN  profile_history his on p.id = his.profile_id \n";
        String join7 = "LEFT JOIN  user_entity u on his.staff_id = u.id \n";

        String where1 = null;
        String where2 = null;
        String where6 = "WHERE 1=1 AND p.state IN :listState AND trans.type IN :transactionType " +
                "AND his.state IN :listState \n";
        sb.append(select + column + from + join1 + join2 + join3 + join4 + join5 + join6 + join7 + where6);
        // sb.append(select + from + join1 + where6);
        // parameters.put("time", time);
        // parameters.put("minutes", minutes);
        parameters.put("listState", state);
        parameters.put("transactionType", transactionType);

        if (code.equals(Constants.Department.QTTD)) {
            where1 = " AND  CAST(p.real_time_received_cm AS DATE) = CAST(GETDATE() AS DATE)";
            if (params.containsKey("username")) {
                if (DataUtils.notNull(params.get("username"))) {
                    String where = " AND ucm.username = :username ";
                    parameters.put("username", params.get("username").toString().toLowerCase());
                    sb.append(where);
                }
            }

            if (params.containsKey("isToday")) {
                if (params.get("isToday").toString().equals("true")) {
                    String where = " AND CAST(p.real_time_received_cm AS DATE) = CAST(GETDATE() AS DATE)  ";
                    sb.append(where);
                }

            }

            sb.append(where1);
        }
        if (code.equals(Constants.Department.GDKH)) {
            where2 = " AND  CAST(p.real_time_received_ct AS DATE) = CAST(GETDATE() AS DATE)";
            sb.append(where2);

            if (params.containsKey("time_received_ct")) {
                if (params.get("time_received_ct").toString().equals("NULL")) {
                    String where = " AND p.time_received_ct is NULL ";
                    sb.append(where);
                }

            }

            if (params.containsKey("isToday")) {
                if (params.get("isToday").toString().equals("true")) {
                    String where = " AND CAST(p.real_time_received_ct AS DATE) = CAST(GETDATE() AS DATE)  ";
                    sb.append(where);
                }

            }

            if (params.containsKey("username")) {
                if (DataUtils.notNull(params.get("username"))) {
                    String where = " AND uct.username = :username";
                    parameters.put("username", params.get("username").toString().toLowerCase());
                    sb.append(where);
                }
            }

        }
        if (code.equals(Constants.Department.QLKH)) {
            where2 = " AND  CAST(p.real_time_received_ct AS DATE) = CAST(GETDATE() AS DATE)";
            sb.append(where2);

            if (params.containsKey("time_received_ct")) {
                if (params.get("time_received_ct").toString().equals("NULL")) {
                    String where = " AND p.time_received_ct is NULL ";
                    sb.append(where);
                }

            }

            if (params.containsKey("isToday")) {
                if (params.get("isToday").toString().equals("true")) {
                    String where = " AND CAST(p.real_time_received_ct AS DATE) = CAST(GETDATE() AS DATE)  ";
                    sb.append(where);
                }

            }

            // if (params.containsKey("username")) {
            //     if (DataUtils.notNull(params.get("username"))) {
            //         String where = " AND uct.username = :username";
            //         parameters.put("username", params.get("username").toString().toLowerCase());
            //         sb.append(where);
            //     }
            // }

        }

        return getResultList(sb.toString(), Constants.ResultSetMapping.PROFILE_DTO, parameters);
    }

    @Override
    public List<ProfileDTO> countProfileByListState(List<Integer> state, List<Integer> transactionType, String code,
            Map<String, Object> params) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        String select = "SELECT p.*, ";

        String column = " u.full_name as staff_name_last, c.name as customer_name," +
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type, "
                +
                "trans.transaction_detail as transaction_detail,  trans.additional_time_max as additional_time_max, c.type as customer_type, "
                +
                "his.time_received as time_received_history   \n";

        String from = "FROM profile AS p \n";

        String join1 = "LEFT JOIN transaction_type AS trans ON p.type = trans.id \n";
        String join2 = "LEFT JOIN customer AS c ON p.customer_id = c.id \n";
        String join3 = "LEFT JOIN  user_entity uc on p.staff_id = uc.id \n";
        String join4 = "LEFT JOIN  user_entity ucm on p.staff_id_cm = ucm.id \n";
        String join5 = "LEFT JOIN  user_entity uct on p.staff_id_ct = uct.id \n";
        String join6 = "LEFT JOIN  profile_history his on p.id = his.profile_id \n";
        String join7 = "LEFT JOIN  user_entity u on his.staff_id = u.id \n";

        String where1 = null;
        String where2 = null;
        String where6 = "WHERE 1=1 AND p.state IN :listState AND trans.type IN :transactionType " +
                "AND his.state IN :listState \n";
        sb.append(select + column + from + join1 + join2 + join3 + join4 + join5 + join6 + join7 + where6);
        // sb.append(select + from + join1 + where6);
        parameters.put("listState", state);
        parameters.put("transactionType", transactionType);



        if (code.equals(Constants.Department.QTTD)) {
            // hồ sơ đẫ nhận
            if (params.containsKey("real_time_received_cm")) {
                if (params.get("real_time_received_cm").toString().equals("NOTNULL")) {
                    String where = " AND p.real_time_received_cm IS NOT NULL ";
                    sb.append(where);
                }

            }

            if (params.containsKey("isToday")) {
                if (params.get("isToday").toString().equals("true")) {
                    String where = " AND CAST(p.real_time_received_cm AS DATE) = CAST(GETDATE() AS DATE)  ";
                    sb.append(where);
                }

            }


            if(params.containsKey("username")) {
                if(DataUtils.notNull(params.get("username"))) {
                    String where = " AND ucm.username = :username";
                    parameters.put("username", params.get("username").toString().toLowerCase());
                    sb.append(where);
                }
            }
        }
        if (code.equals(Constants.Department.GDKH)) {
            // hồ sơ đẫ nhận
            if (params.containsKey("real_time_received_ct")) {
                if (params.get("real_time_received_ct").toString().equals("NOTNULL")) {
                    String where = " AND p.real_time_received_ct IS NOT NULL ";
                    sb.append(where);
                }

            }

            if (params.containsKey("isToday")) {
                if (params.get("isToday").toString().equals("true")) {
                    String where = " AND CAST(p.real_time_received_ct AS DATE) = CAST(GETDATE() AS DATE)  ";
                    sb.append(where);
                }

            }

            if (params.containsKey("username")) {
                if (DataUtils.notNull(params.get("username"))) {
                    String where = " AND uct.username = :username";
                    parameters.put("username", params.get("username").toString().toLowerCase());
                    sb.append(where);
                }
            }
        }

        if (code.equals(Constants.Department.QLKH)) {
            // hồ sơ đẫ nhận
            if (params.containsKey("real_time_received_ct")) {
                if (params.get("real_time_received_ct").toString().equals("NOTNULL")) {
                    String where = " AND p.real_time_received_ct IS NOT NULL ";
                    sb.append(where);
                }

            }

            if (params.containsKey("isToday")) {
                if (params.get("isToday").toString().equals("true")) {
                    String where = " AND CAST(p.real_time_received_ct AS DATE) = CAST(GETDATE() AS DATE)  ";
                    sb.append(where);
                }

            }

            // if (params.containsKey("username")) {
            //     if (DataUtils.notNull(params.get("username"))) {
            //         String where = " AND uct.username = :username";
            //         parameters.put("username", params.get("username").toString().toLowerCase());
            //         sb.append(where);
            //     }
            // }
        }

        return getResultList(sb.toString(), Constants.ResultSetMapping.PROFILE_DTO, parameters);
    }

}
