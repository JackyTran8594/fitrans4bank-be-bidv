package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.constants.PositionTypeEnum;
import com.eztech.fitrans.constants.Constants.Department;
import com.eztech.fitrans.model.ReportProfileView;
import com.eztech.fitrans.repo.ReportRepository;
import com.eztech.fitrans.repo.ReportRepositoryCustom;
import com.eztech.fitrans.util.DataUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportRepositoryImpl extends BaseCustomRepository<ReportProfileView> implements ReportRepository {

    public List<ReportProfileView> search(Map<String, Object> searchDTO, Boolean isCount) {
        Map<String, Object> parameters = new HashMap<>();
        String sql = buildQuery(searchDTO, parameters, isCount);
        return getResultList(sql, ReportProfileView.class, parameters);
    }

    @Override
    public List<ReportProfileView> getProfilesWithParams(List<Integer> transactionType,
            String code, Map<String, Object> paramSearch) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();

        String sql_select = "SELECT p.*, " +
                "u.full_name as staff_name_last, c.name as customer_name," +
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type, trans.transaction_detail as transaction_detail, trans.additional_time_max as additional_time_max, c.type as customer_type, his.time_received as time_received_history  \n";

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

        String sql_search_name = " AND (p.cif LIKE :txtSearch OR c.name LIKE :txtSearch OR u.full_name LIKE :txtSearch OR uct.full_name LIKE :txtSearch OR ucm.full_name LIKE :txtSearch "
                +
                " OR uc.full_name LIKE :txtSearch OR p.return_reason LIKE :txtSearch OR p.review_note LIKE :txtSearch OR p.note LIKE :txtSearch ";
        String sql_search_value = " OR CAST(p.value AS varchar(100)) LIKE :txtSearch  OR CAST(p.type AS varchar(100))  LIKE :txtSearch  OR CAST(p.number_of_bill AS varchar(100))  LIKE :txtSearch "
                + " OR CAST(p.number_of_po AS varchar(100)) LIKE :txtSearch OR CAST(p.state AS varchar(100)) LIKE :txtSearch) \n";
        // String departmentCode = null;
        // String username_CM = paramSearch.containsKey("usernameCM") ?
        // paramSearch.get("username").toString() : "";

        if (paramSearch.containsKey("txtSearch")) {
            if (!DataUtils.isNullOrEmpty(paramSearch.get("txtSearch"))) {
                sb.append(sql_search_name).append(sql_search_value);
                parameters.put("txtSearch", formatLike((String) paramSearch.get("txtSearch").toString().toLowerCase()));
            }

        }

        // QTTD
        if (paramSearch.containsKey("usernameCM")) {
            if (!DataUtils.isNullOrEmpty(paramSearch.get("usernameCM"))) {
                // Admin hoặc trưởng phòng hoặc QTTD được xem tất
                // Không phải xem theo user
                String username = paramSearch.get("usernameCM").toString().toLowerCase();
                if (!username.contains("admin") && !username.contains("qtht")) {
                    String and = " AND ucm.username = :usernameCM ";
                    sb.append(and);
                    parameters.put("usernameCM", paramSearch.get("usernameCM").toString().toLowerCase());
                }
            }

        }

        // QLKH
        if (paramSearch.containsKey("username")) {
            if (!DataUtils.isNullOrEmpty(paramSearch.get("username"))) {
                // Admin hoặc trưởng phòng hoặc QTTD được xem tất
                // Không phải xem theo user
                String username = paramSearch.get("username").toString().toLowerCase();
                if (!username.contains("admin") && !username.contains("qtht")) {
                    String and = " AND uc.username = :username ";
                    sb.append(and);
                    parameters.put("username", paramSearch.get("username").toString().toLowerCase());
                }

            }
        }

        if (paramSearch.containsKey("departmentId")) {
            if (!DataUtils.isNullOrEmpty(paramSearch.get("departmentId"))) {
                sb.append(" AND his.department_id = :departmentId ");
                Integer departmentId = DataUtils.parseToInt(paramSearch.get("departmentId"));
                parameters.put("departmentId", departmentId);
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
            }

        }

        sb.append(" ORDER BY p.created_date DESC ");

        return getResultList(sb.toString(), ReportProfileView.class, parameters);
    }

    @Override
    public String buildQuery(Map<String, Object> paramSearch, Map<String, Object> parameters, boolean count) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        String sql_select = "SELECT ROW_NUMBER() OVER (ORDER BY p.id) as no, p.*, " +
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
                    .append("WHERE 1=1 ");
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
                    .append("WHERE 1=1 \n")
                    .append("AND his.time_received = (select MAX(his.time_received) from profile_history his where his.profile_id = p.id) \n");

        }

        String sql_search_name = " AND (p.cif LIKE :txtSearch OR c.name LIKE :txtSearch OR CAST(p.value AS varchar(100)) LIKE :txtSearch) ";

        String username = (!DataUtils.isNullOrEmpty(paramSearch.get("username")))
                ? paramSearch.get("username").toString().toLowerCase()
                : null;

        // check xem có phải admin ko, nếu admin thì xem tất
        if (!DataUtils.isNullOrEmpty(username) && !username.contains("admin") && !username.contains("qtht")) {
            
            if (paramSearch.containsKey("code")) {
                // nếu là QLKH thì phòng nào xem phòng nấy
                if (!DataUtils.isNullOrEmpty(paramSearch.get("code"))
                        && paramSearch.get("code").toString().equals(Department.QLKH)) {
                    if (paramSearch.containsKey("departmentId")) {
                        if (!DataUtils.isNullOrEmpty(paramSearch.get("departmentId"))) {
                            sb.append(" AND his.department_id = :departmentId ");
                            Integer departmentId = DataUtils.parseToInt(paramSearch.get("departmentId"));
                            parameters.put("departmentId", departmentId);
                        }

                    }
                }
                // nếu không phải qlkh: admin/lãnh đạo GDKH/QTTD thì xem tất
                if (!DataUtils.isNullOrEmpty(paramSearch.get("code"))
                        && !paramSearch.get("code").toString().equals(Department.QLKH)) {
                    // chuyenvien thì không được xem tất
                    if (paramSearch.containsKey("position")) {
                        if (!DataUtils.isNullOrEmpty(paramSearch.get("position"))
                                && paramSearch.get("position").toString()
                                        .equals(PositionTypeEnum.CHUYENVIEN.getName())) {
                            if (paramSearch.containsKey("departmentId")) {
                                if (!DataUtils.isNullOrEmpty(paramSearch.get("departmentId"))) {
                                    sb.append(" AND his.department_id = :departmentId ");
                                    Integer departmentId = DataUtils.parseToInt(paramSearch.get("departmentId"));
                                    parameters.put("departmentId", departmentId);
                                }

                            }
                        }
                    }

                }
            }
        }

        // QTTD
        // if (paramSearch.containsKey("usernameCM")) {
        if (!DataUtils.isNullOrEmpty(paramSearch.get("usernameCM"))) {
            // Admin hoặc trưởng phòng hoặc QTTD được xem tất
            // Không phải xem theo user
            // String username = paramSearch.get("usernameCM").toString().toLowerCase();
            // if (!username.contains("admin") && !username.contains("qtht")) {
            String and = " AND ucm.username = :usernameCM ";
            sb.append(and);
            parameters.put("usernameCM", paramSearch.get("usernameCM").toString().toLowerCase());
            // }
        }

        // }

        // QLKH
        // if (paramSearch.containsKey("username")) {
        if (!DataUtils.isNullOrEmpty(paramSearch.get("usernameCusMan"))) {
            // Admin hoặc trưởng phòng hoặc QTTD được xem tất
            // Không phải xem theo user
            // String username = paramSearch.get("username").toString().toLowerCase();
            // if (!username.contains("admin") && !username.contains("qtht")) {
            String and = " AND uc.username = :usernameCusMan ";
            sb.append(and);
            parameters.put("usernameCusMan", paramSearch.get("usernameCusMan").toString().toLowerCase());
        }

        // }
        // }

        if (paramSearch.containsKey("txtSearch")) {
            if (!DataUtils.isNullOrEmpty(paramSearch.get("txtSearch"))) {
                sb.append(sql_search_name);
                parameters.put("txtSearch", formatLike((String) paramSearch.get("txtSearch").toString()));
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

            }

        }

        if (!count) {
            if (paramSearch.containsKey("sort")) {
                sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY p.created_date ASC"));
            } else {
                sb.append(" ORDER BY p.created_date ASC ");
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
        // System.out.println("----------QUERY:" + sb.toString());
        return sb.toString();
    }

    @Override
    public List search(Map searchDTO, Class tClass) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'search'");
    }

    @Override
    public Long count(Map searchDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'count'");
    }

    @Override
    public Integer updateStatus(Long id, String status, String lastUpdatedBy, LocalDateTime lastUpdateDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateStatus'");
    }

    @Override
    public Boolean checkExits(Long id, String code) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkExits'");
    }

}
