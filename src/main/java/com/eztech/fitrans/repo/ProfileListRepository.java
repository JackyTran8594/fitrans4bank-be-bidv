package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.ProfileList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileListRepository extends JpaRepository<ProfileList, Long>, ProfileListRepositoryCustom {

}
