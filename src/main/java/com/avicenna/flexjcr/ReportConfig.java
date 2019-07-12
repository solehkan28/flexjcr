package com.avicenna.flexjcr;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReportConfig {

    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private final String reportPath;

    private static ReportConfig ourInstance = new ReportConfig();
    public static ReportConfig i() {
        return ourInstance;
    }

    private ReportConfig() {

        String reportPath = null;

        try (InputStream input = ReportConfig.class.getClassLoader().getResourceAsStream("application.properties")) {

            if(input==null) {
                throw new Exception("Fail to load application.properties");
            }

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            reportPath = prop.getProperty("report.path");

        } catch (Exception e) {
            logger.error(e);
        }

        this.reportPath = reportPath;
    }

    public String getReportPath() {
        return reportPath;
    }
}
