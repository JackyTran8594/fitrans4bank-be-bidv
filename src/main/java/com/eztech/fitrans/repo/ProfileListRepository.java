package com.eztech.fitrans.repo;

import java.util.List;

import com.eztech.fitrans.model.ProfileList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileListRepository extends JpaRepository<ProfileList, Long>, ProfileListRepositoryCustom {

    @Query(value = "SELECT pl.* FROM profile_list as pl WHERE pl.id IN (:profileListId)", nativeQuery = true)
    List<ProfileList> findListById(@Param("profileListId") List<Long> profileListId);
}
