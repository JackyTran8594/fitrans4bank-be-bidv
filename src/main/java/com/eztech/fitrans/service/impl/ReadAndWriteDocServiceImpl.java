package com.eztech.fitrans.service.impl;

import java.util.List;

import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.service.ReadAndWriteDocService;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.springframework.stereotype.Service;


@Service
public class ReadAndWriteDocServiceImpl implements ReadAndWriteDocService {

    @Override
    public void ExportDocFile(ProfileDTO profile) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void CopyLayout(XWPFDocument srcDoc, XWPFDocument destDoc) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void CopyStyle(XWPFDocument srcDoc, XWPFDocument destDoc, XWPFStyle style) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<String> convertStringToArray(String str) {
        // TODO Auto-generated method stub
        return null;
    }
    


}
