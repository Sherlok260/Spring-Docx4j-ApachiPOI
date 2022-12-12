package com.example.springdocx4japachipoi.config;

import org.python.util.PythonInterpreter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PythonInterpreterConfiguration {

    @Bean
    public PythonInterpreter pythonInterpreter() {
        PythonInterpreter pythonInterpreter = new PythonInterpreter();
        pythonInterpreter.execfile("translate.py");
        return pythonInterpreter;
    }

}
