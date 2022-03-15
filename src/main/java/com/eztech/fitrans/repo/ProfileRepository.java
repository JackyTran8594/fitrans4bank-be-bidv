package com.eztech.fitrans.repo;

import java.util.List;

import javax.transaction.Transactional;

import com.eztech.fitrans.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long>, ProfileRepositoryCustom {


    @Modifying
    @Transactional
     @Query(value = "DELETE  FROM profile WHERE id IN :ids", nativeQuery = true)
     Integer deleteList(@Param("ids") List<Long> id);
}
