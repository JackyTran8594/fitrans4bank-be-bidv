package com.eztech.fitrans.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;

public class ReadAndWriteDoc {

    public static String decodeBase64DocFile(String str) {
        String newStr = "";
        return newStr;
    }

    public void ExportDocFile() throws IOException{

        try {
            File file = new File(getClass().getResource("BIDV_Template.docx").getFile());
            File outFile = new File(file.getAbsolutePath() + "/destination.docx");
            FileInputStream inpuStream = new FileInputStream(file);
            FileOutputStream outpuStream = new FileOutputStream(outFile);
         
            XWPFDocument docOrigin = new XWPFDocument(inpuStream);
            XWPFDocument docDes = new XWPFDocument();


            for (IBodyElement bodyElement : docOrigin.getBodyElements()) {

                BodyElementType elementType = bodyElement.getElementType();
    
              if (elementType == BodyElementType.TABLE) {
    
                    XWPFTable table = (XWPFTable) bodyElement;
    
                    CopyStyle(docOrigin, docDes, docOrigin.getStyles().getStyle(table.getStyleID()));
    
                    docDes.createTable();
    
                    int pos = docDes.getTables().size() - 1;
    
                    docDes.setTable(pos, table);
                }
            
    
                docDes.write(outpuStream);
                outpuStream.close();
            }

        } catch (Exception e) {
            // TODO: handle exception
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
    public static void CopyStyle(XWPFDocument srcDoc, XWPFDocument destDoc, XWPFStyle style) {
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

}