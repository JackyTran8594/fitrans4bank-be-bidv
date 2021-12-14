package com.eztech.fitrans.ecommerce.task;

import com.eztech.fitrans.common.Const;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Slf4j
public abstract class BaseTask implements JavaDelegate {

    public void setSuccess(DelegateExecution execution) {
        execution.setVariable(Const.TASK_VARIABLE.SUCCESS, Boolean.TRUE);
    }

    public void setError(DelegateExecution execution, String message) {
        execution.setVariable(Const.TASK_VARIABLE.SUCCESS, Boolean.FALSE);
        execution.setVariable(Const.TASK_VARIABLE.MESSAGE, message);
    }
}
