package com.eztech.fitrans.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.ProfileListDTO;
import com.eztech.fitrans.dto.response.QRCodeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.commons.io.FileUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import static com.eztech.fitrans.util.DataUtils.readInputStreamResource;
import static com.eztech.fitrans.util.ExcelFileReader.readExcelFromResource;

@Slf4j
@Component
public class ReadAndWriteDoc {

    private static Logger logger = LoggerFactory.getLogger(ReadAndWriteDoc.class);
    private static final BaseMapper<ProfileDTO, QRCodeDTO> mapper = new BaseMapper<>(ProfileDTO.class, QRCodeDTO.class);

    public File ExportDocFile(ProfileDTO profile, String username, Map<String, ProfileListDTO> mapParams) {

        try {

            // InputStream resource =
            // getClass().getClassLoader().getResourceAsStream("template/BIDV_Template.docx");
            InputStream resource = readInputStreamResource("template/BIDV_Template.docx");

            File file = File.createTempFile("BIDV_Template_", ".docx");
            FileUtils.copyInputStreamToFile(resource, file);
            String folder = "C:\\BIDV_BBBG\\";
            String filename = "BBBG_" + username + "_" + Timestamp.valueOf(LocalDateTime.now()).getTime() + ".docx";
            File outputFile = new File(folder + filename);
            if (file == null) {
                throw new IllegalArgumentException("file not found");
            } else {

                try (FileInputStream inpuStream = new FileInputStream(file)) {
                    XWPFDocument docOrigin = new XWPFDocument(inpuStream);
                    XWPFDocument docDes = new XWPFDocument();
                    // 4 table in template (0-3)
                    int i = 0;
                    for (IBodyElement bodyElement : docOrigin.getBodyElements()) {

                        BodyElementType elementType = bodyElement.getElementType();

                        if (elementType == BodyElementType.TABLE) {

                            XWPFTable table = (XWPFTable) bodyElement;
                            if (i == 1) {

                                XWPFTableRow row1 = table.getRow(0);
                                XWPFTableCell cell1 = row1.getCell(1);
                                XWPFParagraph para1 = cell1.getParagraphs().get(0);
                                para1.setAlignment(ParagraphAlignment.CENTER);
                                XWPFRun run = para1.createRun();
                                String staffName = (!DataUtils.isNullOrEmpty(profile.getStaffName()))
                                        ? profile.getStaffName().toString()
                                        : "";
                                run.setText(staffName);
                                run.setFontSize(12);

                                // check transactionType : 1,2,3
                                XWPFTableRow row2 = table.getRow(1);
                                XWPFTableCell cell2 = row2.getCell(1);
                                XWPFParagraph para2 = cell2.getParagraphs().get(0);
                                para2.setAlignment(ParagraphAlignment.CENTER);
                                XWPFRun run2 = para2.createRun();
                                run2.setFontSize(12);

                                if (profile.getTransactionType().equals(1) | profile.getTransactionType().equals(2)) {
                                    if (!DataUtils.isNullOrEmpty(profile.getStaffNameCM())) {
                                        run2.setText(profile.getStaffNameCM().toString());
                                    }
                                } else {
                                    if (!DataUtils.isNullOrEmpty(profile.getStaffNameCM())) {
                                        run2.setText(profile.getStaffNameCT().toString());
                                    }
                                }
                                cell2.setVerticalAlignment(XWPFVertAlign.CENTER);

                                // row 3
                                if (!DataUtils.isNullOrEmpty(profile.getCif())) {
                                    XWPFTableRow row3 = table.getRow(2);
                                    XWPFTableCell cell3 = row3.getCell(1);
                                    XWPFParagraph para3 = cell3.getParagraphs().get(0);
                                    para3.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run3 = para3.createRun();
                                    run3.setText(profile.getCif());
                                    run3.setFontSize(12);
                                }

                                // row 4
                                if (!DataUtils.isNullOrEmpty(profile.getCustomerName())) {
                                    XWPFTableRow row4 = table.getRow(3);
                                    XWPFTableCell cell4 = row4.getCell(1);
                                    XWPFParagraph para4 = cell4.getParagraphs().get(0);
                                    para4.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run4 = para4.createRun();
                                    run4.setText(profile.getCustomerName());
                                    run4.setFontSize(12);
                                }

                                // row 5
                                if (!DataUtils.isNullOrEmpty(profile.getTypeEnum())) {
                                    XWPFTableRow row5 = table.getRow(4);
                                    XWPFTableCell cell5 = row5.getCell(1);
                                    XWPFParagraph para5 = cell5.getParagraphs().get(0);
                                    para5.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run5 = para5.createRun();
                                    run5.setText(profile.getTypeEnum());
                                    run5.setFontSize(12);
                                }

                                // row 6
                                if (!DataUtils.isNullOrEmpty(profile.getDescription())) {
                                    XWPFTableRow row6 = table.getRow(5);
                                    XWPFTableCell cell6 = row6.getCell(1);
                                    XWPFParagraph para6 = cell6.getParagraphs().get(0);
                                    para6.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run6 = para6.createRun();
                                    run6.setText(profile.getDescription());
                                    run6.setFontSize(12);
                                }

                                // row 7
                                if (!DataUtils.isNullOrEmpty(profile.getValue())) {
                                    XWPFTableRow row7 = table.getRow(6);
                                    XWPFTableCell cell7 = row7.getCell(1);
                                    XWPFParagraph para7 = cell7.getParagraphs().get(0);
                                    para7.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run7 = para7.createRun();
                                    Locale vi = new Locale("vi", "VN");
                                    NumberFormat vietnamFormat = NumberFormat.getCurrencyInstance(vi);
                                    String currency = !DataUtils.isNullOrEmpty(profile.getCurrency())
                                            ? profile.getCurrency().toString()
                                            : "";
                                    run7.setText(vietnamFormat.format(profile.getValue())
                                            .replace(vietnamFormat.getCurrency().getSymbol(), "") + " - " + currency);
                                    run7.setFontSize(12);
                                }

                            }
                            if (i == 2) {
                                ArrayList<String> categories = new ArrayList<>();
                                // ArrayList<String> categories =
                                // convertStringToArray(profile.getCategoryProfile());
                                if (!DataUtils.isNullOrEmpty(profile.getCategoryProfile())) {
                                    categories = convertStringToArray(profile.getCategoryProfile());
                                }
                                if (!DataUtils.isNullOrEmpty(profile.getOthersProfile())) {
                                    categories.add(Integer.valueOf(categories.size() + 1).toString());
                                }
                                if (categories.size() > 0) {
                                    // ArrayList<String> categories =
                                    // convertStringToArray(profile.getCategoryProfile());

                                    // if (!DataUtils.isNullOrEmpty(profile.getOthersProfile())) {
                                    // categories.add(Integer.valueOf(categories.size() + 1).toString());
                                    // }
                                    XWPFTableRow row2 = table.getRows().get(2);
                                    XWPFTableCell cell2 = row2.getCell(1);
                                    XWPFParagraph para = cell2.getParagraphs().get(0);
                                    para.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run = para.createRun();
                                    String type = (!DataUtils.isNullOrEmpty(profile.getTypeEnum()))
                                            ? profile.getTypeEnum()
                                            : "";
                                    run.setText(type);
                                    run.setFontSize(12);
                                    // cell2.setText(profile.getTypeEnum());
                                    XWPFTableRow oldRow = table.getRows().get(3);
                                    CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());

                                    for (int index = 0; index < categories.size(); index++) {
                                        if (index == 0) {
                                            XWPFTableRow row1 = table.getRows().get(3);
                                            XWPFTableCell sttCell = row1.getCell(0);
                                            XWPFTableCell cell = row1.getCell(1);
                                            XWPFParagraph para1 = sttCell.getParagraphs().get(0);
                                            XWPFParagraph para2 = cell.getParagraphs().get(0);
                                            para1.setAlignment(ParagraphAlignment.CENTER);
                                            XWPFRun run1 = para1.createRun();
                                            run1.setText(Integer.valueOf(index + 1).toString());
                                            run1.setFontSize(12);
                                            // for other categories
                                            String cat = mapParams.get(categories.get(index)).type;
                                            String str = (!DataUtils.isNullOrEmpty(cat)) ? cat : categories.get(index);
                                            XWPFRun run2 = para2.createRun();
                                            run2.setText(str);
                                            run2.setFontSize(12);
                                        } else {
                                            XWPFTableRow row = new XWPFTableRow(ctrow, table);
                                            XWPFTableCell sttCell = row.getCell(0);
                                            XWPFTableCell cell = row.getCell(1);
                                            sttCell.removeParagraph(0);
                                            cell.removeParagraph(0);
                                            XWPFParagraph para1 = sttCell.addParagraph();
                                            XWPFParagraph para2 = cell.addParagraph();
                                            para1.setAlignment(ParagraphAlignment.CENTER);
                                            XWPFRun run1 = para1.createRun();
                                            run1.setText(Integer.valueOf(index + 1).toString());
                                            run1.setFontSize(12);
                                            // for other categories
                                            String cat = mapParams.get(categories.get(index)).type;
                                            String str = (!DataUtils.isNullOrEmpty(cat)) ? cat : categories.get(index);
                                            XWPFRun run2 = para2.createRun();
                                            run2.setText(str);
                                            run2.setFontSize(12);
                                            table.addRow(row);
                                        }
                                    }
                                }
                            }

                            CopyStyle(docOrigin, docDes, docOrigin.getStyles().getStyle(table.getStyleID()));

                            docDes.createTable();

                            docDes.setTable(i, table);

                            if (i != 0 || i != 1) {
                                XWPFParagraph paragraphTemp = docDes.createParagraph();
                                XWPFRun runTemp = paragraphTemp.createRun();
                                runTemp.addBreak();
                                if (i == 3) {
                                    runTemp.addBreak();
                                }
                            }
                            // add image in new doc => not copy

                            if (i == 0) {
                                if (profile != null) {

                                    // add ngày tháng năm
                                    LocalDateTime date = LocalDateTime.now();

                                    String dmy = "Hà Nội, ngày " + date.getDayOfMonth() + " tháng "
                                            + date.getMonthValue() + " năm " + date.getYear();
                                    // XWPFTableRow row2 = table.getRow(1);
                                    // XWPFTableCell cell2 = row2.getCell(1);
                                    XWPFParagraph para = docDes.createParagraph();
                                    para.setAlignment(ParagraphAlignment.RIGHT);
                                    XWPFRun run1 = para.createRun();
                                    run1.setText(dmy);
                                    run1.setBold(true);
                                    run1.setItalic(true);
                                    run1.setFontSize(10);


                                    String strUtf8 = null;
                                    if (!DataUtils.isNullOrEmpty(profile.getId())) {
                                        strUtf8 = profile.getId().toString() + "-Begin";
                                    } else {
                                        strUtf8 = username + "-Begin";
                                    }

                                    byte[] imageByteArray = generateQRCode(strUtf8, 100, 100);

                                    try (InputStream inputByteArrayStream = new ByteArrayInputStream(imageByteArray)) {

                                        // XWPFTableRow row = tableQRCode.getRows().get(0);
                                        // XWPFTableCell cell = row.getCell(0);
                                        // XWPFParagraph paraImage = cell.getParagraphs().get(0);
                                        XWPFParagraph paraImage = docDes.createParagraph();
                                        paraImage.setAlignment(ParagraphAlignment.LEFT);
                                        XWPFRun runImage = paraImage.createRun();
                                        runImage.addPicture(inputByteArrayStream, Document.PICTURE_TYPE_PNG,
                                                "qrCodeHandOver", Units.toEMU(70), Units.toEMU(70));
                                        inputByteArrayStream.close();
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                        logger.error(e.getMessage(), e);
                                    }

                                    
                                    // title
                                    XWPFParagraph paragraph = docDes.createParagraph();
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run = paragraph.createRun();
                                    run.setText("BIÊN BẢN BÀN GIAO");
                                    run.setBold(true);
                                    run.setFontFamily("Times New Roman");
                                    run.setFontSize(12);

                                    XWPFParagraph paragraph2 = docDes.createParagraph();
                                    XWPFRun run2 = paragraph2.createRun();
                                }
                            }

                            if (i == 3) {
                                // add paragraph
                                XWPFParagraph paragraph2 = docDes.createParagraph();
                                XWPFRun run2 = paragraph2.createRun();
                                if (profile != null) {

                                    // String strUtf8 = convertJsonStringToUTF8(profile);
                                    XWPFTable tableQRCode = docDes.createTable(1, 2);
                                    tableQRCode.removeBorders();
                                    tableQRCode.setWidth("100%");
                                    String strUtf8 = profile.getId().toString() + "-End";
                                    byte[] imageByteArray = generateQRCode(strUtf8, 100, 100);

                                    try (InputStream inputByteArrayStream = new ByteArrayInputStream(imageByteArray)) {

                                        XWPFTableRow row = tableQRCode.getRows().get(0);
                                        XWPFTableCell cell = row.getCell(1);
                                        XWPFParagraph paraImage = cell.getParagraphs().get(0);
                                        paraImage.setAlignment(ParagraphAlignment.RIGHT);
                                        XWPFRun runImage = paraImage.createRun();
                                        runImage.addPicture(inputByteArrayStream, Document.PICTURE_TYPE_PNG,
                                                "qrFisnished", Units.toEMU(70), Units.toEMU(70));
                                        inputByteArrayStream.close();
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                        logger.error(e.getMessage(), e);
                                    }

                                    String strTransfer = profile.getId().toString() + "-Move";
                                    byte[] imageByteArrayReturn = generateQRCode(strTransfer, 100, 100);

                                    try (InputStream inputByteArrayStream = new ByteArrayInputStream(
                                            imageByteArrayReturn)) {

                                        XWPFTableRow row = tableQRCode.getRows().get(0);
                                        XWPFTableCell cell = row.getCell(0);
                                        XWPFParagraph paraImage = cell.getParagraphs().get(0);
                                        paraImage.setAlignment(ParagraphAlignment.LEFT);
                                        XWPFRun runImage = paraImage.createRun();
                                        runImage.addPicture(inputByteArrayStream, Document.PICTURE_TYPE_PNG,
                                                "qrMoved", Units.toEMU(70), Units.toEMU(70));
                                        inputByteArrayStream.close();
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                            }
                            i++;
                        }

                    }

                    XWPFStyles styles = docDes.createStyles();

                    CTFonts fonts = CTFonts.Factory.newInstance();
                    fonts.setEastAsia("Times New Roman");
                    fonts.setHAnsi("Times New Roman");
                    styles.setDefaultFonts(fonts);

                    if (!Files.isDirectory(Paths.get(folder))) {
                        new File(folder).mkdir();
                        outputFile.createNewFile();
                    } else {
                        outputFile.createNewFile();
                    }
                    FileOutputStream outpuStream = new FileOutputStream(outputFile);
                    docDes.write(outpuStream);
                    outpuStream.close();
                    docDes.close();
                } catch (Throwable ex) {
                    // TODO: handle exception
                    log.error(ex.getMessage(), ex);

                }
                return outputFile;

            }

        } catch (Throwable ex) {
            // TODO: handle exception
            // System.out.println(ex.getMessage());
            log.error(ex.getMessage(), ex);
            return null;
        }

    }

    // copy style param and table
    public void CopyStyle(XWPFDocument srcDoc, XWPFDocument destDoc, XWPFStyle style) {
        if (destDoc == null || style == null)
            return;

        if (destDoc.getStyles() == null) {
            destDoc.createStyles();
        }

        List<XWPFStyle> usedStyleList = srcDoc.getStyles().getUsedStyleList(style);
        for (XWPFStyle xwpfStyle : usedStyleList) {
            destDoc.getStyles().addStyle(xwpfStyle);
        }
    }

    public ArrayList<String> convertStringToArray(String str) {
        String[] array = str.split(",");
        ArrayList<String> array2 = new ArrayList<String>(Arrays.asList(array));
        return array2;
    }

    public static byte[] generateQRCode(String qrContent, int width, int height) {
        try {
            // Charset charset = Charset.forName("UTF-8");
            // CharsetEncoder encoder = charset.newEncoder();
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // qrCodeWriter.
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);
            // String pathStr = "D:\\";
            // File outputFile = new File("filen");
            // Path path = FileSystems.getDefault().getPath(pathStr, "fileName.png");
            // MatrixToImageWriter.writeToPath(bitMatrix, "png", path);
            // byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String convertJsonStringToUTF8(ProfileDTO dto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            QRCodeDTO qrCodeDTO = mapper.toDtoBean(dto);
            String jsonStr = objectMapper.writeValueAsString(qrCodeDTO);
            byte[] bytes = jsonStr.getBytes(StandardCharsets.UTF_8);
            String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
            return utf8EncodedString;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}