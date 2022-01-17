package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.dto.request.ChatMessage;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.event.ScheduledTasks;
import com.eztech.fitrans.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
public class WebsocketController {
    @Autowired
    @Qualifier("ayncTaskExecutor")
    private TaskExecutor threadPoolTaskExecutor;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @Autowired
    private ProfileService service;

    //Web gui message init --> Tra ve list data hồ sơ cho web hiển thị dashboard qua topic /topic/profiles
    @MessageMapping("/init")
    @SendTo("/topic/profiles")
    public List<ProfileDTO> init(ChatMessage user) throws Exception {
        List<ProfileDTO> listData = service.dashboard();
        return listData;
    }

}