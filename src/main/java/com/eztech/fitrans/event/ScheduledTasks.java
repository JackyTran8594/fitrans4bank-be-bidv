package com.eztech.fitrans.event;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private ProfileService service;

//    @Scheduled(fixedDelayString = "20000")
    public void fireGreeting() {
        List<ProfileDTO> listData = service.dashboard();
        List<ProfileDTO> listOld = null;
        try {
            String jsonOld = ThreadContext.get(Constants.LIST_PROFILE_DASHBOARD);
            listOld = DataUtils.jsonToList(jsonOld, ProfileDTO.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        //Compare if change
        boolean different = compareLists(listData, listOld);
        if (different) {
            log.info("============ScheduledTasks found change profiles --> send messsage to client============");
            ThreadContext.put(Constants.LIST_PROFILE_DASHBOARD, DataUtils.objectToJson(listData));
            this.template.convertAndSend("/topic/profiles", listData);
        }
    }

    /**
     * @param prevList
     * @param modelList
     * @return true if different
     */
    public boolean compareLists(List<ProfileDTO> prevList, List<ProfileDTO> modelList) {
        if (prevList != null && modelList == null) {
            return true;
        }
        if (prevList != null && modelList != null && prevList.size() == modelList.size()) {
            //Order list
            Collections.sort(prevList, new Comparator<ProfileDTO>() {
                public int compare(ProfileDTO o1, ProfileDTO o2) {
                    // compare two instance of `Score` and return `int` as result.
                    return o2.getId().compareTo(o2.getId());
                }
            });

            Collections.sort(modelList, new Comparator<ProfileDTO>() {
                public int compare(ProfileDTO o1, ProfileDTO o2) {
                    // compare two instance of `Score` and return `int` as result.
                    return o2.getId().compareTo(o2.getId());
                }
            });

            int lengh = prevList.size();

            ProfileDTO tmp0, tmp1;
            boolean different = false;
            for (int i = 0; i < lengh; i++) {
                tmp0 = prevList.get(i);
                tmp1 = prevList.get(i);
                if (tmp0.getId() != tmp1.getId()) {
                    different = true;
                    break;
                }

                if (tmp0.getState() != tmp1.getState()) {
                    different = true;
                    break;
                }

                if (!DataUtils.safeEqual(tmp0.getStatus(),tmp1.getStatus())) {
                    different = true;
                    break;
                }
            }
            return different;
        }
        return true;
    }
}
