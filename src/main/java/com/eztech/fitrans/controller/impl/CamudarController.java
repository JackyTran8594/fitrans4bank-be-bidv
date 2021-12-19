//package com.eztech.fitrans.controller.impl;
//
//
//import com.eztech.fitrans.service.TestCamudarService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import static org.springframework.http.ResponseEntity.ok;
//
//@Controller
//@RequestMapping("/api/camudar")
//public class CamudarController {
//    private TestCamudarService testCamudarService;
//
//    @Autowired
//    public CamudarController(TestCamudarService testCamudarService) {
//        this.testCamudarService = testCamudarService;
//    }
//
//    //http://localhost:3001/api/camudar/vi
//    //http://localhost:3001/api/camudar/en
//    @GetMapping("/{locale}")
//    public ResponseEntity<String> test(@PathVariable String locale) {
//        return ok(testCamudarService.process(locale));
//    }
//}
