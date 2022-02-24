package com.eztech.fitrans.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_type")
public class TransactionType extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private Integer type;
    @Column(name = "transaction_detail")
    private String transactionDetail;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "note")
    private String note;

    @Column(name = "transaction_detail_2")
    private String transactionDetail2;

    @Column(name = "standard_time_CM")
    private Integer standardTimeCM;

    @Column(name = "standard_time_CT")
    private Integer standardTimeCT;

    @Column(name = "standard_time_checker")
    private Integer standardTimeChecker;

    @Column(name = "additional_time")
    private Integer additionalTime;
}
