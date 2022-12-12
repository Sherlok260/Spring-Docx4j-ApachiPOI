package com.example.springdocx4japachipoi.controller;

import com.example.springdocx4japachipoi.payload.ApiResponse;
import com.example.springdocx4japachipoi.service.ApachiPOIService;
import com.example.springdocx4japachipoi.service.DocxService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api")
public class TranslatorController {

    @Autowired
    DocxService docxService;

    @Autowired
    ApachiPOIService apachiPOIService;


//    @PostMapping("/file")
//    public HttpEntity<?> file(@RequestParam("file")MultipartFile multipartFile) throws IOException, InvalidFormatException {
//
//        apachiPOIService.translator();
//
//    }

    @GetMapping("/hello")
    public HttpEntity<?> hello() {
        return ResponseEntity.ok("hello");
    }

    @GetMapping("/test")
    public HttpEntity<?> test() throws IOException, InvalidFormatException, JAXBException, Docx4JException {
//        ApiResponse translator = apachiPOIService.translator();
        ApiResponse translator2 = docxService.translator();
        return ResponseEntity.ok(translator2);
    }

    @GetMapping(value = "/getfile", produces = { "application/octet-stream" })
    public ResponseEntity<byte[]> download() {

        try {

            File file = new File("Result.docx");
            byte[] contents = Files.readAllBytes(file.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename("Result.docx").build());

            return new ResponseEntity<>(contents, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
