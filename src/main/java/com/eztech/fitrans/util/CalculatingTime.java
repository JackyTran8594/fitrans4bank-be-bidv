package com.eztech.fitrans.util;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CalculatingTime {

    @Value("${app.timeConfig}")
    private Double timeConfig;

    public CalculatingTime() {

    }

    public LocalDateTime convertTimeMarkerWithTimeReceived(Double timeMarker, LocalDateTime timeReceived) {
        double result = (double) timeMarker % 1;
        int hour = 0;
        int minutes = 0;
        int year = timeReceived.getYear();
        Month month = timeReceived.getMonth();
        int day = timeReceived.getDayOfMonth();
        if (result != 0) {
            hour = (int) timeMarker.doubleValue();
            minutes = Math.round((int) (result * 60));

        } else {
            hour = (int) timeMarker.doubleValue();
        }
        timeReceived = LocalDateTime.of(year, month, day, hour, minutes);
        return timeReceived;

    }

    public LocalDateTime convertTimeMarker(Double timeMarker) {
        if (timeMarker == null) {
            return LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
                    LocalDateTime.now().getDayOfMonth(), 16, 0);
        }
        double result = (double) timeMarker % 1;
        int hour = 0;
        int minutes = 0;
        if (result != 0) {
            hour = (int) timeMarker.doubleValue();
            minutes = Math.round((int) (result * 60));

        } else {
            hour = (int) timeMarker.doubleValue();
        }
        return LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
                LocalDateTime.now().getDayOfMonth(), hour, minutes);

    }

    /*
     * #region Tình thời gian xử lý đối với việc bàn giao hồ sơ trước 16h trong ngày
     */
    /**
     * thời gian xử lý tính toán cộng dồn , không quan tâm đến có sang ngày hôm sau
     * không
     * do đó mà sẽ ko có check time cho ngày hôm sau
     * 
     * @param realTimeReceived
     * @param standardTime
     * @param timeChecker
     * @param timeWorked
     * @param numberOfPO
     * @param numberOfBill
     * @param transactionType
     * @return
     */
    public Map<String, Object> calculatingDateFromRealTimeReceived(LocalDateTime realTimeReceived,
            Integer standardTime,
            Integer timeChecker, Integer timeWorked, Integer numberOfPO, Integer numberOfBill,
            Integer transactionType) {
        Map<String, Object> mapResult = new HashMap<>();
        LocalDateTime timeReceivedNew = null;
        int processTimeCase = 0;

        int standard = (!DataUtils.isNullOrEmpty(standardTime)) ? standardTime : 0;
        int checker = (!DataUtils.isNullOrEmpty(timeChecker)) ? timeChecker : 0;
        int worked = (!DataUtils.isNullOrEmpty(timeWorked)) ? timeWorked : 0;

        int PO = (!DataUtils.isNullOrEmpty(numberOfPO)) ? numberOfPO : 0;

        int bill = (!DataUtils.isNullOrEmpty(numberOfBill)) ? numberOfBill : 0;

        int type = (!DataUtils.isNullOrEmpty(transactionType)) ? transactionType : 0;

        LocalDateTime timeMarkerConfig = convertTimeMarker(timeConfig);

        LocalDateTime timeMarker13h30 = LocalDateTime.of(realTimeReceived.getYear(), realTimeReceived.getMonth(),
                realTimeReceived.getDayOfMonth(), 13, 30);
        LocalDateTime timeMarker11h30 = LocalDateTime.of(realTimeReceived.getYear(), realTimeReceived.getMonth(),
                realTimeReceived.getDayOfMonth(), 11, 30);

        LocalDateTime tomorrow = realTimeReceived.plusDays(1);
        LocalDateTime timeMarkerTomorrow = LocalDateTime.of(tomorrow.getYear(), tomorrow.getMonth(),
                tomorrow.getDayOfMonth(), 8, 0);

        int additionalTime = 0;
        // checking transaction type and plusing additional time
        switch (type) {
            case 1:
                if (PO >= 2) {
                    additionalTime = additionalTime + 5 * PO;
                }

                if (bill >= 2) {
                    additionalTime = additionalTime + 1 * bill;
                }
                break;
            case 2:
                break;
            default:
                break;
        }

        // thời gian này đã bao gồm cả giờ nghỉ chưa
        // do đó cần tính lại
        LocalDateTime processTime = realTimeReceived.plusMinutes(standard + checker + additionalTime - worked);

        // thời gian nhận trước 11h30
        // thời gian xử lý: trước 11h30, 11h30-13h30, 13h30-17h, sau 17h
        if (realTimeReceived.isBefore(timeMarkerConfig)) {
            // thời gian nhận mới bằng thời gian nhận cũ
            timeReceivedNew = realTimeReceived;

            // thời gian xử lý trước 11h30
            // không cần tính lại vì trước 11h30
            if (processTime.isBefore(timeMarker11h30)) {
                processTimeCase = 1;
            }
            // thời gian xử lý 11h30-13h30
            // thời gian xử lý tính lại từ mốc 13h30
            if (processTime.isAfter(timeMarker11h30) && processTime.isBefore(timeMarker13h30)) {
                processTimeCase = 2;
            }
            // thời gian xử lý 13h30 - timeConfig
            // tính lại vì sẽ loại bỏ giờ nghỉ trưa
            if (processTime.isAfter(timeMarker13h30) && processTime.isBefore(timeMarkerConfig)) {
                processTimeCase = 3;
            }
            // thời gian xử lý sau timeConfig
            if (processTime.isAfter(timeMarkerConfig)) {
                processTimeCase = 4;
            }

            processTime = calculatingTime(processTime, realTimeReceived, timeMarker11h30, timeMarker13h30,
                    timeMarkerConfig, timeMarkerTomorrow, processTimeCase, standard, checker, additionalTime, worked);

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);

        }

        // thời gian nhận từ 11h30 - 13h30
        // thời gian xử lý: 11h30 - 13h30, 13h30 - timeConfig, sau timeConfig
        if (realTimeReceived.isAfter(timeMarker11h30) && realTimeReceived.isBefore(timeMarker13h30)) {
            // thời gian nhận lấy mốc 13h30
            timeReceivedNew = timeMarker13h30;

            // thời gian xử lý 11h30-13h30
            // thời gian xử lý tính lại từ mốc 13h30
            if (processTime.isAfter(timeMarker11h30) && processTime.isBefore(timeMarker13h30)) {
                processTimeCase = 2;

            }

            // thời gian xử lý 13h30 - timeMarkerConfig
            // tính lại vì sẽ loại bỏ giờ nghỉ trưa
            if (processTime.isAfter(timeMarker13h30) && processTime.isBefore(timeMarkerConfig)) {
                processTimeCase = 3;
            }

            // thời gian xử lý sau timeMarkerConfig
            if (processTime.isAfter(timeMarkerConfig)) {
                processTimeCase = 4;
            }

            processTime = calculatingTime(processTime, realTimeReceived, timeMarker11h30, timeMarker13h30,
                    timeMarkerConfig, timeMarkerTomorrow, processTimeCase, standard, checker, additionalTime, worked);

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);
        }

        // thời gian nhận từ 13h30 - timeMarkerConfig
        // thời gian xử lý: 13h30 - timeMarkerConfig, sau timeMarkerConfig
        if (realTimeReceived.isAfter(timeMarker13h30) && realTimeReceived.isBefore(timeMarkerConfig)) {
            // thời gian nhận mới bằng thời gian nhận cũ
            timeReceivedNew = realTimeReceived;

            // thời gian xử lý từ 13h30 - timeMarkerConfig
            // không cần tính lại
            if (processTime.isAfter(timeMarker13h30) && processTime.isBefore(timeMarkerConfig)) {
                processTimeCase = 3;
            }

            // thời gian xử lý sau 17h
            if (processTime.isAfter(timeMarkerConfig)) {
                // thời gian từ 17h - thời gian xử lý => ngoài giờ hành chính
                processTimeCase = 4;

            }

            processTime = calculatingTime(processTime, realTimeReceived, timeMarker11h30, timeMarker13h30,
                    timeMarkerConfig, timeMarkerTomorrow, processTimeCase, standard, checker, additionalTime, worked);

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);

        }

        // thời gian nhận sau timeMarkerConfig

        if (realTimeReceived.isAfter(timeMarkerConfig)) {
            // thời gian xử lý không tính lại
            // thời gian nhận mới bằng thời gian nhận cũ
            timeReceivedNew = realTimeReceived;

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);
        }

        return mapResult;
    }

    /**
     * 
     * @param processTime
     * @param timeReceived
     * @param timeMarker11h30
     * @param timeMarker13h30
     * @param timeMarkerConfig
     * @param timeMarkerTomorrow
     * @param processTimeCase
     * @param standard
     * @param checker
     * @param additionalTime
     * @param worked
     * @return
     */
    public LocalDateTime calculatingTime(LocalDateTime processTime, LocalDateTime realTimeReceived,
            LocalDateTime timeMarker11h30,
            LocalDateTime timeMarker13h30, LocalDateTime timeMarkerConfig, LocalDateTime timeMarkerTomorrow,
            int processTimeCase, int standard, int checker, int additionalTime, int worked) {

        switch (processTimeCase) {
            // thời gian xử lý trước 11h30
            case 1:

                break;

            // thời gian xử lý từ 11h30 - 13h30
            case 2:
                // thời gian xử lý 11h30-13h30
                // thời gian xử lý tính lại từ mốc 13h30
                // thời gian chênh lệch giữa thời gian nhận và mốc 11h30 => thời gian làm
                if (realTimeReceived.isBefore(timeMarker11h30)) {
                    Long time1 = durationToMinute(realTimeReceived, timeMarker11h30);
                    // thời gian xử lý = thời gian xử lý cũ - thời gian đã làm
                    processTime = timeMarker13h30.plusMinutes(standard + checker + additionalTime - worked - time1);

                }
                if (realTimeReceived.isAfter(timeMarker11h30) && realTimeReceived.isBefore(timeMarker13h30)) {
                    processTime = timeMarker13h30.plusMinutes(standard + checker + additionalTime - worked);

                }

                break;

            // thời gian xử lý từ 13h30 - timeMarkerConfig
            case 3:
                // thời gian còn lại = thời gian xử lý - thời gian đã làm
                if (realTimeReceived.isBefore(timeMarker11h30)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 11h30 => thời gian làm
                    Long time1 = durationToMinute(realTimeReceived, timeMarker11h30);
                    // // thời gian nghỉ trưa : 11h30 - 13h30 => thời gian không làm
                    // Long time2 = durationToMinute(timeMarker11h30, timeMarker13h30); // không cần
                    // tính thời gian nghỉ chưa
                    processTime = timeMarker13h30
                            .plusMinutes(standard + checker + additionalTime - worked - time1);

                }
                if (realTimeReceived.isAfter(timeMarker11h30) && realTimeReceived.isBefore(timeMarker13h30)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 11h30 => thời gian làm
                    // Long time1 = durationToMinute(timeReceived, timeMarker11h30);
                    // thời gian nghỉ trưa : 11h30 - 13h30 => thời gian không làm
                    // Long time2 = durationToMinute(timeMarker11h30, timeMarker13h30); => không cần
                    // tính, chỉ cần tính thời gian đã làm
                    processTime = timeMarker13h30
                            .plusMinutes(standard + checker + additionalTime - worked);

                }
                if (realTimeReceived.isAfter(timeMarker13h30) && realTimeReceived.isBefore(timeMarkerConfig)) {

                }

                break;

            // thời gian xử lý sau timeMarkerConfig
            // không cần tính lại
            case 4:

                // if (realTimeReceived.isBefore(timeMarker11h30)) {
                // // thời gian chênh lệch giữa thời gian nhận và mốc 11h30
                // Long time1 = durationToMinute(realTimeReceived, timeMarker11h30); // thời
                // gian đã làm
                // // thời gian nghỉ trưa : 11h30 - 13h30
                // // Long time2 = durationToMinute(timeMarker11h30, timeMarker13h30); // =>
                // không
                // // cần tính chỉ cần tính thời gian đã làm
                // // thời gian đã làm : 13h30 - timeMarkerConfig
                // Long time2 = durationToMinute(timeMarker13h30, timeMarkerConfig); // thời
                // gian đã làm
                // // thời gian từ 17h - thời gian xử lý
                // // Long time3 = durationToMinute(timeMarker17h, processTime); // thời gian
                // ngoài
                // // giờ hành chính, tính cho sáng hôm sau nếu có

                // // thời gian xử lý = thời gian xử lý cũ - thời gian đã làm
                // processTime = timeMarkerTomorrow
                // .plusMinutes(standard + checker + additionalTime - worked - time1 - time2);
                // }

                // if (realTimeReceived.isAfter(timeMarker11h30) &&
                // realTimeReceived.isBefore(timeMarker13h30)) {
                // // thời gian chênh lệch giữa thời gian nhận và mốc 11h30
                // // Long time1 = durationToMinute(timeReceived, timeMarker13h30); // Trả về 0
                // nếu
                // // timeReceived >
                // // // timeMarker11h30
                // // thời gian 13h30 - timeMarkerConfigTomorrow
                // Long time2 = durationToMinute(timeMarker13h30, timeMarkerConfig); // thời
                // gian đã làm
                // // thời gian từ timeMarkerConfigTomorrow - thời gian xử lý
                // // Long time3 = durationToMinute(timeMarker17h, processTime); // thời gian
                // ngoài
                // // giờ hành chính

                // // thời gian xử lý = thời gian xử lý cũ - thời gian đã làm
                // processTime = timeMarkerTomorrow
                // .plusMinutes(standard + checker + additionalTime - worked - time2);

                // }
                // if (realTimeReceived.isAfter(timeMarker13h30) &&
                // realTimeReceived.isBefore(timeMarkerConfig)) {
                // // thời gian từ timeMarkerConfig - thời gian xử lý => ngoài giờ hành chính

                // Long time3 = durationToMinute(timeMarkerConfig, processTime);

                // processTime = timeMarkerTomorrow
                // .plusMinutes(time3);

                // }

                break;

            default:
                processTime = realTimeReceived
                        .plusMinutes(standard + checker + additionalTime - worked);
                break;
        }

        return processTime;
    }

    /* #endregion */

    /*
     * #region Tính thời gian xử lý đối với việc bàn giao hồ sơ sau 16h trong ngày
     */
    // timeWorked = thời gian đã làm -> dùng để tính đối với hoàn trả hồ sơ (trường
    // này được lưu là additional time)
    // timeReceivedOfPreviousRecord = thời gian xử lý của bản ghi trước đó
    //
    // hàm này thay cho hàm calculatingDate
    /**
     * 
     * @param timeReceived
     * @param standardTime
     * @param timeChecker
     * @param timeWorked
     * @param numberOfPO
     * @param numberOfBill
     * @param transactionType
     * @return
     */
    public Map<String, Object> calculatingDateFromTimeReceived(LocalDateTime timeReceived, Integer standardTime,
            Integer timeChecker, Integer timeWorked, Integer numberOfPO, Integer numberOfBill,
            Integer transactionType) {

        Map<String, Object> mapResult = new HashMap<>();
        LocalDateTime timeReceivedNew = null;
        int processTimeCase = 0;

        int standard = (!DataUtils.isNullOrEmpty(standardTime)) ? standardTime : 0;
        int checker = (!DataUtils.isNullOrEmpty(timeChecker)) ? timeChecker : 0;
        int worked = (!DataUtils.isNullOrEmpty(timeWorked)) ? timeWorked : 0;

        int PO = (!DataUtils.isNullOrEmpty(numberOfPO)) ? numberOfPO : 0;

        int bill = (!DataUtils.isNullOrEmpty(numberOfBill)) ? numberOfBill : 0;

        int type = (!DataUtils.isNullOrEmpty(transactionType)) ? transactionType : 0;

        double result = (double) timeConfig % 1;
        int hour = 0;
        int minutes = 0;
        if (result != 0) {
            hour = (int) timeConfig.doubleValue();
            minutes = Math.round((int) (result * 60));

        } else {
            hour = (int) timeConfig.doubleValue();
        }

        LocalDateTime timeMarkerConfig = LocalDateTime.of(timeReceived.getYear(), timeReceived.getMonth(),
                timeReceived.getDayOfMonth(), hour, minutes);

        LocalDateTime timeMarker13h30 = LocalDateTime.of(timeReceived.getYear(), timeReceived.getMonth(),
                timeReceived.getDayOfMonth(), 13, 30);
        LocalDateTime timeMarker11h30 = LocalDateTime.of(timeReceived.getYear(), timeReceived.getMonth(),
                timeReceived.getDayOfMonth(), 11, 30);

        LocalDateTime tomorrow = timeReceived.plusDays(1);
        LocalDateTime timeMarkerTomorrow = LocalDateTime.of(tomorrow.getYear(), tomorrow.getMonth(),
                tomorrow.getDayOfMonth(), 8, 0);

        int additionalTime = 0;
        // checking transaction type and plusing additional time
        switch (type) {
            case 1:
                if (PO >= 2) {
                    additionalTime = additionalTime + 5 * PO;
                }

                if (bill >= 2) {
                    additionalTime = additionalTime + 1 * bill;
                }
                break;
            case 2:
                break;
            default:
                break;
        }

        // thời gian này đã bao gồm cả giờ nghỉ chưa
        // do đó cần tính lại
        LocalDateTime processTime = timeReceived.plusMinutes(standard + checker + additionalTime - worked);

        // thời gian nhận trước 11h30
        // thời gian xử lý: trước 11h30, 11h30-13h30, 13h30-17h, sau 17h
        if (timeReceived.isBefore(timeMarker11h30)) {
            // thời gian nhận mới bằng thời gian nhận cũ
            timeReceivedNew = timeReceived;

            // thời gian xử lý trước 11h30
            // không cần tính lại vì trước 11h30
            if (processTime.isBefore(timeMarker11h30)) {
                processTimeCase = 1;
            }
            // thời gian xử lý 11h30-13h30
            // thời gian xử lý tính lại từ mốc 13h30
            if (processTime.isAfter(timeMarker11h30) && processTime.isBefore(timeMarker13h30)) {
                processTimeCase = 2;
            }
            // thời gian xử lý 13h30 - timeConfig
            // tính lại vì sẽ loại bỏ giờ nghỉ trưa
            if (processTime.isAfter(timeMarker13h30) && processTime.isBefore(timeMarkerConfig)) {
                processTimeCase = 3;
            }
            // thời gian xử lý sau timeConfig
            if (processTime.isAfter(timeMarkerConfig)) {
                processTimeCase = 4;
            }

            processTime = calculatingTimeProcess(processTime, timeReceived, timeMarker11h30, timeMarker13h30,
                    timeMarkerConfig, timeMarkerTomorrow, processTimeCase, standard, checker, additionalTime, worked);

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);

        }

        // thời gian nhận từ 11h30 - 13h30
        // thời gian xử lý: 11h30 - 13h30, 13h30 - timeConfig, sau timeConfig
        if (timeReceived.isAfter(timeMarker11h30) && timeReceived.isBefore(timeMarker13h30)) {
            // thời gian nhận lấy mốc 13h30
            timeReceivedNew = timeMarker13h30;

            // thời gian xử lý 11h30-13h30
            // thời gian xử lý tính lại từ mốc 13h30
            if (processTime.isAfter(timeMarker11h30) && processTime.isBefore(timeMarker13h30)) {
                processTimeCase = 2;

            }

            // thời gian xử lý 13h30 - timeMarkerConfig
            // tính lại vì sẽ loại bỏ giờ nghỉ trưa
            if (processTime.isAfter(timeMarker13h30) && processTime.isBefore(timeMarkerConfig)) {
                processTimeCase = 3;
            }

            // thời gian xử lý sau timeMarkerConfig
            if (processTime.isAfter(timeMarkerConfig)) {
                processTimeCase = 4;
            }

            processTime = calculatingTimeProcess(processTime, timeReceived, timeMarker11h30, timeMarker13h30,
                    timeMarkerConfig, timeMarkerTomorrow, processTimeCase, standard, checker, additionalTime, worked);

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);
        }

        // thời gian nhận từ 13h30 - timeMarkerConfig
        // thời gian xử lý: 13h30 - timeMarkerConfig, sau timeMarkerConfig
        if (timeReceived.isAfter(timeMarker13h30) && timeReceived.isBefore(timeMarkerConfig)) {
            // thời gian nhận mới bằng thời gian nhận cũ
            timeReceivedNew = timeReceived;

            // thời gian xử lý từ 13h30 - timeMarkerConfig
            // không cần tính lại
            if (processTime.isAfter(timeMarker13h30) && processTime.isBefore(timeMarkerConfig)) {
                processTimeCase = 3;
            }

            // thời gian xử lý sau 17h
            if (processTime.isAfter(timeMarkerConfig)) {
                // thời gian từ 17h - thời gian xử lý => ngoài giờ hành chính
                processTimeCase = 4;

            }

            processTime = calculatingTimeProcess(processTime, timeReceived, timeMarker11h30, timeMarker13h30,
                    timeMarkerConfig, timeMarkerTomorrow, processTimeCase, standard, checker, additionalTime, worked);

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);

        }

        // thời gian nhận sau timeMarkerConfig

        if (timeReceived.isAfter(timeMarkerConfig)) {
            // thời gian nhận mới bằng mốc 8h sáng hôm sau
            timeReceivedNew = timeMarkerTomorrow;
            processTimeCase = 4;
            // thời gian xử lý tính sang ngày hôm sau
            // processTime = timeMarkerTomorrow
            // .plusMinutes(standard + checker + additionalTime - worked);

            processTime = calculatingTimeProcess(processTime, timeReceived, timeMarker11h30, timeMarker13h30,
                    timeMarkerConfig, timeMarkerTomorrow, processTimeCase, standard, checker, additionalTime, worked);

            switch (timeReceivedNew.getDayOfWeek().getValue()) {
                case 6:
                    timeReceivedNew = timeReceivedNew.plusDays(2);
                    break;
                case 7:
                    timeReceivedNew = timeReceivedNew.plusDays(1);
                    break;
                default:
                    break;
            }

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);
        }

        return mapResult;

    }

    /**
     * 
     * @param processTime
     * @param timeReceived
     * @param timeMarker11h30
     * @param timeMarker13h30
     * @param timeMarkerConfig
     * @param timeMarkerTomorrow
     * @param processTimeCase
     * @param standard
     * @param checker
     * @param additionalTime
     * @param worked
     * @return
     */
    public LocalDateTime calculatingTimeProcess(LocalDateTime processTime, LocalDateTime timeReceived,
            LocalDateTime timeMarker11h30,
            LocalDateTime timeMarker13h30, LocalDateTime timeMarkerConfig, LocalDateTime timeMarkerTomorrow,
            int processTimeCase, int standard, int checker, int additionalTime, int worked) {

        switch (processTimeCase) {
            // thời gian xử lý trước 11h30
            case 1:

                break;

            // thời gian xử lý từ 11h30 - 13h30
            case 2:
                // thời gian xử lý 11h30-13h30
                // thời gian xử lý tính lại từ mốc 13h30
                // thời gian chênh lệch giữa thời gian nhận và mốc 11h30 => thời gian làm
                if (timeReceived.isBefore(timeMarker11h30)) {
                    Long time1 = durationToMinute(timeReceived, timeMarker11h30);
                    // thời gian xử lý = thời gian xử lý cũ - thời gian đã làm
                    processTime = timeMarker13h30.plusMinutes(standard + checker + additionalTime - worked - time1);

                }
                if (timeReceived.isAfter(timeMarker11h30) && timeReceived.isBefore(timeMarker13h30)) {
                    processTime = timeMarker13h30.plusMinutes(standard + checker + additionalTime - worked);

                }

                // tính lại thời gian xử lý nếu quá timeMarkerConfig
                // if(realTimeReceived.isAfter(timeConfigValue)) {
                if (processTime.isAfter(timeMarkerConfig)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc timeMarkerConfig => thời gian
                    // ngoài giờ
                    // SATURDAY, SUNDAY = [6,7]
                    // kiểm tra xem có phải t7, cn không
                    // SATURDAY,

                    LocalDateTime timeMarker11h30Tomorrow = null;
                    LocalDateTime timeMarker13h30Tomorrow = null;
                    LocalDateTime timeMarkerConfigTomorrow = null;

                    switch (processTime.getDayOfWeek().getValue()) {
                        // SATURDAY
                        case 6:
                            timeMarkerTomorrow = timeMarkerTomorrow.plusDays(1);
                            // thời gian xử lý 11h30-13h30 ngày hôm sau (thứ 2)
                            timeMarker11h30Tomorrow = timeMarker11h30.plusDays(2);
                            timeMarker13h30Tomorrow = timeMarker13h30.plusDays(2);
                            timeMarkerConfigTomorrow = timeMarkerConfig.plusDays(2);
                            break;
                        // SUNDAY
                        case 7:
                            // thời gian xử lý 11h30-13h30 ngày hôm sau (thứ 2)
                            timeMarker11h30Tomorrow = timeMarker11h30.plusDays(1);
                            timeMarker13h30Tomorrow = timeMarker13h30.plusDays(1);
                            timeMarkerConfigTomorrow = timeMarkerConfig.plusDays(1);
                            break;
                        // REMAIN
                        default:
                            // thời gian xử lý 11h30-13h30 ngày hôm sau
                            timeMarker11h30Tomorrow = timeMarker11h30.plusDays(1);
                            timeMarker13h30Tomorrow = timeMarker13h30.plusDays(1);
                            timeMarkerConfigTomorrow = timeMarkerConfig.plusDays(1);
                            break;
                    }

                    Long time4 = durationToMinute(timeMarkerConfig, processTime);
                    processTime = timeMarkerTomorrow.plusMinutes(time4);

                    if (processTime.isAfter(timeMarker11h30Tomorrow) && processTime.isBefore(timeMarker13h30Tomorrow)) {
                        Long time5 = durationToMinute(processTime, timeMarker11h30Tomorrow);
                        processTime = timeMarker13h30Tomorrow.plusMinutes(time5);
                    }

                    if (processTime.isAfter(timeMarkerConfigTomorrow)) {
                        Long time5 = durationToMinute(timeMarkerConfigTomorrow, processTime);
                        LocalDateTime tomorrow2 = timeMarkerConfigTomorrow.plusDays(1);
                        LocalDateTime timeMarkerTomorrow2 = LocalDateTime.of(tomorrow2.getYear(), tomorrow2.getMonth(),
                                tomorrow2.getDayOfMonth(), 8, 0);
                        processTime = timeMarkerTomorrow2.plusMinutes(time5);
                    }
                }
                // }

                break;

            // thời gian xử lý từ 13h30 - timeMarkerConfig
            case 3:
                // thời gian còn lại = thời gian xử lý - thời gian đã làm
                if (timeReceived.isBefore(timeMarker11h30)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 11h30 => thời gian làm
                    Long time1 = durationToMinute(timeReceived, timeMarker11h30);
                    // // thời gian nghỉ trưa : 11h30 - 13h30 => thời gian không làm
                    // Long time2 = durationToMinute(timeMarker11h30, timeMarker13h30); // không cần
                    // tính thời gian nghỉ chưa
                    processTime = timeMarker13h30
                            .plusMinutes(standard + checker + additionalTime - worked - time1);

                }
                if (timeReceived.isAfter(timeMarker11h30) && timeReceived.isBefore(timeMarker13h30)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 11h30 => thời gian làm
                    // Long time1 = durationToMinute(timeReceived, timeMarker11h30);
                    // thời gian nghỉ trưa : 11h30 - 13h30 => thời gian không làm
                    // Long time2 = durationToMinute(timeMarker11h30, timeMarker13h30); => không cần
                    // tính, chỉ cần tính thời gian đã làm
                    processTime = timeMarker13h30
                            .plusMinutes(standard + checker + additionalTime - worked);

                }
                if (timeReceived.isAfter(timeMarker13h30) && timeReceived.isBefore(timeMarkerConfig)) {

                }

                // tính lại thời gian xử lý nếu quá timeMarkerConfigTomorrow
                if (processTime.isAfter(timeMarkerConfig)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 17h

                    // SATURDAY, SUNDAY = [6,7]
                    // kiểm tra xem có phải t7, cn không
                    // SATURDAY,

                    LocalDateTime timeMarker11h30Tomorrow = null;
                    LocalDateTime timeMarker13h30Tomorrow = null;
                    LocalDateTime timeMarkerConfigTomorrow = null;

                    switch (processTime.getDayOfWeek().getValue()) {
                        // SATURDAY
                        case 6:
                            timeMarkerTomorrow = timeMarkerTomorrow.plusDays(1);
                            // thời gian xử lý 11h30-13h30 ngày hôm sau (thứ 2)
                            timeMarker11h30Tomorrow = timeMarker11h30.plusDays(2);
                            timeMarker13h30Tomorrow = timeMarker13h30.plusDays(2);
                            timeMarkerConfigTomorrow = timeMarkerConfig.plusDays(2);
                            break;
                        // SUNDAY
                        case 7:
                            // thời gian xử lý 11h30-13h30 ngày hôm sau (thứ 2)
                            timeMarker11h30Tomorrow = timeMarker11h30.plusDays(1);
                            timeMarker13h30Tomorrow = timeMarker13h30.plusDays(1);
                            timeMarkerConfigTomorrow = timeMarkerConfig.plusDays(1);
                            break;
                        // REMAIN
                        default:
                            // thời gian xử lý 11h30-13h30 ngày hôm sau
                            timeMarker11h30Tomorrow = timeMarker11h30.plusDays(1);
                            timeMarker13h30Tomorrow = timeMarker13h30.plusDays(1);
                            timeMarkerConfigTomorrow = timeMarkerConfig.plusDays(1);
                            break;
                    }

                    Long time4 = durationToMinute(timeMarkerConfig, processTime);
                    processTime = timeMarkerTomorrow.plusMinutes(time4);

                    // thời gian xử lý 11h30-13h30 ngày hôm sau
                    // LocalDateTime timeMarker11h30Tomorrow = timeMarker11h30.plusDays(1);
                    // LocalDateTime timeMarker13h30Tomorrow = timeMarker13h30.plusDays(1);
                    // LocalDateTime timeMarkerConfigTomorrow = timeMarkerConfig.plusDays(1);

                    if (processTime.isAfter(timeMarker11h30Tomorrow) && processTime.isBefore(timeMarker13h30Tomorrow)) {
                        Long time5 = durationToMinute(processTime, timeMarker11h30Tomorrow);
                        processTime = timeMarker13h30Tomorrow.plusMinutes(time5);
                    }

                    if (processTime.isAfter(timeMarkerConfigTomorrow)) {
                        Long time5 = durationToMinute(timeMarkerConfigTomorrow, processTime);
                        LocalDateTime tomorrow2 = timeMarkerConfigTomorrow.plusDays(1);
                        LocalDateTime timeMarkerTomorrow2 = LocalDateTime.of(tomorrow2.getYear(), tomorrow2.getMonth(),
                                tomorrow2.getDayOfMonth(), 8, 0);
                        processTime = timeMarkerTomorrow2.plusMinutes(time5);
                    }
                }

                break;

            // thời gian xử lý sau timeMarkerConfig
            case 4:

                if (timeReceived.isBefore(timeMarker11h30)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 11h30
                    Long time1 = durationToMinute(timeReceived, timeMarker11h30); // thời gian đã làm
                    // thời gian nghỉ trưa : 11h30 - 13h30
                    // Long time2 = durationToMinute(timeMarker11h30, timeMarker13h30); // => không
                    // cần tính chỉ cần tính thời gian đã làm
                    // thời gian đã làm : 13h30 - timeMarkerConfig
                    Long time2 = durationToMinute(timeMarker13h30, timeMarkerConfig); // thời gian đã làm
                    // thời gian từ 17h - thời gian xử lý
                    // Long time3 = durationToMinute(timeMarker17h, processTime); // thời gian ngoài
                    // giờ hành chính, tính cho sáng hôm sau nếu có

                    // thời gian xử lý = thời gian xử lý cũ - thời gian đã làm
                    processTime = timeMarkerTomorrow
                            .plusMinutes(standard + checker + additionalTime - worked - time1 - time2);
                }

                if (timeReceived.isAfter(timeMarker11h30) && timeReceived.isBefore(timeMarker13h30)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 11h30
                    // Long time1 = durationToMinute(timeReceived, timeMarker13h30); // Trả về 0 nếu
                    // timeReceived >
                    // // timeMarker11h30
                    // thời gian 13h30 - timeMarkerConfigTomorrow
                    Long time2 = durationToMinute(timeMarker13h30, timeMarkerConfig); // thời gian đã làm
                    // thời gian từ timeMarkerConfigTomorrow - thời gian xử lý
                    // Long time3 = durationToMinute(timeMarker17h, processTime); // thời gian ngoài
                    // giờ hành chính

                    // thời gian xử lý = thời gian xử lý cũ - thời gian đã làm
                    processTime = timeMarkerTomorrow
                            .plusMinutes(standard + checker + additionalTime - worked - time2);

                }

                if (timeReceived.isAfter(timeMarkerConfig)) {
                    // thời gian xử lý tính sang ngày hôm sau
                    processTime = timeMarkerTomorrow
                            .plusMinutes(standard + checker + additionalTime - worked);

                }

                // SATURDAY, SUNDAY = [6,7]
                // kiểm tra xem có phải t7, cn không
                // SATURDAY,
                // LocalDateTime timeMarker11h30Tomorrow = null;
                // LocalDateTime timeMarker13h30Tomorrow = null;
                // LocalDateTime timeMarkerConfigTomorrow = null;

                switch (processTime.getDayOfWeek().getValue()) {
                    // SATURDAY
                    case 6:
                        // nếu thời gian xử lý rơi vào t7 -> cộng 2 ngày
                        processTime = processTime.plusDays(2);
                        // // thời gian xử lý 11h30-13h30 ngày hôm sau
                        // timeMarker11h30Tomorrow = timeMarker11h30.plusDays(2);
                        // timeMarker13h30Tomorrow = timeMarker13h30.plusDays(2);
                        // timeMarkerConfigTomorrow = timeMarkerConfig.plusDays(2);
                        break;
                    // SUNDAY
                    case 7:
                        // nếu thời gian xử lý rơi vào cn -> cộng 1 ngày
                        processTime = processTime.plusDays(2);

                        break;
                    // REMAIN
                    default:

                        break;
                }

                // if (processTime.isAfter(timeMarker11h30Tomorrow) &&
                // processTime.isBefore(timeMarker13h30Tomorrow)) {
                // Long time5 = durationToMinute(timeMarker11h30Tomorrow, processTime);
                // processTime = timeMarker13h30Tomorrow.plusMinutes(time5);
                // }

                // if (processTime.isAfter(timeMarkerConfigTomorrow)) {
                // Long time5 = durationToMinute(timeMarkerConfigTomorrow, processTime);
                // LocalDateTime tomorrow2 = timeMarkerConfigTomorrow.plusDays(1);
                // LocalDateTime timeMarkerTomorrow2 = LocalDateTime.of(tomorrow2.getYear(),
                // tomorrow2.getMonth(),
                // tomorrow2.getDayOfMonth(), 8, 0);
                // processTime = timeMarkerTomorrow2.plusMinutes(time5);
                // }

                break;

            default:
                processTime = timeMarkerTomorrow
                        .plusMinutes(standard + checker + additionalTime - worked);
                break;
        }

        return processTime;
    }
    /* #endregion */

    public Long durationToMinute(LocalDateTime from, LocalDateTime to) {

        long minutes = ChronoUnit.MINUTES.between(from, to);
        // long seconds = ChronoUnit.SECONDS.between(from, to);
        long minutesProcess = 0;

        if (minutes > 0) {
            minutesProcess = minutesProcess + minutes;
        }

        return Long.valueOf(minutesProcess);
    }

    // public LocalDateTime calculatingTimeForAllCase() {

    // }
}
