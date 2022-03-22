package com.eztech.fitrans.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import java.awt.image.*;

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
// import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
// import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReadAndWriteDoc {

    private static Logger logger = LoggerFactory.getLogger(ReadAndWriteDoc.class);
    private static final BaseMapper<ProfileDTO, QRCodeDTO> mapper = new BaseMapper<>(ProfileDTO.class, QRCodeDTO.class);

    public File ExportDocFile(ProfileDTO profile, String username, Map<String, ProfileListDTO> mapParams) {

        try {
            // String url = getClass().getResource("")

            // InputStream resource = getClass().getClassLoader().getResourceAsStream("template" + File.separator +"BIDV_Template.docx");
            InputStream resource = getClass().getClassLoader().getResourceAsStream("template/BIDV_Template.docx");

            File file = File.createTempFile("BIDV_Template_", ".docx");
                FileUtils.copyInputStreamToFile(resource, file);
            // System.out.println(resource.)
            // URL image = getClass().getClassLoader().getResource("template/bidv.png");
            // URL rootFolder = getClass().getClassLoader().getResource("template");
            String folder = "C:\\BIDV_BBBG\\";
            String filename = "BBBG_" + username + "_" + Timestamp.valueOf(LocalDateTime.now()).getTime() + ".docx";
            File outputFile = new File(folder + filename);
            ;
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
                                run.setText(profile.getStaffName().toString());
                                // run.setFontFamily("Times New Roman");
                                // run.setFontSize(12);
                                // para1.setText(profile.getStaffName().toString());
                                // para1.setVerticalAlignment(XWPFVertAlign.CENTER);
                                // cell1.setText(profile.getStaffName().toString());
                                // cell1.setVerticalAlignment(XWPFVertAlign.CENTER);

                                // check transactionType : 1,2,3
                                XWPFTableRow row2 = table.getRow(1);
                                XWPFTableCell cell2 = row2.getCell(1);
                                // XWPFParagraph para2 = cell2.addParagraph();
                                // para2.setAlignment(ParagraphAlignment.CENTER);
                                // XWPFRun run2 = para2.createRun();

                                if (profile.getTransactionType().equals(1) | profile.getTransactionType().equals(2)) {
                                    cell2.setText(profile.getStaffNameCM().toString());
                                } else {
                                    cell2.setText(profile.getStaffNameCT().toString());

                                }
                                cell2.setVerticalAlignment(XWPFVertAlign.CENTER);

                                // run2.setFontSize(12);

                                // row 3
                                XWPFTableRow row3 = table.getRow(2);
                                XWPFTableCell cell3 = row3.getCell(1);
                                // XWPFParagraph para3 = cell3.addParagraph();
                                // para3.setAlignment(ParagraphAlignment.CENTER);
                                // XWPFRun run3 = para3.createRun();
                                // run3.setText(profile.getCif());
                                // run3.setFontFamily("Times New Roman");
                                // run3.setFontSize(12);
                                cell3.setText(profile.getCif());
                                cell3.setVerticalAlignment(XWPFVertAlign.CENTER);

                                // row 3
                                XWPFTableRow row4 = table.getRow(3);
                                XWPFTableCell cell4 = row4.getCell(1);
                                // XWPFParagraph para4 = cell4.addParagraph();
                                // para4.setAlignment(ParagraphAlignment.CENTER);
                                // XWPFRun run4 = para4.createRun();
                                // run4.setText(profile.getCustomerName());
                                // run4.setFontFamily("Times New Roman");
                                // run4.setFontSize(12);
                                cell4.setText(profile.getCustomerName());
                                cell4.setVerticalAlignment(XWPFVertAlign.CENTER);

                                // row 5
                                XWPFTableRow row5 = table.getRow(4);
                                XWPFTableCell cell5 = row5.getCell(1);
                                // XWPFParagraph para5 = cell5.addParagraph();
                                // para5.setAlignment(ParagraphAlignment.CENTER);
                                // XWPFRun run5 = para5.createRun();
                                // run5.setText(profile.getTypeEnum());
                                // run5.setFontFamily("Times New Roman");
                                // run5.setFontSize(12);
                                cell5.setText(profile.getTypeEnum());
                                cell5.setVerticalAlignment(XWPFVertAlign.CENTER);

                                // row 6
                                XWPFTableRow row6 = table.getRow(5);
                                XWPFTableCell cell6 = row6.getCell(1);
                                // XWPFParagraph para6 = cell6.addParagraph();
                                // para6.setAlignment(ParagraphAlignment.CENTER);
                                // XWPFRun run6 = para6.createRun();
                                // run6.setText(profile.getNote());
                                // run6.setFontFamily("Times New Roman");
                                // run6.setFontSize(12);
                                cell6.setText(profile.getNote());
                                cell6.setVerticalAlignment(XWPFVertAlign.CENTER);

                                // row 7
                                XWPFTableRow row7 = table.getRow(6);
                                XWPFTableCell cell7 = row7.getCell(1);
                                // XWPFParagraph para7 = cell7.addParagraph();
                                // para7.setAlignment(ParagraphAlignment.CENTER);
                                // XWPFRun run7 = para7.createRun();
                                // run7.setText(profile.getValue().toString());
                                // run7.setFontFamily("Times New Roman");
                                // run7.setFontSize(12);
                                cell7.setText(String.valueOf(profile.getValue()));
                                cell7.setVerticalAlignment(XWPFVertAlign.CENTER);

                            }
                            if (i == 2) {
                                if (profile.getCategoryProfile() != null) {
                                    List<String> categories = convertStringToArray(profile.getCategoryProfile());
                                    XWPFTableRow row2 = table.getRows().get(2);
                                    XWPFTableCell cell2 = row2.getCell(1);
                                    cell2.setText(profile.getTypeEnum());
                                    XWPFTableRow oldRow = table.getRows().get(3);
                                    CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
                                    for (String string : categories) {
                                        XWPFTableRow row = new XWPFTableRow(ctrow, table);
                                        XWPFTableCell cell = row.getCell(1);
                                        cell.removeParagraph(0);
                                        cell.setText(mapParams.get(string).type);
                                        // Integer indexCheckBox =
                                        // Integer.parseInt(mapParams.get(string).profileStatus);
                                        Integer indexCheckBox = mapParams.get(string).profileStatus;
                                        XWPFTableCell checkBoxCell = row.getCell(indexCheckBox);
                                        checkBoxCell.setText("x");
                                        checkBoxCell.setVerticalAlignment(XWPFVertAlign.CENTER);
                                        table.addRow(row);
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

                                    // String strUtf8 = convertJsonStringToUTF8(profile);
                                    String strUtf8 = profile.getId().toString();
                                    byte[] imageByteArray = generateQRCode(strUtf8, 100, 100);

                                    try (InputStream inputByteArrayStream = new ByteArrayInputStream(imageByteArray)) {

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
                                    XWPFParagraph paragraph = docDes.createParagraph();
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                                    XWPFRun run = paragraph.createRun();
                                    run.setText("BIÊN BẢN BÀN GIAO");
                                    run.setBold(true);
                                    run.setFontFamily("Times New Roman");
                                    run.setFontSize(12);

                                }
                            }

                            if (i == 3) {

                                if (profile != null) {

                                    // String strUtf8 = convertJsonStringToUTF8(profile);
                                    String strUtf8 = profile.getId().toString();
                                    byte[] imageByteArray = generateQRCode(strUtf8, 100, 100);

                                    try (InputStream inputByteArrayStream = new ByteArrayInputStream(imageByteArray)) {

                                        XWPFParagraph paraImage = docDes.createParagraph();
                                        paraImage.setAlignment(ParagraphAlignment.RIGHT);
                                        XWPFRun runImage = paraImage.createRun();
                                        runImage.addPicture(inputByteArrayStream, Document.PICTURE_TYPE_PNG,
                                                "qrFisnished", Units.toEMU(70), Units.toEMU(70));
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

                    // File fileOuput = new File(folder + filename);

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

        } catch (

        Throwable ex) {
            // TODO: handle exception
            // System.out.println(ex.getMessage());
            log.error(ex.getMessage(), ex);
            return null;
        }

    }

    // public static void CopyLayout(XWPFDocument srcDoc, XWPFDocument destDoc) {

    // CTPageMar pgMar = srcDoc.getDocument().getBody().getSectPr().getPgMar();

    // BigInteger bottom = (BigInteger) pgMar.getBottom();
    // BigInteger footer = (BigInteger) pgMar.getFooter();
    // BigInteger gutter = (BigInteger) pgMar.getGutter();
    // BigInteger header = (BigInteger) pgMar.getHeader();
    // BigInteger left = (BigInteger) pgMar.getLeft();
    // BigInteger right = (BigInteger) pgMar.getRight();
    // BigInteger top = (BigInteger) pgMar.getTop();

    // CTPageMar addNewPgMar =
    // destDoc.getDocument().getBody().addNewSectPr().addNewPgMar();

    // addNewPgMar.setBottom(bottom);
    // addNewPgMar.setFooter(footer);
    // addNewPgMar.setGutter(gutter);
    // addNewPgMar.setHeader(header);
    // addNewPgMar.setLeft(left);
    // addNewPgMar.setRight(right);
    // addNewPgMar.setTop(top);

    // CTPageSz pgSzSrc = srcDoc.getDocument().getBody().getSectPr().getPgSz();

    // BigInteger code = pgSzSrc.getCode();
    // BigInteger h = (BigInteger) pgSzSrc.getH();
    // // Enum orient = pgSzSrc.getOrient();
    // BigInteger w = (BigInteger) pgSzSrc.getW();

    // CTPageSz addNewPgSz =
    // destDoc.getDocument().getBody().addNewSectPr().addNewPgSz();

    // addNewPgSz.setCode(code);
    // addNewPgSz.setH(h);
    // // addNewPgSz.setOrient(orient);
    // addNewPgSz.setW(w);
    // }

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

    public List<String> convertStringToArray(String str) {
        String[] array = str.split(",");
        List<String> array2 = Arrays.asList(array);
        return array2;
    }

    public static byte[] generateQRCode(String qrContent, int width, int height) {
        try {
            // Charset charset = Charset.forName("UTF-8");
            // CharsetEncoder encoder = charset.newEncoder();
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
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