package com.eztech.fitrans.constants;

import java.io.File;

public class Constants {
    public static final String PATH_MESSAGE = "i18n/messages";
    public static final String PATH_ERROR = "i18n/errors";
    public static final String ACTION_USER = "ACTION_USER";
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";

    public static final String LIST_PROFILE_DASHBOARD = "LIST_PROFILE_DASHBOARD";

    public static final class TASK_VARIABLE {
        public static final String COMMAND = "command";
        public static final String SUCCESS = "success";
        public static final String MESSAGE = "message";
        public static final String TEST_DATA = "testData";
        public static final String DATA_RTN = "DataRtn";
    }

    public static final class RESULT {
        public static final String PROCESS_RETURN_SUCCESS = "true";
    }
    public static final class ResultSetMapping {
        public static final String USER_ENTITY_DTO = "userEntityDTO";
        public static final String PROFILE_DTO = "profileEntityDTO";
        public static final String PROFILE_HISTORY_DTO = "profileHistoryDTO";
        public static final String STAFF_CONTACT_DTO = "staffContactDTO";

    }


    public static final class MessageParam {
        public static final String PARAM_CODE = "param.code";
        public static final String PARAM_NAME = "param.name";
        public static final String PARAM_VALUE = "param.value";
        public static final String PARAM_DESCRIPTION = "param.description";
        public static final String CIF = "param.customer.cif";
        public static final String CUSTOMER_NAME = "param.customer.name";
        public static final String CUSTOMER_ADDRESS = "param.customer.address";
        public static final String CUSTOMER_TEL = "param.customer.tel";
        public static final String OPTIONSET_CODE = "param.optionset.code";
        public static final String OPTIONSET_NAME = "param.optionset.name";
        public static final String OPTIONSET_VALUE = "param.optionset.value";
        public static final String OPTIONSET_DESC = "param.optionset.description";
    }

    public static final class TemplateExcel {
        public static final String TEMPLATE_PRODUCT_XLSX = "template" + File.separator + "import" + File.separator  + "File_mau_import_template.xlsx";
        public static final String TEMPLATE_IMPORT_CUSTOMER = "template"+ File.separator +"import"+ File.separator +"template_customer.json";
    }

    public static class MsgKey {
        public static String MS0000 = "MS0000";
        public static String MS0001 = "MS0001";
        public static String MS0002 = "MS0002";
        public static String MS0003 = "MS0003";
        public static String MS0004 = "MS0004";
        public static String MS0005 = "MS0005";
    }
}
