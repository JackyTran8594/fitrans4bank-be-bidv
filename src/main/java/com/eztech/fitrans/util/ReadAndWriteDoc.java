package com.eztech.fitrans.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eztech.fitrans.dto.response.ProfileDTO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
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

    // public static String decodeBase64DocFile(String str) {
    // String newStr = "";
    // return newStr;
    // }
    private static Logger logger = LoggerFactory.getLogger(ReadAndWriteDoc.class);

    public void WriteDocument() {
        XWPFDocument document = new XWPFDocument();

        try {

            // header
            XWPFTable header = document.createTable();

            XWPFTableRow hRow1 = header.getRow(0);
            hRow1.getCell(0).setText("NGÂN HÀNG TMCP ĐẦU TƯ VÀ  PHÁT TRIỂN VIỆT NAM CHI NHÁNH SỞ GIAO DỊCH 1 ");
            hRow1.addNewTableCell().setText("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM \n Độc lập – Tự do – Hạnh phúc");

            XWPFTableRow hRow2 = header.createRow();
            hRow2.getCell(0).setText("Số:");
            hRow2.getCell(1).setText("Hà Nội, ngày …… tháng …… năm…….");

            // add paragraph to add line break
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run4 = paragraph.createRun();
            run4.addBreak();

            // title
            XWPFTable title = document.createTable();

            XWPFTableRow tRow1 = title.getRow(0);
            tRow1.getCell(0).setText("QRCode");
            tRow1.addNewTableCell().setText("BIÊN BẢN BÀN GIAO HỒ SƠ");
            tRow1.addNewTableCell().setText("");

            // add paragraph to add line break
            XWPFParagraph paragraph2 = document.createParagraph();
            XWPFRun run5 = paragraph2.createRun();
            run5.addBreak();
            // end title

            // content
            XWPFTable content = document.createTable();

            XWPFTableRow cRow1 = content.getRow(0);
            cRow1.getCell(0).setText("1.  Bên giao:");
            cRow1.addNewTableCell().setText("");

            XWPFTableRow cRow2 = content.createRow();
            cRow2.getCell(0).setText("2.	Bên nhận:");
            cRow2.getCell(1).setText("");

            XWPFTableRow cRow3 = content.createRow();
            cRow3.getCell(0).setText("3.	CIF:");
            cRow3.getCell(1).setText("");

            XWPFTableRow cRow4 = content.createRow();
            cRow4.getCell(0).setText("4.	Tên doanh nghiệp:");
            cRow4.getCell(1).setText("");

            XWPFTableRow cRow5 = content.createRow();
            cRow5.getCell(0).setText("5.	Loại giao dịch:");
            cRow5.getCell(1).setText("");

            XWPFTableRow cRow6 = content.createRow();
            cRow6.getCell(0).setText("6.	Mô tả hồ sơ");
            cRow6.getCell(1).setText("");

            XWPFTableRow cRow7 = content.createRow();
            cRow7.getCell(0).setText("7.	Giá trị giao dịch");
            cRow7.getCell(1).setText("");

            XWPFTableRow cRow8 = content.createRow();
            cRow8.getCell(0).setText("8.	Danh mục hồ sơ bàn giao:");
            cRow8.getCell(1).setText("");

            // add paragraph to add line break
            XWPFParagraph paragraph3 = document.createParagraph();
            XWPFRun run6 = paragraph3.createRun();
            run6.addBreak();

            // table_content
            XWPFTable table_content = document.createTable();

            XWPFTableRow tcRow1 = table_content.getRow(0);
            tcRow1.getCell(0).setText("STT");
            tcRow1.addNewTableCell().setText("Loại hồ sơ");
            tcRow1.addNewTableCell().setText("Số, ngày");
            tcRow1.addNewTableCell().setText("Số lượng");
            tcRow1.addNewTableCell().setText("Tình trạng văn bản");
            tcRow1.addNewTableCell().setText("");
            tcRow1.addNewTableCell().setText("");
            tcRow1.addNewTableCell().setText("");

            XWPFTableRow tcRow2 = table_content.createRow();
            tcRow2.getCell(0).setText("");
            tcRow2.getCell(1).setText("");
            tcRow2.getCell(2).setText("");
            tcRow2.getCell(3).setText("");
            tcRow2.getCell(4).setText("Bản gốc");
            tcRow2.getCell(5).setText("Bản công chứng");
            tcRow2.getCell(6).setText("Bản sao y");
            tcRow2.getCell(7).setText("Bản photo");

            // // merge row1 => merge "Tình trạng văn bản"
            // CTHMerge hMerge = CTHMerge.Factory.newInstance();
            // hMerge.setVal(STMerge.RESTART);
            // table_content.getRow(0).getCell(4).getCTTc().getTcPr().setHMerge(hMerge);
            // table_content.getRow(1).getCell(4).getCTTc().getTcPr().setHMerge(hMerge);

            // // Secound Row cell will be merged/"deleted"
            // CTHMerge hMerge1 = CTHMerge.Factory.newInstance();
            // hMerge.setVal(STMerge.CONTINUE);
            // table_content.getRow(0).getCell(7).getCTTc().getTcPr().setHMerge(hMerge1);
            // table_content.getRow(1).getCell(7).getCTTc().getTcPr().setHMerge(hMerge1);

            // table_content.getRow(1).getCell(4).getCTTc().getTcPr().setHMerge(hMerge1);

            // for (int i = 0; i < 4; i++) {
            // // First Row
            // CTVMerge vmerge = CTVMerge.Factory.newInstance();
            // vmerge.setVal(STMerge.RESTART);
            // table_content.getRow(0).getCell(i).getCTTc().getTcPr().setVMerge(vmerge);
            // table_content.getRow(0).getCell(i + 1).getCTTc().getTcPr().setVMerge(vmerge);

            // // Secound Row cell will be merged
            // CTVMerge vmerge1 = CTVMerge.Factory.newInstance();
            // vmerge.setVal(STMerge.CONTINUE);
            // table_content.getRow(1).getCell(i).getCTTc().getTcPr().setVMerge(vmerge1);
            // table_content.getRow(1).getCell(i +
            // 1).getCTTc().getTcPr().setVMerge(vmerge1);
            // }

            XWPFTableRow tcRow3 = table_content.createRow();
            tcRow3.getCell(0).setText("I");
            tcRow3.getCell(1).setText("");
            tcRow3.getCell(2).setText("");
            tcRow3.getCell(3).setText("");
            tcRow3.getCell(4).setText("");
            tcRow3.getCell(5).setText("");
            tcRow3.getCell(6).setText("");
            tcRow3.getCell(7).setText("");

            // merge row3 => merge "Hồ sơ vay vốn"
            // CTHMerge hMerge3 = CTHMerge.Factory.newInstance();
            // hMerge3.setVal(STMerge.RESTART);
            // table_content.getRow(0).getCell(0).getCTTc().getTcPr().setHMerge(hMerge3);
            // table_content.getRow(1).getCell(0).getCTTc().getTcPr().setHMerge(hMerge3);

            // // Secound Row cell will be merged/"deleted"
            // CTHMerge hMerge4 = CTHMerge.Factory.newInstance();
            // hMerge4.setVal(STMerge.CONTINUE);
            // table_content.getRow(0).getCell(7).getCTTc().getTcPr().setHMerge(hMerge4);
            // table_content.getRow(1).getCell(7).getCTTc().getTcPr().setHMerge(hMerge4);

            // add paragraph to add line break
            XWPFParagraph paragraph4 = document.createParagraph();
            XWPFRun run7 = paragraph4.createRun();
            run7.addBreak();

            // table_content
            XWPFTable signature = document.createTable();

            XWPFTableRow signRow1 = signature.getRow(0);
            signRow1.getCell(0).setText("BÊN GIAO");
            signRow1.addNewTableCell().setText("BÊN NHẬN");

            XWPFTableRow signRow2 = signature.createRow();
            signRow2.getCell(0).setText("");
            signRow2.getCell(1).setText("");

            try {
                String output = "destination.docx";
                URL rootFolder = getClass().getClassLoader().getResource("template");
                File file = new File("D:\\" + output);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                document.write(outputStream);
                outputStream.close();
                document.close();

            } catch (Exception e) {
                // TODO: handle exception
                logger.error(e.getMessage(), e);
            }

        } catch (Exception e) {
            // TODO: handle exception
            logger.error(e.getMessage(), e);
        }
    }

    public void ExportDocFile(ProfileDTO profile) {

        try {
            // String url = getClass().getResource("")
            URL resource = getClass().getClassLoader().getResource("template/BIDV_Template.docx");
            URL image = getClass().getClassLoader().getResource("template/bidv.png");
            URL rootFolder = getClass().getClassLoader().getResource("template");
            if (resource == null) {
                throw new IllegalArgumentException("file not found");
            } else {

                File file = new File(resource.toURI());

                try (FileInputStream inpuStream = new FileInputStream(file)) {
                    XWPFDocument docOrigin = new XWPFDocument(inpuStream);
                    XWPFDocument docDes = new XWPFDocument();
                    // 5 table in template
                    int i = 0;
                    for (IBodyElement bodyElement : docOrigin.getBodyElements()) {

                        BodyElementType elementType = bodyElement.getElementType();

                        if (elementType == BodyElementType.TABLE) {

                            XWPFTable table = (XWPFTable) bodyElement;

                            CopyStyle(docOrigin, docDes, docOrigin.getStyles().getStyle(table.getStyleID()));

                            if (i == 1) {

                            }
                            if (i == 2) {

                                XWPFTableRow row1 = table.getRow(0);
                                XWPFTableCell cell1 = row1.getCell(1);
                                cell1.setText(profile.getStaffId_CM());

                                XWPFTableRow row2 = table.getRow(1);
                                XWPFTableCell cell2 = row2.getCell(1);
                                cell2.setText(profile.getStaffId_CM());

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
                            if (i == 3) {
                                if (profile.getCategoryProfile() != null) {
                                    List<String> categories = convertStringToArray(profile.getCategoryProfile());
                                    XWPFTableRow oldRow = table.getRows().get(3);
                                    CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
                                    for (String string : categories) {
                                        XWPFTableRow row = new XWPFTableRow(ctrow, table);
                                        XWPFTableCell cell = row.getCell(1);
                                        cell.removeParagraph(0);
                                        cell.setText(string);
                                        
                                        // XWPFTableRow row = table.createRow();
                                        // int i = 0;
                                        // for (XWPFTableCell cell : row.getTableCells()) {
                                        // for (XWPFParagraph paragraph : cell.getParagraphs()) {
                                        // for (XWPFRun r : paragraph.getRuns()) {
                                        // r.setText("i" + i++, 0);
                                        // }
                                        // }
                                        // }
                                        // row.getCell(0).setText("");
                                        // row.addNewTableCell().setText(string);
                                        table.addRow(row);
                                    }

                                }
                            }
                            
                            XWPFParagraph paragraph = docDes.createParagraph();
                            XWPFRun run = paragraph.createRun();
                            run.addBreak();


                            docDes.createTable();

                            docDes.setTable(i, table);
                            i++;
                        }
                      

                    }
                    String outFile = "D:\\destination.docx";
                    File outputFile = new File(outFile);
                    outputFile.createNewFile();
                    FileOutputStream outpuStream = new FileOutputStream(outputFile);
                    docDes.write(outpuStream);
                    outpuStream.close();
                    docDes.close();
                } catch (Throwable ex) {
                    // System.out.println(ex.getMessage());
                    log.error(ex.getMessage(), ex);

                    // TODO: handle exception
                }

            }

        } catch (Throwable ex) {
            // TODO: handle exception
            // System.out.println(ex.getMessage());
            log.error(ex.getMessage(), ex);
        }

    }

    public static void CopyLayout(XWPFDocument srcDoc, XWPFDocument destDoc) {

        CTPageMar pgMar = srcDoc.getDocument().getBody().getSectPr().getPgMar();

        BigInteger bottom = (BigInteger) pgMar.getBottom();
        BigInteger footer = (BigInteger) pgMar.getFooter();
        BigInteger gutter = (BigInteger) pgMar.getGutter();
        BigInteger header = (BigInteger) pgMar.getHeader();
        BigInteger left = (BigInteger) pgMar.getLeft();
        BigInteger right = (BigInteger) pgMar.getRight();
        BigInteger top = (BigInteger) pgMar.getTop();

        CTPageMar addNewPgMar = destDoc.getDocument().getBody().addNewSectPr().addNewPgMar();

        addNewPgMar.setBottom(bottom);
        addNewPgMar.setFooter(footer);
        addNewPgMar.setGutter(gutter);
        addNewPgMar.setHeader(header);
        addNewPgMar.setLeft(left);
        addNewPgMar.setRight(right);
        addNewPgMar.setTop(top);

        CTPageSz pgSzSrc = srcDoc.getDocument().getBody().getSectPr().getPgSz();

        BigInteger code = pgSzSrc.getCode();
        BigInteger h = (BigInteger) pgSzSrc.getH();
        // Enum orient = pgSzSrc.getOrient();
        BigInteger w = (BigInteger) pgSzSrc.getW();

        CTPageSz addNewPgSz = destDoc.getDocument().getBody().addNewSectPr().addNewPgSz();

        addNewPgSz.setCode(code);
        addNewPgSz.setH(h);
        // addNewPgSz.setOrient(orient);
        addNewPgSz.setW(w);
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

    public List<String> convertStringToArray(String str) {
        String[] array = str.split(",");
        List<String> array2 = Arrays.asList(array);
        return array2;
    }


    public byte[] generateQRCode(String qrContent, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e.getMessage());
        }
        return null;
    }


}