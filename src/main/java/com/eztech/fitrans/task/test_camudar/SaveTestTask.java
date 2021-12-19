//package com.eztech.fitrans.task.test_camudar;
//
//import com.eztech.fitrans.task.BaseTask;
//import lombok.extern.slf4j.Slf4j;
//import org.camunda.bpm.engine.delegate.DelegateExecution;
//import org.springframework.stereotype.Component;
//
//import static com.eztech.fitrans.constants.Constants.TASK_VARIABLE.DATA_RTN;
//
//@Slf4j
//@Component
//public class SaveTestTask extends BaseTask {
//    @Override
//    public void execute(DelegateExecution execution) throws Exception {
//        try {
//            execution.setVariable(DATA_RTN,"test success localte vi");
//            setSuccess(execution);
//        } catch (Exception ex) {
//            log.error(ex.getMessage(), ex);
//            this.setError(execution, ex.getMessage());
//        }
//    }
//}
