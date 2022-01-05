package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.OptionSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OptionSetRepository extends JpaRepository<OptionSet, Long>, OptionSetRepositoryCustom {
    List<OptionSet> findByCodeAndStatus(String code, String status);

    @Query(value = "SELECT po_option_set_id FROM po_option_set WHERE id != :id AND code=:code AND STATUS = :status", nativeQuery = true)
    List<Long> findByNotIdCodeAndStatus(@Param("id") Long id,
                                        @Param("code") String code,
                                        @Param("status") String status);

    @Query(value = "SELECT po_option_set_id FROM po_option_set WHERE id IN :listId", nativeQuery = true)
    List<Long> findByListId(@Param("listId") List<Long> listId);

    @Query(value = "SELECT count(po_option_set_id) FROM po_option_set WHERE id != :id AND code=:code AND STATUS > 0", nativeQuery = true)
    Long checkUnique(@Param("id") Long id, @Param("code") String code);

    @Query(value = "SELECT count(po_option_set_id) FROM po_option_set WHERE code=:code AND STATUS > 0", nativeQuery = true)
    Long checkUnique(@Param("code") String code);

    @Modifying
    @Transactional
    @Query(value = "UPDATE po_option_set SET STATUS = :status,last_updated_by = :lastUpdatedBy, last_updated_date = :lastUpdateDate WHERE id = :id", nativeQuery = true)
    Integer updateStatus(@Param("id") Long id,
                         @Param("status") Integer status,
                         @Param("lastUpdatedBy") String lastUpdatedBy,
                         @Param("lastUpdateDate") LocalDateTime lastUpdateDate);
}
