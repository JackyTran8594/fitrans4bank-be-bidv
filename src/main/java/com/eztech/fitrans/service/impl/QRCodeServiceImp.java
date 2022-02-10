package com.eztech.fitrans.service.impl;

import java.io.ByteArrayOutputStream;

import com.eztech.fitrans.service.QRCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class QRCodeServiceImp implements QRCodeService {

    private static Logger logger = LoggerFactory.getLogger(QRCodeServiceImp.class);

    @Override
    public byte[] generateQRCode(String qrContent, int width, int height) {
        // TODO Auto-generated method stub
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            //TODO: handle exception
            logger.error(e.getMessage(), e);
            
        }
        return null;
    }
    
}
