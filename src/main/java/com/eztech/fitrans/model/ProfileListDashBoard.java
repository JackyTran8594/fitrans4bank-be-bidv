package com.eztech.fitrans.model;

import com.eztech.fitrans.config.formatdate.LocalDateTimeDeserializer;
import com.eztech.fitrans.config.formatdate.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Immutable
@Getter
public class ProfileListDashBoard implements Serializable {
    @Id
    public Long Id;

    @Column(name = "username")
    public String username;

    @Column(name = "full_name")
    public String fullName;

//    @Column(name = "state")
//    public Integer state;

//    @Column(name = "totl_time_processing")
//    public Integer totalTimeProcessing; //=> tổng thời gian dự kiến xử lý

//    @Column(name = "remain_time_ct")
//    public Integer remainTimeCT; //=> tổng thời gian dự kiến xử lý

    @Column(name = "time_checker")
    public Integer timeChecker;

    @Column(name = "standard_time_ct")
    public Integer standardTimeCT;

    @Column(name = "standard_time_cm")
    public Integer standardTimeCM;
//
//    @Column(name = "additional_time")
//    public Integer additionalTime;
//
    @Column(name = "total_time_expired")
    public Integer totalTimeExpired; //=> tổng thời gian quá hạn

//    @Column(name = "profile_processed")
//    public Integer profileProcessed; // => tông hồ sơ đã xử lý theo username

    @Column(name = "number_of_profile")
    public Integer numberOfProfile; // tổng hồ sơ đang xử lý theo username

//    @Column(name = "profile_exist")
//    public Integer profileExist; // tổng hồ sơ tồn theo username

//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @Column(name = "end_time")
//    public LocalDateTime endTime;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "process_date")
    public LocalDateTime processDate;

}
