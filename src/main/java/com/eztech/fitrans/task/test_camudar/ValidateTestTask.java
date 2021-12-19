//package com.eztech.fitrans.task.test_camudar;
//
//import com.eztech.fitrans.task.BaseTask;
//import lombok.extern.slf4j.Slf4j;
//import org.camunda.bpm.engine.delegate.DelegateExecution;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//
//import static com.eztech.fitrans.constants.Constants.TASK_VARIABLE.TEST_DATA;
//
//@Slf4j
//@Component
//public class ValidateTestTask extends BaseTask {
//    @Override
//    public void execute(DelegateExecution execution) throws Exception {
//        try {
//            String testData = (String) execution.getVariable(TEST_DATA);
//            Assert.notNull(testData, "TEST_DATA is not null");
//            setSuccess(execution);
//        } catch (Exception ex) {
//            log.error(ex.getMessage(), ex);
//            setError(execution, ex.getMessage());
//        }
//    }
//}
