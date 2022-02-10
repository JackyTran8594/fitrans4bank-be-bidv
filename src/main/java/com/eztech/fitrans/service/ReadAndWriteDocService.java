package com.eztech.fitrans.service;

import java.util.List;

import com.eztech.fitrans.dto.response.ProfileDTO;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFStyle;

public interface ReadAndWriteDocService {
    
    void WriteDocument() ;

    void ExportDocFile(ProfileDTO profile);

    void CopyLayout(XWPFDocument srcDoc, XWPFDocument destDoc);

    // copy style param and table
    void CopyStyle(XWPFDocument srcDoc, XWPFDocument destDoc, XWPFStyle style);

    List<String> convertStringToArray(String str);
}
