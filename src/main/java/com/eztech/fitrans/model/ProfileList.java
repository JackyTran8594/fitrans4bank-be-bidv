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
@Table(name = "profile_list")
public class ProfileList extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "profile_list_id")
    private String profileListId;
    @Column(name = "type")
    private String type;
    @Column(name = "amount")
    private Integer amount;
    @Column(name = "profile_status")
    private String profileStatus;
    @Column(name = "note")
    private String note;
}
