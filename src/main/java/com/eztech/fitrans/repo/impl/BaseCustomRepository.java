package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@SuppressWarnings({"java:S2326", "java:S4977", "java:S6212"})
public abstract class BaseCustomRepository<T> {

    @PersistenceContext
    protected EntityManager entityManager;

    <T> T getSingleResult(String query, Map<String, Object> parameters, Class<T> clazz) {
        try {
            Query nativeQuery = entityManager.createNativeQuery(query);
            if (CollectionUtils.isEmpty(parameters)) {
                return clazz.cast(nativeQuery.getSingleResult());
            }
            parameters.forEach(nativeQuery::setParameter);
            return clazz.cast(nativeQuery.getSingleResult());
        } catch (NoResultException e) {
            return null;
        }
    }

    <T> T getSingleResult(String query, String mapping, Map<String, Object> parameters) {
        Query nativeQuery = entityManager.createNativeQuery(query, mapping);
        return executeSingleResultQuery(nativeQuery, parameters);
    }

    <T> T getSingleResult(String query, Class<T> clazz, Map<String, Object> parameters) {
        Query nativeQuery = entityManager.createNativeQuery(query, clazz);
        return executeSingleResultQuery(nativeQuery, parameters);
    }

    private <T> T executeSingleResultQuery(Query nativeQuery, Map<String, Object> parameters) {
        try {
            if (CollectionUtils.isEmpty(parameters)) {
                return (T) nativeQuery.getSingleResult();
            }
            parameters.forEach(nativeQuery::setParameter);
            return (T) nativeQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    <T> List<T> getResultList(String query, String mapping, Map<String, Object> parameters) {
        Query nativeQuery = entityManager.createNativeQuery(query, mapping);
        return executeResultListQuery(nativeQuery, parameters);
    }

    <T> List<T> getResultList(String query, Class<T> clazz, Map<String, Object> parameters) {
        Query nativeQuery = entityManager.createNativeQuery(query, clazz);
        return executeResultListQuery(nativeQuery, parameters);
    }

    private <T> List<T> executeResultListQuery(Query nativeQuery, Map<String, Object> parameters) {
        try {
            if (CollectionUtils.isEmpty(parameters)) {
                return nativeQuery.getResultList();
            }
            parameters.forEach(nativeQuery::setParameter);
            return nativeQuery.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    List<Object[]> getResultList(String query, Map<String, Object> parameters) {
        try {
            Query nativeQuery = entityManager.createNativeQuery(query);
            if (CollectionUtils.isEmpty(parameters)) {
                return nativeQuery.getResultList();
            }
            parameters.forEach(nativeQuery::setParameter);
            return nativeQuery.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    int executeUpdate(String query, Map<String, Object> parameters) {
        Query nativeQuery = entityManager.createNativeQuery(query);
        if (CollectionUtils.isEmpty(parameters)) {
            return nativeQuery.executeUpdate();
        }
        parameters.forEach(nativeQuery::setParameter);
        return nativeQuery.executeUpdate();
    }

    String formatLike(String param) {
        return "%" + param.trim() + "%";
    }

    Integer offetPaging(Integer page, Integer limit) {
        page = page == null ? 0 : page;
        limit = limit == null ? 10 : limit;
        return (page) * limit;
    }

    Long getCountResult(String query, Map<String, Object> parameters) {
        Query nativeQuery = entityManager.createNativeQuery(query);
        if (CollectionUtils.isEmpty(parameters)) {
            return ((BigInteger) nativeQuery.getSingleResult()).longValue();
        }
        parameters.forEach(nativeQuery::setParameter);
        return ((BigInteger) nativeQuery.getSingleResult()).longValue();
    }

    //
    String formatSort(String sort, String defaultSort) {
        if (DataUtils.notNull(sort)) {
            List<String> items = Arrays.asList(sort.split(";"));
            return formatSort(items, defaultSort);
        }
        return defaultSort;
    }

    @SuppressWarnings("java:S3776")
    String formatSort(List<String> sort, String defaultSort) {
        if (DataUtils.notNullOrEmpty(sort)) {
            StringBuilder sb = new StringBuilder();
            sb.append(" ORDER BY ");
            if (sort.get(0).contains(",")) {
                //&sort=code,asc&sort=lastUpdateDate,desc
                String[] tmpArr;
                for (String tmp : sort) {
                    tmpArr = tmp.split(",");
                    if (tmpArr.length > 1) {
                        sb.append(" ")
                                .append(DataUtils.camelToSnake(tmpArr[0]))
                                .append(" ")
                                .append(tmpArr[1])
                                .append(",");
                    } else {
                        sb.append(" ")
                                .append(DataUtils.camelToSnake(tmpArr[0]))
                                .append(" ASC,");
                    }
                }
                sb = sb.deleteCharAt(sb.length() - 1);
                sb.append(" ");
                return sb.toString();
            } else {
                //sort=code,asc
                for (String s : sort) {
                    sb.append(DataUtils.camelToSnake(s))
                            .append(" ");
                }
                sb.append(" ");
                return sb.toString();
            }


        }
        return defaultSort == null ? "" : String.format(" %s ", defaultSort);
    }

    public abstract String buildQuery(Map<String, Object> paramSearch, Map<String, Object> parameters, boolean count);

    public boolean paramNotNullOrEmpty(Map<String, Object> paramSearch, String key) {
        if (paramSearch.get(key) == null) {
            return false;
        }

        String data = String.valueOf(paramSearch.get(key));
        return DataUtils.notNullOrEmpty(data);
    }

}
