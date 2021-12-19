//package com.eztech.fitrans.service;
//
//import com.eztech.fitrans.constants.Constants;
//import lombok.extern.slf4j.Slf4j;
//import org.camunda.bpm.engine.HistoryService;
//import org.camunda.bpm.engine.RuntimeService;
//import org.camunda.bpm.engine.history.HistoricVariableInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import java.util.List;
//import java.util.Map;
//
//import static com.eztech.fitrans.constants.Constants.RESULT.PROCESS_RETURN_SUCCESS;
//import static com.eztech.fitrans.constants.Constants.TASK_VARIABLE.SUCCESS;
//
//@Slf4j
//@Component
//public class CamundaService {
//    private final RuntimeService runtimeService;
//    private final HistoryService historyService;
//
//    @Autowired
//    public CamundaService(RuntimeService runtimeService, HistoryService historyService) {
//        this.runtimeService = runtimeService;
//        this.historyService = historyService;
//    }
//
//    public String startProcessInstanceByKey(String processName, Map<String, Object> variables) {
//        String instanceId = runtimeService.startProcessInstanceByKey(processName, variables).getProcessInstanceId();
//        log.debug("Start process instace {}", instanceId);
//        return instanceId;
//    }
//
//    public void checkResult(String processInstanceId) {
//        HistoricVariableInstance isSuccess = getHistoricVariableByName(processInstanceId, SUCCESS);
//        log.info("---> isSuccess: {}", isSuccess.getValue());
//        if (!PROCESS_RETURN_SUCCESS.equalsIgnoreCase(isSuccess.getValue().toString())) {
//            HistoricVariableInstance message = getHistoricVariableByName(processInstanceId, Constants.TASK_VARIABLE.MESSAGE);
//            throw new IllegalArgumentException(String.valueOf(message.getValue()));
//        }
//    }
//
//    public HistoricVariableInstance getHistoricVariableByName(String processInstanceId, String name) {
//        List<HistoricVariableInstance> vars = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).variableName(name).list();
//        log.debug("Get history of process instance {} with variable {}. Result contains {} item(s).", processInstanceId, name, vars.size());
//        return CollectionUtils.isEmpty(vars) ? null : vars.get(0);
//    }
//}
