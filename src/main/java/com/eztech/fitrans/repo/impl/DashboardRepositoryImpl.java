package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.model.ProfileListDashBoard;
import com.eztech.fitrans.repo.DashboardRepository;
import com.eztech.fitrans.repo.DashboardRepositoryCustom;
import com.eztech.fitrans.util.DataUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardRepositoryImpl extends BaseCustomRepository<ProfileListDashBoard> implements DashboardRepository {

    /**
     * giành cho dashboard của Quản trị tín dụng
     *
     * @param state
     * @param transactionType
     * @param code
     * @param params
     * @return
     */
    @Override
    public List<ProfileListDashBoard> profileInDayByListStateCM(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> params) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        String select = "SELECT ROW_NUMBER() OVER (ORDER BY temptbl.username) as id, temptbl.full_name, temptbl.username, Max(temptbl.process_date) as process_date, " +
                "SUM(temptbl.standard_time_checker) as time_checker, SUM(temptbl.standard_time_ct) as standard_time_ct, " +
                "SUM(temptbl.standard_time_cm) as standard_time_cm, " +
                "COUNT(*) as number_of_profile," +
                "SUM(temptbl.expired_time) as total_time_expired  \n";


        String from = "FROM \n";

        String subSelect = " SELECT ue.full_name, ue.username , p.id, p.state, p.process_date, p.time_received_ct, " +
                "trans.standard_time_checker, trans.standard_time_ct,trans.standard_time_cm, " +
                "DATEDIFF(MINUTE, CURRENT_TIMESTAMP , CONVERT(DATETIME, p.process_date)) as expired_time \n";

        String fromSub = "FROM profile AS p \n";

        String join1 = "LEFT JOIN transaction_type AS trans ON p.type = trans.id \n";
        String join2 = "LEFT JOIN user_entity AS ue ON p.staff_id_cm = ue.id \n";
        String join3 = "LEFT JOIN department as d on d.id = ue.department_id \n";
        String where6 = "WHERE 1=1 AND p.state IN :listState AND trans.type IN :transactionType ";
        String where7 = " AND d.code = :code ";
        String groupByOfSub = " GROUP BY ue.full_name, ue.username, p.id, p.state, p.process_date, p.time_received_ct," +
                "trans.standard_time_checker,trans.standard_time_ct,trans.standard_time_cm";
        String tempTable = " as temptbl \n";
        String groupByOfTempTable = " GROUP BY temptbl.full_name, temptbl.username";
        parameters.put("listState", state);
        parameters.put("code", code);
        parameters.put("transactionType", transactionType);


        if (code.equals(Constants.Department.QTTD)) {

            String where1 = " AND  CAST(p.real_time_received_cm AS DATE) = CAST(GETDATE() AS DATE) ";
            String whereAll = where6 + where7 + where1;
            if(params.containsKey("username")) {
                if(DataUtils.notNull(params.get("username"))) {
                    String where = " AND ue.username =: username";
                    parameters.put("username", params.get("username").toString().toLowerCase());
                    whereAll += where;
                }
            }
            sb.append(select + from + "(" + subSelect + fromSub + join1 + join2 + join3 + whereAll + groupByOfSub + ")" + tempTable + groupByOfTempTable);

        }

        return getResultList(sb.toString(), ProfileListDashBoard.class, parameters);
    }

    @Override
    public List<ProfileListDashBoard> profileInDayByListStateCT(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> params) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        String select = "SELECT ROW_NUMBER() OVER (ORDER BY temptbl.username) as id, temptbl.full_name, temptbl.username, Max(temptbl.process_date) as process_date, " +
                "SUM(temptbl.standard_time_checker) as time_checker, SUM(temptbl.standard_time_ct) as standard_time_ct, " +
                "SUM(temptbl.standard_time_cm) as standard_time_cm, " +
                "COUNT(*) as number_of_profile, " +
                "SUM(temptbl.expired_time) as total_time_expired \n";

        String from = "FROM \n";

        String subSelect = " SELECT ue.full_name, ue.username , p.id, p.state, p.process_date, p.time_received_ct, " +
                "trans.standard_time_checker, trans.standard_time_ct,trans.standard_time_cm, " +
                "DATEDIFF(MINUTE, CURRENT_TIMESTAMP , CONVERT(DATETIME, p.process_date)) as expired_time \n";

        String fromSub = "FROM profile AS p \n";
        String join1 = "LEFT JOIN transaction_type AS trans ON p.type = trans.id \n";
        String join2 = "LEFT JOIN user_entity AS ue ON p.staff_id_ct = ue.id \n";
        String join3 = "LEFT JOIN department as d on d.id = ue.department_id \n";
        String where6 = "WHERE 1=1 AND p.state IN :listState AND trans.type IN :transactionType ";
        String where7 = " AND d.code = :code ";
        String groupByOfSub = " GROUP BY ue.full_name, ue.username, p.id, p.state, p.process_date, p.time_received_ct," +
                "trans.standard_time_checker,trans.standard_time_ct,trans.standard_time_cm";
        String tempTable = " as temptbl \n";
        String groupByOfTempTable = " GROUP BY temptbl.full_name, temptbl.username";
        parameters.put("listState", state);
        parameters.put("code", code);
        parameters.put("transactionType", transactionType);

        if (code.equals(Constants.Department.GDKH)) {

            String where1 = " AND  CAST(p.real_time_received_ct AS DATE) = CAST(GETDATE() AS DATE) ";
            String whereAll = where6 + where7 + where1;

//            sb.append(select + from + "(" + subSelect + fromSub + join1 + join2 + join3 + where6 + where7);

            if (params.containsKey("real_time_received_ct")) {

                if (params.get("real_time_received_ct").toString().equals("NOTNULL")) {
                    String where = " AND p.real_time_received_ct IS NOT NULL ";
                    whereAll += where;
                }

            }

            if(params.containsKey("username")) {
                if(DataUtils.notNull(params.get("username"))) {
                    String where = " AND ue.username =: username";
                    parameters.put("username", params.get("username").toString().toLowerCase());
                    whereAll += where;
                }
            }

            sb.append(select + from + "(" + subSelect + fromSub + join1 + join2 + join3 + whereAll + groupByOfSub + ")" + tempTable + groupByOfTempTable);
//            sb.append(groupByOfSub + ")" + tempTable + groupByOfTempTable);
        }


        return getResultList(sb.toString(), ProfileListDashBoard.class, parameters);
    }

    @Override
    public List search(Map searchDTO, Class aClass) {
        return null;
    }

    @Override
    public Long count(Map searchDTO) {
        return null;
    }

    @Override
    public Integer updateStatus(Long id, String status, String lastUpdatedBy, LocalDateTime lastUpdateDate) {
        return null;
    }

    @Override
    public Boolean checkExits(Long id, String code) {
        return null;
    }

    @Override
    public String buildQuery(Map<String, Object> paramSearch, Map<String, Object> parameters, boolean count) {
        return null;
    }
}
