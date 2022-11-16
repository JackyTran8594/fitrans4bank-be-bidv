package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.PriorityCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface PriorityCardRepository extends JpaRepository<PriorityCard, Long>, PriorityCardRepositoryCustom {
    
    @Modifying
    @Transactional
    @Query(value = "DELETE  FROM priority_card WHERE id IN :ids", nativeQuery = true)
    Integer delete(@Param("ids") List<Long> id);
   
}
