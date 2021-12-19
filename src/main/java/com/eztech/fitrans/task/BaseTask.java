//package com.eztech.fitrans.task;
//
//import com.eztech.fitrans.constants.Constants;
//import lombok.extern.slf4j.Slf4j;
//import org.camunda.bpm.engine.delegate.DelegateExecution;
//import org.camunda.bpm.engine.delegate.JavaDelegate;
//
//@Slf4j
//public abstract class BaseTask implements JavaDelegate {
//
//    public void setSuccess(DelegateExecution execution) {
//        execution.setVariable(Constants.TASK_VARIABLE.SUCCESS, Boolean.TRUE);
//    }
//
//    public void setError(DelegateExecution execution, String message) {
//        execution.setVariable(Constants.TASK_VARIABLE.SUCCESS, Boolean.FALSE);
//        execution.setVariable(Constants.TASK_VARIABLE.MESSAGE, message);
//    }
//}
