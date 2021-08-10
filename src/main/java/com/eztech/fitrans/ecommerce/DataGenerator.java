package com.eztech.fitrans.ecommerce;

import com.eztech.fitrans.ecommerce.entity.enums.OrderStatusEnum;
import com.eztech.fitrans.ecommerce.entity.enums.OrderTypeEnum;
import com.eztech.fitrans.exception.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:indentation", "checkstyle:sizes"})
public class DataGenerator {
    private Random rnd;
    private StringBuilder sqlStatement;
    private Map<String, String> countries;
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final int CREATED_BY_USER_ID = 1;
    private static final int UPDATED_BY_USER_ID = 1;
    private Map<Integer, String> intWithStatus, intWithType;

    {
        List<String> statuses = OrderStatusEnum.asList();
        intWithStatus = new TreeMap<>();

        for (int i = 1; i <= statuses.size(); i++) {
            intWithStatus.put(i, statuses.get(i - 1));
        }
        List<String> types = OrderTypeEnum.asList();
        intWithType = new TreeMap<>();

        for (int i = 1; i <= types.size(); i++) {
            intWithType.put(i, types.get(i - 1));
        }
    }

    private String getCurrentLocalDateTimeStamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    public DataGenerator() {
        rnd = new Random(System.currentTimeMillis());
        sqlStatement = new StringBuilder();
        countries = getCountriesMap();
    }

    private String generateRandomDate(LocalDateTime startDate, LocalDateTime endDate) {

        if (startDate == null) {
            startDate = LocalDateTime.of(2009, Month.JANUARY, 1, 1, 1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        int deltaYears = endDate.getYear() - startDate.getYear();
        int range = endDate.getDayOfYear() - startDate.getDayOfYear() + deltaYears * 365;

        startDate = startDate.plusDays(generateInt(1, range));

        return startDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    private int generateInt(int minFacet, int rightFacet) {
        return minFacet + rnd.nextInt(rightFacet - minFacet + 1);
    }

    private String generateRandomStatus() {
        return intWithStatus.get(generateInt(1, 2));
    }

    private String generateRandomType() {
        return intWithType.get(generateInt(1, 5));
    }

    private BigDecimal generateRandomValue() {
        BigDecimal value = new BigDecimal(rnd.nextDouble() * 1000);
        return value.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private void appendCountries(Map<String, String> countries) {
        sqlStatement.append("INSERT INTO country (code, name) VALUES");
        String countriesStatement = countries.entrySet().stream()
                .map(entry -> "('" + entry.getKey() + "', '" + entry.getValue() + "')")
                .collect(Collectors.joining(","));
        sqlStatement.append(countriesStatement);
        sqlStatement.append(";");
    }

    private void appendOrders() {
        LocalDateTime startDateSince2013 = LocalDateTime.of(2013, Month.JANUARY, 1, 1, 1);
        sqlStatement.append("INSERT INTO orders (name, created_date, updated_date, date, value, currency," +
                " type, status, country_id, created_by_user_id, updated_by_user_id) VALUES ");

        String orderStr = Stream.generate(() -> 0)
                .limit(generateInt(1500, 2000))
                .map(entry -> "('Order " + generateInt(1, 20000) + "', '" +
                        getCurrentLocalDateTimeStamp() + "', '" + getCurrentLocalDateTimeStamp() + "', '" +
                        generateRandomDate(startDateSince2013, null) + "', " +
                        generateRandomValue() + ", 'USD', '" + generateRandomType() + "', '" +
                        generateRandomStatus() + "', " + generateInt(1, countries.size()) + ", " +
                        CREATED_BY_USER_ID + ", " + UPDATED_BY_USER_ID + ")")
                .collect(Collectors.joining(","));
        sqlStatement.append(orderStr);
        sqlStatement.append(',');

        LocalDateTime startDateSince2014 = LocalDateTime.of(2014, Month.MAY, 1, 1, 1);
        LocalDateTime endDateTill2016 = LocalDateTime.of(2016, Month.MAY, 1, 1, 1);
        orderStr = Stream.generate(() -> 0)
                .limit(generateInt(500, 800))
                .map(entry -> "('Order " + generateInt(1, 20000) + "', '" +
                        getCurrentLocalDateTimeStamp() + "', '" + getCurrentLocalDateTimeStamp() + "', '" +
                        generateRandomDate(startDateSince2014, endDateTill2016) + "', " +
                        generateRandomValue() + ", 'USD', '" + generateRandomType() + "', '" +
                        generateRandomStatus() + "', " + generateInt(1, countries.size()) + ", " +
                        CREATED_BY_USER_ID + ", " + UPDATED_BY_USER_ID + ")")
                .collect(Collectors.joining(","));
        sqlStatement.append(orderStr);
        sqlStatement.append(',');

        LocalDateTime startDateSince2017 = LocalDateTime.of(2017, Month.JUNE, 1, 1, 1);
        orderStr = Stream.generate(() -> 0)
                .limit(generateInt(2000, 3000))
                .map(entry -> "('Order " + generateInt(1, 20000) + "', '" +
                        getCurrentLocalDateTimeStamp() + "', '" + getCurrentLocalDateTimeStamp() + "', '" +
                        generateRandomDate(startDateSince2017, null) + "', " +
                        generateRandomValue() + ", 'USD', '" + generateRandomType() + "', '" +
                        generateRandomStatus() + "', " + generateInt(1, countries.size()) + ", " +
                        CREATED_BY_USER_ID + ", " + UPDATED_BY_USER_ID + ")")
                .collect(Collectors.joining(","));
        sqlStatement.append(orderStr);
        sqlStatement.append(',');

        LocalDateTime startDateForLastSixMonths = LocalDateTime.now().minusMonths(6);
        orderStr = Stream.generate(() -> 0)
                .limit(generateInt(1000, 1200))
                .map(entry -> "('Order " + generateInt(1, 20000) + "', '" +
                        getCurrentLocalDateTimeStamp() + "', '" + getCurrentLocalDateTimeStamp() + "', '" +
                        generateRandomDate(startDateForLastSixMonths, null) + "', " +
                        generateRandomValue() + ", 'USD', '" + generateRandomType() + "', '" +
                        generateRandomStatus() + "', " + generateInt(1, countries.size()) + ", " +
                        CREATED_BY_USER_ID + ", " + UPDATED_BY_USER_ID + ")")
                .collect(Collectors.joining(","));
        sqlStatement.append(orderStr);
        sqlStatement.append(";");
    }

    private void appendUserActivities() {
        LocalDateTime startDateForLastFourYears = LocalDateTime.now().minusYears(4);
        sqlStatement.append("INSERT INTO user_activity (user_id, date, url) values ");
        String userActivitiesForFourYears = Stream.generate(() -> 1)
                .limit(1000)
                .map(entry -> "(" + generateInt(1, 4) + ", '" +
                        generateRandomDate(startDateForLastFourYears, null) + "', 'url " +
                        generateInt(1, 50) + "')")
                .collect(Collectors.joining(","));
        sqlStatement.append(userActivitiesForFourYears);
        sqlStatement.append(',');

        LocalDateTime startDateForLastTwoMonths = LocalDateTime.now().minusMonths(2);
        String userActivitiesForTwoMonths = Stream.generate(() -> 1)
                .limit(350)
                .map(entry -> "(" + generateInt(1, 4) + ", '" +
                        generateRandomDate(startDateForLastTwoMonths, null) + "', 'url " +
                        generateInt(1, 50) + "')")
                .collect(Collectors.joining(","));
        sqlStatement.append(userActivitiesForTwoMonths);
        sqlStatement.append(";");
    }

    private void appendTraffic() {
        sqlStatement.append("INSERT INTO traffic (date, value) values ");
        String trafficForLastFiveYears = Stream.generate(() -> 0)
                .limit(1000)
                .map(entry -> "('" + generateRandomDate(LocalDateTime.now().minusYears(5), null) + "', " +
                        generateInt(1, 100) + ")")
                .collect(Collectors.joining(","));
        sqlStatement.append(trafficForLastFiveYears);
        sqlStatement.append(',');
        String trafficForLastTwoMonths = Stream.generate(() -> 0)
                .limit(400)
                .map(entry -> "('" + generateRandomDate(LocalDateTime.now().minusMonths(2), null) + "', " +
                        generateInt(1, 100) + ")")
                .collect(Collectors.joining(","));
        sqlStatement.append(trafficForLastTwoMonths);
        sqlStatement.append(";");
    }

    @Autowired
    @EventListener(ContextStartedEvent.class)
    public void generateData(EntityManagerFactory emf) {
        appendCountries(countries);
        appendUserActivities();
        appendTraffic();
        appendOrders();
        doSqlQuery(emf.createEntityManager());
    }

    private void doSqlQuery(EntityManager entityManager) {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery(sqlStatement.toString()).executeUpdate();

        entityManager.getTransaction().commit();
    }

    private Map<String, String> getCountriesMap() {
        final String fileName = "countries.json";
        Map<String, String> map;
        try {

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() { };
            map = mapper.readValue(inputStream, typeRef);
        } catch (IOException e) {
            throw new JsonParseException("Can't parse file " + fileName, e);
        }
        return map;
    }
}
