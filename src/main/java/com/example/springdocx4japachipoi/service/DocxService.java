package com.example.springdocx4japachipoi.service;

import com.example.springdocx4japachipoi.config.PythonInterpreterConfiguration;
import com.example.springdocx4japachipoi.payload.ApiResponse;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Text;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DocxService {

    @Autowired
    PythonInterpreterConfiguration py;

    public String replacer(PythonInterpreter pythonInterpreter, String text) throws UnsupportedEncodingException, UnsupportedEncodingException {
        PyObject eval = pythonInterpreter.eval("to_cyrillic('" + text + "')");
        String response = eval.toString();
        byte bytes[] = response.getBytes("ISO-8859-1");
        String result = new String(bytes, StandardCharsets.UTF_8);
        return result;
    }

    public ApiResponse translator() throws JAXBException, Docx4JException {

        File doc = new File("Reja.docx");
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
                .load(doc);
        MainDocumentPart mainDocumentPart = wordMLPackage
                .getMainDocumentPart();
        String textNodesXPath = "//w:t";
        List<Object> textNodes= mainDocumentPart
                .getJAXBNodesViaXPath(textNodesXPath, false);
        String xml = mainDocumentPart.getXML();
        for (Object obj : textNodes) {
            Text text = (Text) ((JAXBElement) obj).getValue();
            if(text.getValue() == " " || text.getValue() == "\n") {
                continue;
            }
            try {
                String temp = replacer(py.pythonInterpreter(), text.getValue());
                temp = "<w:t>"+temp+"</w:t>";
                xml = xml.replace("<w:t>"+text.getValue()+"</w:t>", temp);
            } catch (Exception e) {
//                System.out.println(text.getValue());
            }
        }
//        System.out.println(xml);

        try {
            mainDocumentPart.setJaxbElement((org.docx4j.wml.Document) XmlUtils.unmarshalString(xml));
            wordMLPackage.save(new File("Result.docx"));
            return new ApiResponse("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("failed", false);
        }
    }
}
