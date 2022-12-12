package com.example.springdocx4japachipoi.service;

import com.example.springdocx4japachipoi.config.PythonInterpreterConfiguration;
import com.example.springdocx4japachipoi.payload.ApiResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ApachiPOIService {

    @Autowired
    PythonInterpreterConfiguration py;

    public void replacer(PythonInterpreter pythonInterpreter, XWPFRun run, String r1) {
        try {
            PyObject latin_to_kril = pythonInterpreter.eval("to_cyrillic('"+r1+"')");

            String response_latin_to_kril = latin_to_kril.toString();
            byte bytess[] = response_latin_to_kril.getBytes("ISO-8859-1");
            String result_latin_to_kril = new String(bytess, "UTF-8");

            run.setText(result_latin_to_kril, 0);
            System.out.println(run);
        } catch (Exception e) {
            System.out.println(r1);
        }
    }

    public ApiResponse translator() throws InvalidFormatException, IOException {

        XWPFDocument docx = new XWPFDocument(OPCPackage.open("Reja.docx"));
//        XWPFDocument docx = new XWPFDocument(OPCPackage.open("bir_nimala.docx"));

        //for pages
        List<XWPFParagraph> paragraphs = docx.getParagraphs();
        for(XWPFParagraph xwpfParagraph: paragraphs) {
            List<XWPFRun> runs = xwpfParagraph.getRuns();
            for(XWPFRun run: runs) {
                String r1 = String.valueOf(run);
                if(r1.equals(" ") || r1.equals("\n")) {
                    continue;
                }
                replacer(py.pythonInterpreter(), run, r1);
            }
        }

        //for tables
        for (XWPFTable tbl : docx.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun run : p.getRuns()) {
                            String r1 = String.valueOf(run);
                            if(r1.equals(" ") || r1.equals("\n")) {
                                continue;
                            }
                            replacer(py.pythonInterpreter(), run, r1);
                        }
                    }
                }
            }
        }

        //for saves
        try {
            docx.write(new FileOutputStream("Result.docx"));
            return new ApiResponse("success", true);
        } catch (IOException e) {
            e.printStackTrace();
            return new ApiResponse("failed", false);
        }
    }

}
