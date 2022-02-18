package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.ProfileHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileHistoryRepository extends JpaRepository<ProfileHistory, Long>, ProfileHistoryRepositoryCustom {

}
