package com.eztech.fitrans.util;

import com.eztech.fitrans.dto.response.TransactionTypeDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class DataUtils {

    private DataUtils() {
    }

    public static final String REGEX_NUMBER = "\\d+";

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean notNull(Object obj) {
        return !isNull(obj);
    }

    public static boolean nullOrEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    public static boolean nullOrEmpty(Collection objects) {
        return objects == null || objects.isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().equals("");
    }

    public static boolean isNullOrEmpty(final Object obj) {
        return obj == null || obj.toString().isEmpty();
    }

    public static boolean notNullOrEmpty(Collection<?> collection) {
        return !isNullOrEmpty(collection);
    }

    public static Integer parseToInt(String value) {
        return parseToInt(value, null);
    }

    public static Integer parseToInt(Object value) {
        String tmp = parseToString(value);
        if (isNull(tmp)) {
            return null;
        }
        return Integer.valueOf(tmp);
    }

    public static Integer parseToInt(Object value, Integer defaultValue) {
        String tmp = parseToString(value);
        if (isNull(tmp)) {
            return defaultValue;
        }
        return Integer.valueOf(tmp);
    }

    public static boolean notNullOrEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static String parseToString(Object value, String defaultVal) {
        return value != null ? String.valueOf(value) : defaultVal;
    }

    public static boolean nullOrZero(Long value) {
        return (value == null || value.equals(0L));
    }

    public static Character parseToCharacter(Object obj) {
        return (Character) obj;
    }

    public static String parseToString(Object obj) {
        if (isNull(obj)) {
            return null;
        }
        return String.valueOf(obj);
    }

    public static Double parseToDouble(Object obj) {
        if (isNull(obj)) {
            return null;
        }
        return Double.parseDouble(parseToString(obj));
    }

    public static BigInteger parseToBigInteger(Object obj) {
        if (isNull(obj)) {
            return null;
        }
        return new BigInteger(parseToString(obj));
    }

    public static Long parseToLong(Object obj) {
        if (isNull(obj)) {
            return null;
        }
        return Long.parseLong(parseToString(obj));
    }

    public static Short parseToShort(Object obj) {
        if (isNull(obj)) {
            return null;
        }
        return Short.parseShort(parseToString(obj));
    }

    public static Integer parseToInteger(Object obj) {
        if (isNull(obj)) {
            return null;
        }
        return Integer.parseInt(parseToString(obj));
    }

    public static LocalDate parseToLocalDate(Object obj) {
        if (isNull(obj)) {
            return null;
        }
        return LocalDate.parse(parseToString(obj));
    }

    public static LocalDateTime parseToLocalDateTime(Object obj) {
        if (isNull(obj)) {
            return null;
        }
        return LocalDateTime.parse(parseToString(obj));
    }

    public static LocalDate longToLocalDate(Long input) {
        if (isNull(input) || input <= 0L) {
            return null;
        }
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(input), ZoneId.systemDefault());
        return date.toLocalDate();
    }

    public static LocalDateTime parseToLocalDatetime(Object value) {
        if (value == null)
            return null;
        String tmp = parseToString(value, null);
        if (tmp == null)
            return null;

        try {
            LocalDateTime rtn = convertStringToLocalDateTime(tmp, "yyyy-MM-dd HH:mm:ss");
            return rtn;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    public static LocalDateTime longToLocalDateTime(Long input) {
        if (isNull(input) || input <= 0L) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(input), ZoneId.systemDefault());
    }

    public static Long localDateToLong(LocalDate input) {
        return input.toEpochDay();
    }

    public static Long localDateTimeToLong(LocalDateTime input) {
        ZonedDateTime zdt = input.atZone(ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    public static String objectToJson(Object data, String defaultValue) {
        if (isNull(data)) {
            return defaultValue;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(data);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
            return "";
        }
    }

    public static String objectToJson(Object data) {
        return objectToJson(data, "");
    }

    private static <T> T jsonToObjectFronGson(String jsonData, Class<T> classOutput) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData, classOutput);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    public static <T> T jsonToObject(String jsonData, Class<T> classOutput) {
        try {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false);
            return mapper.readValue(jsonData, classOutput);
        } catch (Exception ex) {
            return jsonToObjectFronGson(jsonData, classOutput);
        }
    }

    public static LocalDateTime convertStringToLocalDateTime(String value, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        if (value == null) {
            return null;
        } else if (value.contains(".")) {
            value = value.substring(0, value.indexOf('.'));
        }
        return LocalDateTime.parse(value, formatter);
    }

    public static String localDateToString(LocalDate value, String format) {
        if (!notNull(value)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return value.format(formatter); // "1986-04-08 12:30"
    }

    public static String localDateTimeToString(LocalDateTime value, String format) {
        if (!notNull(value)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return value.format(formatter); // "1986-04-08 12:30"
    }

    public static String formatIsdn(String msisdn) {
        if (msisdn.startsWith("0")) {
            return msisdn.substring(1);
        } else if (msisdn.startsWith("84") && msisdn.length() == 11) {
            return msisdn.substring(2);
        } else if (msisdn.startsWith("+84")) {
            return msisdn.substring(3);
        }
        return msisdn;
    }

    public static String formatMsisdn(String isdn) {
        if (isdn.startsWith("84") && isdn.length() >= 11) {
            return isdn;
        } else if (isdn.startsWith("+84")) {
            return isdn.substring(1);
        } else if (isdn.startsWith("0")) {
            isdn = isdn.substring(1);
        }
        return String.format("84%s", isdn);
    }

    public static boolean isInteger(Object obj) {
        return obj == parseToInteger(obj);
    }

    public static String randomNumberByDate() {
        String randomNumber = String.valueOf(System.nanoTime());
        if (randomNumber.startsWith("0")) {
            randomNumber = randomNumber.replaceFirst("0", "9");
        }
        return randomNumber;
    }

    @SuppressWarnings("java:S1612")
    public static <T> List<List<T>> nPartition(List<T> objs, final int N) {
        return new ArrayList<>(IntStream.range(0, objs.size()).boxed().collect(
                Collectors.groupingBy(e -> e % N, Collectors.mapping(e -> objs.get(e), Collectors.toList()))).values());
    }

    public static <T> List<List<T>> distribute(List<T> elements, int nrOfGroups) {
        if (CollectionUtils.isEmpty(elements)) {
            return new ArrayList<>(0);
        }
        int size = elements.size();
        if (nrOfGroups >= size) {
            List<List<T>> groups = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                groups.add(Arrays.asList(elements.get(i)));
            }
            return groups;
        }

        int elementsPerGroup = size / nrOfGroups;
        int leftoverElements = size % nrOfGroups;

        List<List<T>> groups = new ArrayList<>();
        for (int i = 0; i < nrOfGroups; i++) {
            groups.add(elements.subList(i * elementsPerGroup + Math.min(i, leftoverElements),
                    (i + 1) * elementsPerGroup + Math.min(i + 1, leftoverElements)));
        }
        return groups;
    }

    public static Integer incrRetry(Integer retry) {
        if (isNull(retry)) {
            return 1;
        }
        return retry + 1;
    }

    public static String checkNullInput(Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }

    public static Long convertToLong(Object object) {
        if (object == null) {
            return 0L;
        }
        return parseToLong(object);
    }

    public static Integer formatPartitionByTail(String msisdn, Integer tail) {
        if (msisdn == null) {
            return 0;
        }
        tail = (tail == null) ? 1 : tail;
        msisdn = msisdn.trim();

        if (!msisdn.matches(REGEX_NUMBER)) {
            return 0;
        }

        int length = msisdn.length();
        if (length < tail) {
            return 0;
        }

        return Integer.parseInt(msisdn.substring(length - tail));

    }

    public static boolean safeEqual(Object obj1, Object obj2) {
        return ((obj1 != null) && (obj2 != null) && obj2.toString().equals(obj1.toString()));
    }

    public static boolean safeEqualIgnoreCase(Object obj1, Object obj2) {
        return ((obj1 != null) && (obj2 != null) && obj2.toString().equalsIgnoreCase(obj1.toString()));
    }

    public static String htmlUnescape(String data) {
        if (data == null) {
            return null;
        }
        return HtmlUtils.htmlUnescape(data);
    }

    public static String formatCurrency(BigInteger number) {
        Locale loc = new Locale("vi", "VN");
        NumberFormat nf = NumberFormat.getCurrencyInstance(loc);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
        return nf.format(number).trim();
    }

    public static <T> List<T> jsonToList(String json, Class<T> classOutput) throws IOException {
        if (isNull(json)) {
            return Collections.emptyList();
        }
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        return objectMapper
                .readValue(json, typeFactory.constructCollectionType(List.class, classOutput));
    }

    public static LocalDate convertStringToLocalDate(String value, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        if (value == null || value.trim().isEmpty()) {
            return null;
        } else if (value.contains(".")) {
            value = value.substring(0, value.indexOf('.'));
        }
        return LocalDate.parse(value, formatter);
    }

    public static String localDateTimeToString(LocalDateTime value) {
        if (!notNull(value)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return value.format(formatter); // "1986-04-08 12:30"
    }

    public static void throwIf(boolean test, String message) throws Exception {
        if (test)
            throw new Exception(message);
    }

    public static void throwBusIf(boolean test, String message) throws Exception {
        if (test)
            throw new Exception(message);
    }

    public static void throwInputIf(boolean test, String message) throws Exception {
        if (test)
            throw new Exception(message);
    }

    public static boolean matchByPattern(String value, String regex) {
        if (nullOrEmpty(regex) || nullOrEmpty(value)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static String subString(String input, int lengh) {
        if (input == null) {
            return null;
        }
        if (lengh <= 0) {
            return input;
        }

        int strlengh = input.length();
        if (strlengh > lengh) {
            return input.substring(0, lengh);
        }
        return input;
    }

    public static String camelToSnake(String str) {
        // Regular Expression
        String regex = "([a-z])([A-Z]+)";

        // Replacement string
        String replacement = "$1_$2";

        // Replace the given regex
        // with replacement string
        // and convert it to lower case.
        str = str
                .replaceAll(
                        regex, replacement)
                .toLowerCase();

        // return string
        return str;
    }

    public static String convertDateToString(Date date, String format) {
        SimpleDateFormat sdf = null;
        try {
            sdf = new SimpleDateFormat(format);
            return date == null ? "" : sdf.format(date);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static boolean checkSqlOnlySelect(String sql) {
        sql = sql.toUpperCase();
        return sql.contains("INSERT") || sql.contains("UPDATE")
                || sql.contains("DELETE") || sql.contains("DROP")
                || sql.contains("CREATE TABLE") || sql.contains("ALTER TABLE");
    }

    public static String replaceAll(String input, String find, String replace) {
        if (DataUtils.notNull(input)) {
            return input.replaceAll(find, replace);
        }
        return null;
    }

    // template\import\File_mau_import_template.xlsx
    public static InputStream readInputStreamResource(String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        return classPathResource.getInputStream();
    }

    public static byte[] readFileResource(String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        return classPathResource.getInputStream().readAllBytes();

        // return
        // DataUtils.class.getClassLoader().getResourceAsStream(path).readAllBytes();
    }

    public static <T> T base64ToObject(String encodedString, Class<T> classOutput)
            throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8.name());

        return jsonToObject(decodedString, classOutput);
    }

    public static <T> T byteToObject(byte[] input, Class<T> classOutput) {
        String jsonData = new String(input, StandardCharsets.UTF_8);
        try {
            return DataUtils.jsonToObject(jsonData, classOutput);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    /**
     * @param obj1
     * @param defaultValue
     * @return
     * @author phuvk
     */
    public static int safeToInt(Object obj1, int defaultValue) {
        int result = defaultValue;
        if (obj1 != null) {
            try {
                result = Integer.parseInt(obj1.toString());
            } catch (Exception ignored) {
            }
        }

        return result;
    }

    /**
     * @param obj1 Object
     * @return int
     */
    public static int safeToInt(Object obj1) {
        return safeToInt(obj1, 0);
    }

    /**
     * @param obj1 Object
     * @return String
     */
    public static String safeToString(Object obj1, String defaultValue) {
        if (obj1 == null || safeEqual(obj1.toString(), "null")) {
            return defaultValue;
        }
        return obj1.toString();
    }

    /**
     * @param obj1 Object
     * @return String
     */
    public static String safeToString(Object obj1) {
        return safeToString(obj1, "");
    }

    /**
     * safe equal
     *
     * @param obj1 Long
     * @param obj2 Long
     * @return boolean
     */
    public static boolean safeEqual(Long obj1, Long obj2) {
        if (obj1 == obj2)
            return true;
        return ((obj1 != null) && (obj2 != null) && (obj1.compareTo(obj2) == 0));
    }

    /**
     * safe equal
     *
     * @param obj1 Long
     * @param obj2 Long
     * @return boolean
     */
    public static boolean safeEqual(BigInteger obj1, BigInteger obj2) {
        if (obj1 == obj2)
            return true;
        return (obj1 != null) && (obj2 != null) && obj1.equals(obj2);
    }

    /**
     * @param obj1
     * @param obj2
     * @return
     * @date 09-12-2015 17:43:20
     * @author TuyenLT18
     * @description
     */
    public static boolean safeEqual(Short obj1, Short obj2) {
        if (obj1 == obj2)
            return true;
        return ((obj1 != null) && (obj2 != null) && (obj1.compareTo(obj2) == 0));
    }

    /**
     * safe equal
     *
     * @param obj1 String
     * @param obj2 String
     * @return boolean
     */
    public static boolean safeEqual(String obj1, String obj2) {
        if (obj1 == null && obj2 == null)
            return true;
        else if (obj1 == obj2)
            return true;
        return ((obj1 != null) && (obj2 != null) && obj1.equals(obj2));
    }

    public static boolean isNullOrEmptyObj(Object obj) {
        if (obj == null)
            return true;
        CharSequence cs = ((String) obj);
        return isNullOrEmpty(cs);
    }

    /**
     * check null or empty
     * Su dung ma nguon cua thu vien StringUtils trong apache common lang
     *
     * @param cs String
     * @return boolean
     */
    public static boolean isNullOrEmpty(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNullOrEmpty(final Object[] collection) {
        return collection == null || collection.length == 0;
    }

    public static boolean isNullOrEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNullObject(Object obj1) {
        if (obj1 == null) {
            return true;
        }
        if (obj1 instanceof String) {
            return isNullOrEmpty(obj1.toString());
        }
        return false;
    }

    public static Long safeToLong(Object obj1, Long defaultValue) {
        Long result = defaultValue;
        if (obj1 != null) {
            if (obj1 instanceof BigDecimal) {
                return ((BigDecimal) obj1).longValue();
            }
            if (obj1 instanceof BigInteger) {
                return ((BigInteger) obj1).longValue();
            }
            try {
                result = Long.parseLong(obj1.toString());
            } catch (Exception ignored) {
            }
        }

        return result;
    }

    /**
     * @param obj1 Object
     * @return Long
     */
    public static Long safeToLong(Object obj1) {
        return safeToLong(obj1, 0L);
    }

    public static Double safeToDouble(Object obj1, Double defaultValue) {
        Double result = defaultValue;
        if (obj1 != null) {
            try {
                result = Double.parseDouble(obj1.toString());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return result;
    }

    public static boolean isNullOrZero(Long value) {
        return (value == null || value.equals(0L));
    }

    public static String getKeyParam(String key, Object... params) {
        String result = key;
        if (!isNullOrEmpty(params)) {
            for (int i = 0; i < params.length; i++) {
                result += "_" + String.valueOf(params[i]);
            }
        }
        return result;
    }

    public static boolean compareList(List<String> source, List<String> dest, int type) {
        switch (type) {
            /* EQUAL */
            case 0:
                if (source.size() == dest.size()) {
                    for (String s : source) {
                        if (!dest.contains(s)) {
                            return false;
                        }
                    }

                    for (String d : dest) {
                        if (!source.contains(d)) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }

                break;
            /* IN */
            case 1:
                if (source.size() <= dest.size()) {
                    for (String s : source) {
                        if (!dest.contains(s)) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }

                break;
            /* CONTAIN */
            case 2:
                if (source.size() >= dest.size()) {
                    for (String d : dest) {
                        if (!source.contains(d)) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }

                break;
        }

        return true;
    }

    public static LocalDateTime processDate(LocalDateTime from, LocalDateTime to, LocalDateTime compare,
            LocalDateTime timeReceived) {
        long years = ChronoUnit.YEARS.between(from, to);
        long months = ChronoUnit.MONTHS.between(from, to);
        long days = ChronoUnit.DAYS.between(from, to);
        long hours = ChronoUnit.HOURS.between(from, to);
        long minutes = ChronoUnit.MINUTES.between(from, to);
        long seconds = ChronoUnit.SECONDS.between(from, to);
        // LocalDateTime realReceived = LocalDateTime.now();
        LocalDateTime processDate = null;
        boolean isAfter = timeReceived.isAfter(compare);
        if (!isAfter) {
            // set processDate = real time received of profile processing/first record
            timeReceived = compare;
        }

        if (years > 0) {
            processDate = timeReceived.plusYears(years);
        }
        if (months > 0) {
            processDate = timeReceived.plusMonths(months);
        }
        if (days > 0) {
            processDate = timeReceived.plusDays(days);
        }
        if (hours > 0) {
            processDate = timeReceived.plusHours(hours);
        }
        if (minutes > 0) {
            processDate = timeReceived.plusMinutes(minutes);
        }
        if (seconds > 0) {
            processDate = timeReceived.plusSeconds(seconds);
        }

        return processDate;

    }

    // timeWorked = thời gian đã làm -> dùng để tính đối với hoàn trả hồ sơ (trường
    // này được lưu là additional time)
    // timeReceivedOfPreviousRecord = thời gian xử lý của bản ghi trước đó
    // hàm này thay cho hàm calculatingDate
    public static Map<String, Object> calculatingDateFromTimeReceived(LocalDateTime timeReceived, Integer standardTime,
            Integer timeChecker, Integer timeWorked, Integer numberOfPO, Integer numberOfBill,
            Integer transactionType) {

        Map<String, Object> mapResult = new HashMap<>();
        LocalDateTime timeReceivedNew = null;
        int processTimeCase = 0;

        int standard = (!DataUtils.isNullOrEmpty(standardTime)) ? standardTime : 0;
        int checker = (!DataUtils.isNullOrEmpty(timeChecker)) ? timeChecker : 0;
        int worked = (!DataUtils.isNullOrEmpty(timeWorked)) ? timeWorked : 0;

        int PO = (!DataUtils.isNullOrEmpty(numberOfPO)) ? numberOfPO : 0;

        int bill = (!DataUtils.isNullOrEmpty(numberOfBill)) ? numberOfBill : 0;

        int type = (!DataUtils.isNullOrEmpty(transactionType)) ? transactionType : 0;

        LocalDateTime timeMarker17h = LocalDateTime.of(timeReceived.getYear(), timeReceived.getMonth(),
                timeReceived.getDayOfMonth(), 17, 0);

        LocalDateTime timeMarkerConfig = LocalDateTime.of(timeReceived.getYear(), timeReceived.getMonth(),
                timeReceived.getDayOfMonth(), 17, 0);

        LocalDateTime timeMarker13h30 = LocalDateTime.of(timeReceived.getYear(), timeReceived.getMonth(),
                timeReceived.getDayOfMonth(), 13, 30);
        LocalDateTime timeMarker11h30 = LocalDateTime.of(timeReceived.getYear(), timeReceived.getMonth(),
                timeReceived.getDayOfMonth(), 11, 30);

        LocalDateTime tomorrow = timeReceived.plusDays(1);
        LocalDateTime timeMarkerTomorrow = LocalDateTime.of(tomorrow.getYear(), tomorrow.getMonth(),
                tomorrow.getDayOfMonth(), 8, 0);

        int additionalTime = 0;
        // checking transaction type and plusing additional time
        switch (type) {
            case 1:
                if (PO >= 2) {
                    additionalTime = additionalTime + 5 * PO;
                }

                if (bill >= 2) {
                    additionalTime = additionalTime + 1 * bill;
                }
                break;
            case 2:
                break;
            default:
                break;
        }

        // thời gian này đã bao gồm cả giờ nghỉ chưa
        // do đó cần tính lại
        LocalDateTime processTime = timeReceived.plusMinutes(standard + checker + additionalTime - worked);

        // thời gian nhận trước 11h30
        // thời gian xử lý: trước 11h30, 11h30-13h30, 13h30-17h, sau 17h
        if (timeReceived.isBefore(timeMarker11h30)) {
            // thời gian nhận mới bằng thời gian nhận cũ
            timeReceivedNew = timeReceived;

            // thời gian xử lý trước 11h30
            // không cần tính lại vì trước 11h30
            if (processTime.isBefore(timeMarker11h30)) {
                processTimeCase = 1;
            }
            // thời gian xử lý 11h30-13h30
            // thời gian xử lý tính lại từ mốc 13h30
            if (processTime.isAfter(timeMarker11h30) && processTime.isBefore(timeMarker13h30)) {
                processTimeCase = 2;
            }
            // thời gian xử lý 13h30 - 17h
            // tính lại vì sẽ loại bỏ giờ nghỉ trưa
            if (processTime.isAfter(timeMarker13h30) && processTime.isBefore(timeMarker17h)) {
                processTimeCase = 3;
            }
            // thời gian xử lý sau 17h
            if (processTime.isAfter(timeMarker17h)) {
                processTimeCase = 4;
            }

            processTime = calculatingTimeProcess(processTime, timeReceived, timeMarker11h30, timeMarker13h30,
                    timeMarker17h, timeMarkerTomorrow, processTimeCase, standard, checker, additionalTime, worked);

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);

        }

        // thời gian nhận từ 11h30 - 13h30
        // thời gian xử lý: 11h30 - 13h30, 13h30 - 17h, sau 17h
        if (timeReceived.isAfter(timeMarker11h30) && timeReceived.isBefore(timeMarker13h30)) {
            // thời gian nhận lấy mốc 13h30
            timeReceivedNew = timeMarker13h30;

            // thời gian xử lý 11h30-13h30
            // thời gian xử lý tính lại từ mốc 13h30
            if (processTime.isAfter(timeMarker11h30) && processTime.isBefore(timeMarker13h30)) {
                processTimeCase = 2;

            }

            // thời gian xử lý 13h30 - 17h
            // tính lại vì sẽ loại bỏ giờ nghỉ trưa
            if (processTime.isAfter(timeMarker13h30) && processTime.isBefore(timeMarker17h)) {
                processTimeCase = 3;
            }

            // thời gian xử lý sau 17h
            if (processTime.isAfter(timeMarker17h)) {
                processTimeCase = 4;
            }

            processTime = calculatingTimeProcess(processTime, timeReceived, timeMarker11h30, timeMarker13h30,
                    timeMarker17h, timeMarkerTomorrow, processTimeCase, standard, checker, additionalTime, worked);

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);
        }

        // thời gian nhận từ 13h30 - 17h
        // thời gian xử lý: 13h30 - 17h, sau 17h
        if (timeReceived.isAfter(timeMarker13h30) && timeReceived.isBefore(timeMarker17h)) {
            // thời gian nhận mới bằng thời gian nhận cũ
            timeReceivedNew = timeReceived;

            // thời gian xử lý từ 13h30 - 17h
            // không cần tính lại
            if (processTime.isAfter(timeMarker13h30) && processTime.isBefore(timeMarker17h)) {
                processTimeCase = 3;
            }

            // thời gian xử lý sau 17h
            if (processTime.isAfter(timeMarker17h)) {
                // thời gian từ 17h - thời gian xử lý => ngoài giờ hành chính
                processTimeCase = 4;

            }

            processTime = calculatingTimeProcess(processTime, timeReceived, timeMarker11h30, timeMarker13h30,
                    timeMarker17h, timeMarkerTomorrow, processTimeCase, standard, checker, additionalTime, worked);

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);

        }

        // thời gian nhận sau 17h

        if (timeReceived.isAfter(timeMarker17h)) {
            // thời gian nhận mới bằng mốc 8h sáng hôm sau
            timeReceivedNew = timeMarkerTomorrow;

            // thời gian xử lý tính sang ngày hôm sau
            processTime = timeMarkerTomorrow
                    .plusMinutes(standard + checker + additionalTime - worked);

            mapResult.put("processTime", processTime);
            mapResult.put("timeReceived", timeReceivedNew);
        }

        return mapResult;

    }

    public static LocalDateTime calculatingTimeProcess(LocalDateTime processTime, LocalDateTime timeReceived,
            LocalDateTime timeMarker11h30,
            LocalDateTime timeMarker13h30, LocalDateTime timeMarker17h, LocalDateTime timeMarkerTomorrow,
            int processTimeCase, int standard, int checker, int additionalTime, int worked) {

        switch (processTimeCase) {
            // thời gian xử lý trước 11h30
            case 1:

                break;

            // thời gian xử lý từ 11h30 - 13h30
            case 2:
                // thời gian xử lý 11h30-13h30
                // thời gian xử lý tính lại từ mốc 13h30
                // thời gian chênh lệch giữa thời gian nhận và mốc 11h30 => thời gian làm
                if (timeReceived.isBefore(timeMarker11h30)) {
                    Long time1 = durationToMinute(timeReceived, timeMarker11h30);
                    // thời gian xử lý = thời gian xử lý cũ - thời gian đã làm
                    processTime = timeMarker13h30.plusMinutes(standard + checker + additionalTime - worked - time1);

                }
                if (timeReceived.isAfter(timeMarker11h30) && timeReceived.isBefore(timeMarker13h30)) {
                    processTime = timeMarker13h30.plusMinutes(standard + checker + additionalTime - worked);

                }

                // tính lại thời gian xử lý nếu quá 17h
                if (processTime.isAfter(timeMarker17h)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 17h => thời gian ngoài giờ
                    Long time4 = durationToMinute(timeMarker17h, processTime);
                    processTime = timeMarkerTomorrow.plusMinutes(time4);

                    // thời gian xử lý 11h30-13h30 ngày hôm sau
                    LocalDateTime timeMarker11h30Tomorrow = timeMarker11h30.plusDays(1);
                    LocalDateTime timeMarker13h30Tomorrow = timeMarker13h30.plusDays(1);
                    LocalDateTime timeMarker17hTomorrow = timeMarker17h.plusDays(1);

                    if (processTime.isAfter(timeMarker11h30Tomorrow) && processTime.isBefore(timeMarker13h30Tomorrow)) {
                        Long time5 = durationToMinute(processTime, timeMarker11h30Tomorrow);
                        processTime = timeMarker13h30Tomorrow.plusMinutes(time5);
                    }

                    if (processTime.isAfter(timeMarker17hTomorrow)) {
                        Long time5 = durationToMinute(timeMarker17hTomorrow, processTime);
                        LocalDateTime tomorrow2 = timeMarker17hTomorrow.plusDays(1);
                        LocalDateTime timeMarkerTomorrow2 = LocalDateTime.of(tomorrow2.getYear(), tomorrow2.getMonth(),
                                tomorrow2.getDayOfMonth(), 8, 0);
                        processTime = timeMarkerTomorrow2.plusMinutes(time5);
                    }
                }

                break;

            // thời gian xử lý từ 13h30 - 17h
            case 3:
                // thời gian còn lại = thời gian xử lý - thời gian đã làm
                if (timeReceived.isBefore(timeMarker11h30)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 11h30 => thời gian làm
                    Long time1 = durationToMinute(timeReceived, timeMarker11h30);
                    // // thời gian nghỉ trưa : 11h30 - 13h30 => thời gian không làm
                    // Long time2 = durationToMinute(timeMarker11h30, timeMarker13h30); // không cần
                    // tính thời gian nghỉ chưa
                    processTime = timeMarker13h30
                            .plusMinutes(standard + checker + additionalTime - worked - time1);

                }
                if (timeReceived.isAfter(timeMarker11h30) && timeReceived.isBefore(timeMarker13h30)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 11h30 => thời gian làm
                    // Long time1 = durationToMinute(timeReceived, timeMarker11h30);
                    // thời gian nghỉ trưa : 11h30 - 13h30 => thời gian không làm
                    // Long time2 = durationToMinute(timeMarker11h30, timeMarker13h30); => không cần
                    // tính, chỉ cần tính thời gian đã làm
                    processTime = timeMarker13h30
                            .plusMinutes(standard + checker + additionalTime - worked);

                }
                if (timeReceived.isAfter(timeMarker13h30) && timeReceived.isBefore(timeMarker17h)) {

                }

                // tính lại thời gian xử lý nếu quá 17h
                if (processTime.isAfter(timeMarker17h)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 17h
                    Long time4 = durationToMinute(timeMarker17h, processTime);
                    processTime = timeMarkerTomorrow.plusMinutes(time4);

                    // thời gian xử lý 11h30-13h30 ngày hôm sau
                    LocalDateTime timeMarker11h30Tomorrow = timeMarker11h30.plusDays(1);
                    LocalDateTime timeMarker13h30Tomorrow = timeMarker13h30.plusDays(1);
                    LocalDateTime timeMarker17hTomorrow = timeMarker17h.plusDays(1);

                    if (processTime.isAfter(timeMarker11h30Tomorrow) && processTime.isBefore(timeMarker13h30Tomorrow)) {
                        Long time5 = durationToMinute(processTime, timeMarker11h30Tomorrow);
                        processTime = timeMarker13h30Tomorrow.plusMinutes(time5);
                    }

                    if (processTime.isAfter(timeMarker17hTomorrow)) {
                        Long time5 = durationToMinute(timeMarker17hTomorrow, processTime);
                        LocalDateTime tomorrow2 = timeMarker17hTomorrow.plusDays(1);
                        LocalDateTime timeMarkerTomorrow2 = LocalDateTime.of(tomorrow2.getYear(), tomorrow2.getMonth(),
                                tomorrow2.getDayOfMonth(), 8, 0);
                        processTime = timeMarkerTomorrow2.plusMinutes(time5);
                    }
                }

                break;

            // thời gian xử lý sau 17h
            case 4:

                if (timeReceived.isBefore(timeMarker11h30)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 11h30
                    Long time1 = durationToMinute(timeReceived, timeMarker11h30); // thời gian đã làm
                    // thời gian nghỉ trưa : 11h30 - 13h30
                    // Long time2 = durationToMinute(timeMarker11h30, timeMarker13h30); // => không
                    // cần tính chỉ cần tính thời gian đã làm
                    // thời gian đã làm : 13h30 - 17
                    Long time2 = durationToMinute(timeMarker13h30, timeMarker17h); // thời gian đã làm
                    // thời gian từ 17h - thời gian xử lý
                    // Long time3 = durationToMinute(timeMarker17h, processTime); // thời gian ngoài
                    // giờ hành chính, tính cho sáng hôm sau nếu có

                    // thời gian xử lý = thời gian xử lý cũ - thời gian đã làm
                    processTime = timeMarkerTomorrow
                            .plusMinutes(standard + checker + additionalTime - worked - time1 - time2);
                }

                if (timeReceived.isAfter(timeMarker11h30) && timeReceived.isBefore(timeMarker13h30)) {
                    // thời gian chênh lệch giữa thời gian nhận và mốc 11h30
                    // Long time1 = durationToMinute(timeReceived, timeMarker13h30); // Trả về 0 nếu
                    // timeReceived >
                    // // timeMarker11h30
                    // thời gian 13h30 - 17h
                    Long time2 = durationToMinute(timeMarker13h30, timeMarker17h); // thời gian đã làm
                    // thời gian từ 17h - thời gian xử lý
                    // Long time3 = durationToMinute(timeMarker17h, processTime); // thời gian ngoài
                    // giờ hành chính

                    // thời gian xử lý = thời gian xử lý cũ - thời gian đã làm
                    processTime = timeMarkerTomorrow
                            .plusMinutes(standard + checker + additionalTime - worked - time2);

                }
                if (timeReceived.isAfter(timeMarker13h30) && timeReceived.isBefore(timeMarker17h)) {
                    // thời gian từ 17h - thời gian xử lý => ngoài giờ hành chính
                    Long time3 = durationToMinute(timeMarker17h, processTime);

                    processTime = timeMarkerTomorrow
                            .plusMinutes(time3);

                }
                if (timeReceived.isAfter(timeMarker17h)) {
                    // thời gian xử lý tính sang ngày hôm sau
                    processTime = timeMarkerTomorrow
                            .plusMinutes(standard + checker + additionalTime - worked);

                }

                // thời gian xử lý 11h30-13h30 ngày hôm sau
                LocalDateTime timeMarker11h30Tomorrow = timeMarker11h30.plusDays(1);
                LocalDateTime timeMarker13h30Tomorrow = timeMarker13h30.plusDays(1);
                LocalDateTime timeMarker17hTomorrow = timeMarker17h.plusDays(1);

                if (processTime.isAfter(timeMarker11h30Tomorrow) && processTime.isBefore(timeMarker13h30Tomorrow)) {
                    Long time5 = durationToMinute(timeMarker11h30Tomorrow, processTime);
                    processTime = timeMarker13h30Tomorrow.plusMinutes(time5);
                }

                if (processTime.isAfter(timeMarker17hTomorrow)) {
                    Long time5 = durationToMinute(timeMarker17hTomorrow, processTime);
                    LocalDateTime tomorrow2 = timeMarker17hTomorrow.plusDays(1);
                    LocalDateTime timeMarkerTomorrow2 = LocalDateTime.of(tomorrow2.getYear(), tomorrow2.getMonth(),
                            tomorrow2.getDayOfMonth(), 8, 0);
                    processTime = timeMarkerTomorrow2.plusMinutes(time5);
                }

                break;

            default:
                processTime = timeMarkerTomorrow
                        .plusMinutes(standard + checker + additionalTime - worked);
                break;
        }

        return processTime;
    }

    public static LocalDateTime checkTime(LocalDateTime processTime, int marked, int markedMinutes, int standardTime,
            int timeChecker,
            int additionalTime, LocalDateTime timeReceived) {
        if (processTime.getHour() > marked
                || (processTime.getHour() == marked && processTime.getMinute() > markedMinutes)) {

            // int year = LocalDate.now().getYear();
            // ;
            // int month = LocalDate.now().getMonthValue();
            // int day = LocalDate.now().getDayOfMonth();

            int year = timeReceived.getYear();
            int month = timeReceived.getMonthValue();
            int day = timeReceived.getDayOfMonth();

            // thời gian giữa nhận và xử lý
            // thời gian nhận trước thời gian xử lý => kết quả > 0 và ngược lại = 0
            Long duration = durationToMinute(timeReceived, processTime);
            // int minutes = standardTime
            // + timeChecker + additionalTime;

            // đã có thời gian nhận và xử lý nên ko cần cộng thêm thời gian checker,
            // additionalTime, standardTime
            // vì thời gian xử lý đã được tính trước đó
            int minutes = 0;
            int hour = 0;

            // thơi gian nhận trước 17h
            // thời gian xử lý truyền vào là sau 17h
            if (marked == 17) {
                // tính lại thời gian năm , tháng, ngày
                LocalDateTime tomorrow = timeReceived.plusDays(1);
                year = tomorrow.getYear();
                month = tomorrow.getMonthValue();
                day = tomorrow.getDayOfMonth();

                LocalDateTime timeMarker17h = LocalDateTime.of(timeReceived.getYear(), timeReceived.getMonthValue(),
                        timeReceived.getDayOfMonth(), 17, 0);
                LocalDateTime timeMarker13h30 = LocalDateTime.of(timeReceived.getYear(), timeReceived.getMonthValue(),
                        timeReceived.getDayOfMonth(), 13, 30);
                LocalDateTime timeMarker11h30 = LocalDateTime.of(timeReceived.getYear(), timeReceived.getMonthValue(),
                        timeReceived.getDayOfMonth(), 11, 30);
                boolean isAfter = timeReceived.isAfter(timeMarker13h30);
                boolean isBefore = timeReceived.isBefore(timeMarker17h);
                boolean isBefore11h30 = timeReceived.isBefore(timeMarker11h30);
                // tính thời gian giữa giờ nghỉ 17h đến thời gian nhận
                Long durationWithTimeReceived = 0L;

                // thời gian nhận trước 11h30
                if (isBefore11h30) {
                    durationWithTimeReceived = durationToMinute(
                            timeMarker17h, processTime);
                    // thời gian nhận đến 11h30
                    Long time = durationToMinute(timeReceived, timeMarker11h30);
                    // mốc thời gian + thời gian xử lý tính từ mốc
                    minutes = minutes + time.intValue() + durationWithTimeReceived.intValue();
                }

                // thời gian nhận từ 11h30 - 13h30
                // xử lý tính từ 13h30
                if (!isBefore11h30 && !isAfter) {
                    durationWithTimeReceived = durationToMinute(
                            timeMarker13h30, timeMarker17h);
                    // tổng thời gian xử lý - thời gian tính từ mốc 13h30 - 17h
                    minutes = minutes + duration.intValue() - durationWithTimeReceived.intValue();
                }

                // thời gian nhận từ 13h30 - 17h
                if (isAfter && isBefore) {
                    durationWithTimeReceived = durationToMinute(
                            timeMarker17h, processTime);
                    // thời gian còn lại sẽ + thêm thời gian xử lý còn lại
                    minutes = minutes + (duration.intValue() - durationWithTimeReceived.intValue());
                }

                // thời gian nhận sau 17h
                if (isAfter && !isBefore) {
                    duration = 0L;
                    durationWithTimeReceived = 0L;
                    minutes = minutes + duration.intValue() + durationWithTimeReceived.intValue();
                }

                // minutes = minutes + (duration.intValue() -
                // durationWithTimeReceived.intValue());
                if (minutes > 60) {
                    hour = 8 + minutes / 60;
                    minutes = minutes % 60;
                } else {
                    hour = 8;
                }
            }

            // thời gian nhận trước 11h30
            if (marked == 11) {
                // year = LocalDate.now().getYear();
                // month = LocalDate.now().getMonthValue();
                // day = LocalDate.now().getDayOfMonth();
                LocalDateTime timeMarker = LocalDateTime.of(year, month, day, 11, 30);
                boolean isBefore = timeReceived.isBefore(timeMarker);
                // tính thời gian giữa giờ nghỉ 11h30 đến thời gian nhận
                Long durationWithTimeReceived = null;
                if (isBefore) {
                    durationWithTimeReceived = durationToMinute(timeReceived,
                            LocalDateTime.of(year, month, day, 11, 30));
                }
                // nếu sau ko tính duration
                else {
                    duration = 0L;
                    durationWithTimeReceived = 0L;
                }
                // thời gian còn lại sẽ + thêm thời gian xử lý còn lại
                minutes = minutes + (duration.intValue() - durationWithTimeReceived.intValue());
                if (minutes > 60) {
                    hour = 13 + minutes / 60;
                    minutes = minutes % 60;
                } else {
                    hour = 13;
                }
            }

            // thời gian nhận trong khoảng 11h30 - 13h30
            // cộng thêm duration giữa thời gian xử lý và thời gian bàn giao
            if (marked == 13) {
                // year = LocalDate.now().getYear();
                // month = LocalDate.now().getMonthValue();
                // day = LocalDate.now().getDayOfMonth();
                // LocalDateTime timeMarker = LocalDateTime.of(year, month, day, 13, 30);
                // boolean isBefore = timeMarker.isBefore(timeReceived);
                // // nhận trước 13h30 và sau 11h30 thì duration = 0;
                // if(isBefore) {
                // duration = 0L;
                // }
                minutes = minutes + 30 + duration.intValue();
                if (minutes > 60) {
                    hour = 13 + (minutes + 30) / 60;
                    minutes = minutes % 60;
                } else {
                    hour = 13;
                }
            }

            // set proces time = tomorrow
            processTime = LocalDateTime.of(year, month, day, hour, minutes);

        }
        return processTime;
    }

    public static Long durationToMinute(LocalDateTime from, LocalDateTime to) {

        long minutes = ChronoUnit.MINUTES.between(from, to);
        // long seconds = ChronoUnit.SECONDS.between(from, to);
        long minutesProcess = 0;

        if (minutes > 0) {
            minutesProcess = minutesProcess + minutes;
        }

        return Long.valueOf(minutesProcess);
    }

    // public static

}
