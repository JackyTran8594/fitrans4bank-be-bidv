package com.eztech.fitrans.repo;

import com.eztech.fitrans.model.OptionSetValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OptionSetValueRepository extends JpaRepository<OptionSetValue, Long> {

    List<OptionSetValue> findByOptionSetIdAndNameAndStatus(Long optionSetId, String name, String status);

    @Query(value = "SELECT po_option_set_value_id FROM po_option_set_value WHERE id != :id AND po_option_set_id=:optionSetId AND name=:name AND STATUS = :status", nativeQuery = true)
    List<Long> findByNotIdNameAndStatus(@Param("id") Long id,
                                        @Param("optionSetId") Long optionSetId,
                                        @Param("name") String name,
                                        @Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE po_option_set_value SET STATUS = :status,last_updated_by = :lastUpdatedBy, last_updated_date = :lastUpdateDate WHERE id = :id", nativeQuery = true)
    Integer updateStatusByOptionSetId(@Param("id") Long id,
                                      @Param("status") String status,
                                      @Param("lastUpdatedBy") String lastUpdatedBy,
                                      @Param("lastUpdateDate") LocalDateTime lastUpdateDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE po_option_set_value SET STATUS = :status,last_updated_by = :lastUpdatedBy, last_updated_date = :lastUpdateDate WHERE id = :id", nativeQuery = true)
    Integer updateStatus(@Param("id") Long id,
                         @Param("status") String status,
                         @Param("lastUpdatedBy") String lastUpdatedBy,
                         @Param("lastUpdateDate") LocalDateTime lastUpdateDate);

    @Query(value = "SELECT count(id) FROM po_option_set_value WHERE id != :id  AND po_option_set_id=:optionSetId AND name=:name AND STATUS > 0", nativeQuery = true)
    Long checkUnique(@Param("id") Long id,
                     @Param("optionSetId") Long optionSetId,
                     @Param("name") String name);

    @Query(value = "SELECT count(id) FROM po_option_set_value WHERE po_option_set_id=:optionSetId AND name=:name AND STATUS > 0", nativeQuery = true)
    Long checkUnique(@Param("optionSetId") Long optionSetId,
                     @Param("name") String name);

    @Transactional
    @Query(value = "DELETE FROM po_option_set_value WHERE po_option_set_id = :optionSetId", nativeQuery = true)
    void deleteByOptionSetId(@Param("optionSetId") Long optionSetId);

    @Query(value = "SELECT v.name FROM po_option_set_value v left join po_option_set o ON o.id = v.po_option_set_id WHERE o.code = :code  AND v.value=:value AND o.STATUS > 0 AND v.STATUS > 0", nativeQuery = true)
    String getName(@Param("code") String code,
                   @Param("value") String value);


}