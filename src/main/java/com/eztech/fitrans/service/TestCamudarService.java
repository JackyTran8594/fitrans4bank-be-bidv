//package com.eztech.fitrans.service;
//
//import com.eztech.fitrans.constants.CamundaProcess;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static com.eztech.fitrans.constants.Constants.TASK_VARIABLE.*;
//
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class TestCamudarService {
//    private final CamundaService camundaService;
//
//    public String process(String locale) {
//        Map<String, Object> variables = new HashMap<>();
//        variables.put(COMMAND, null);
//        variables.put(TEST_DATA, "test");
//        String processName = CamundaProcess.CAMUNDA_TEST.getName() + locale;
//        String processInstanceId = camundaService.startProcessInstanceByKey(processName, variables);
//        String rtn = (String) camundaService.getHistoricVariableByName(processInstanceId, DATA_RTN).getValue();
//        return rtn;
//    }
//
//}
