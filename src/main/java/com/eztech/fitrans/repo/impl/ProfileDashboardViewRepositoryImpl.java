package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.model.ProfileListDashBoard;
import com.eztech.fitrans.model.view.ProfileDashBoardView;
import com.eztech.fitrans.repo.DashboardRepository;
import com.eztech.fitrans.repo.DashboardRepositoryCustom;
import com.eztech.fitrans.repo.ProfileDashboardViewRepository;
import com.eztech.fitrans.util.DataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileDashboardViewRepositoryImpl extends BaseCustomRepository<ProfileDashBoardView> implements ProfileDashboardViewRepository {

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
    public List<ProfileDashBoardView> profileInDayByListStateCM(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> params) {
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

        return getResultList(sb.toString(), ProfileDashBoardView.class, parameters);
    }

    @Override
    public List<ProfileDashBoardView> profileInDayByListStateCT(List<Integer> state, List<Integer> transactionType, String code, Map<String, Object> params) {
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


        return getResultList(sb.toString(), ProfileDashBoardView.class, parameters);
    }


    @Override
    public String buildQuery(Map<String, Object> paramSearch, Map<String, Object> parameters, boolean count) {
        return null;
    }

    @Override
    public List<ProfileDashBoardView> countProfileInDayByListState(List<Integer> state, List<Integer> transactionType,
            String code, Map<String, Object> params) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        String select = "SELECT p.*, ";

        String column = 
        // " u.full_name as staff_name_last," + 
        " c.name as customer_name," +
                "uc.full_name as staff_name, ucm.full_name as staff_name_cm, uct.full_name as staff_name_ct, trans.type as transaction_type, "
                +
                "trans.transaction_detail as transaction_detail,  trans.additional_time_max as additional_time_max, c.type as customer_type" 
                // + ", "
                // +
                // "his.time_received as time_received_history  " 
                +  "\n"
                ;

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
                String where7 = " AND his.time_received = (select MAX(his.time_received) from profile_history his where his.profile_id = p.id) \n";
        sb.append(select + column + from + join1 + join2 + join3 + join4 + join5 + join6 + join7 + where6 + where7);

   
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

        

        }

        return getResultList(sb.toString(), ProfileDashBoardView.class, parameters);
    }
}
