package com.eztech.fitrans.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "priority_card")
@NoArgsConstructor
@AllArgsConstructor
public class PriorityCard extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long departmentId;

    public Long userId;

    public Integer numberOfPriority;

    @Column(name = "note", columnDefinition = "nvarchar(500)")
    public String note;

}
