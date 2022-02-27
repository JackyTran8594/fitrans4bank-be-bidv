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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.image.*;

import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.QRCodeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
// import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
// import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReadAndWriteDoc {

    private static Logger logger = LoggerFactory.getLogger(ReadAndWriteDoc.class);
    private static final BaseMapper<ProfileDTO, QRCodeDTO> mapper = new BaseMapper<>(ProfileDTO.class, QRCodeDTO.class);

    public File ExportDocFile(ProfileDTO profile, String username) {

        try {
            // String url = getClass().getResource("")
            URL resource = getClass().getClassLoader().getResource("template/BIDV_Template.docx");
            URL image = getClass().getClassLoader().getResource("template/bidv.png");
            URL rootFolder = getClass().getClassLoader().getResource("template");
            String outFile = "C:\\BIDV\\BBBG_" + username + "_" + Timestamp.valueOf(LocalDateTime.now());
            File outputFile = new File(outFile);

            if (resource == null) {
                throw new IllegalArgumentException("file not found");
            } else {

                File file = new File(resource.toURI());

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
                                cell1.setText(profile.getStaffId_CM().toString());

                                XWPFTableRow row2 = table.getRow(1);
                                XWPFTableCell cell2 = row2.getCell(1);
                                cell2.setText(profile.getStaffId_CM().toString());

                                XWPFTableRow row3 = table.getRow(2);
                                XWPFTableCell cell3 = row3.getCell(1);
                                cell3.setText(profile.getCif());

                                // XWPFTableRow row4 = table.getRow(3);
                                // XWPFTableCell cell4 = row4.getCell(1);
                                // row1.getCell(1).setText(profile.getCompanyName());

                                XWPFTableRow row5 = table.getRow(4);
                                XWPFTableCell cell5 = row5.getCell(1);
                                cell5.setText(profile.getTypeEnum());

                                // XWPFTableRow row6 = table.getRow(5);
                                // XWPFTableCell cell6 = row6.getCell(1);
                                // row1.getCell(1).setText(profile.getNotes());

                                XWPFTableRow row7 = table.getRow(6);
                                XWPFTableCell cell7 = row7.getCell(1);
                                cell7.setText(String.valueOf(profile.getValue()));

                                // XWPFTableRow row8 = table.getRow(7);
                                // XWPFTableCell cell8 = row8.getCell(1);
                                // row8.getCell(1).setText(profile.getCategoryProfile());

                            }
                            if (i == 2) {
                                if (profile.getCategoryProfile() != null) {
                                    List<String> categories = convertStringToArray(profile.getCategoryProfile());
                                    XWPFTableRow oldRow = table.getRows().get(3);
                                    CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
                                    for (String string : categories) {
                                        XWPFTableRow row = new XWPFTableRow(ctrow, table);
                                        XWPFTableCell cell = row.getCell(1);
                                        cell.removeParagraph(0);
                                        cell.setText(string);
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

                                    String strUtf8 = convertJsonStringToUTF8(profile);
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

                                    String strUtf8 = convertJsonStringToUTF8(profile);
                                    byte[] imageByteArray = generateQRCode(strUtf8, 100, 100);

                                    try (InputStream inputByteArrayStream = new ByteArrayInputStream(imageByteArray)) {

                                        XWPFParagraph paraImage = docDes.createParagraph();
                                        paraImage.setAlignment(ParagraphAlignment.RIGHT);
                                        XWPFRun runImage = paraImage.createRun();
                                        runImage.addPicture(inputByteArrayStream, Document.PICTURE_TYPE_PNG,
                                                "fileName.png", Units.toEMU(70), Units.toEMU(70));
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
                    if (!outputFile.exists()) {
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