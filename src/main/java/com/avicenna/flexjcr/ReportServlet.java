package com.avicenna.flexjcr;

import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            // get parameters
            String reportName = req.getParameter("reportName");
            String param1 = req.getParameter("param1");
            String param2 = req.getParameter("param2");
            String param3 = req.getParameter("param3");
            String param4 = req.getParameter("param4");
            String param5 = req.getParameter("param5");
            String param6 = req.getParameter("param6");
            String param7 = req.getParameter("param7");
            String param8 = req.getParameter("param8");
            String param9 = req.getParameter("param9");
            String param10 = req.getParameter("param10");

            // generate pdf
            ReportProcessor processor = new ReportProcessor(reportName,
                    param1, param2, param3, param4, param5, param6, param7, param8, param9, param10);
            ByteArrayInputStream bis = processor.getPDF(req);

            //Create a byte[] the same size as the exported ByteArrayInputStream.
            byte[] buffer = new byte[bis.available()];
            int bytesRead = 0;

            //Set response headers to indicate mime type and inline file.
            //resp.reset();
            resp.setHeader("Content-disposition", "inline;filename=report.pdf");
            resp.setContentType("application/pdf");

            //Stream the byte array to the client.
            while ((bytesRead = bis.read(buffer)) != -1) {
                resp.getOutputStream().write(buffer, 0, bytesRead);
            }

            //Flush and close the output stream.
            resp.getOutputStream().flush();
            resp.getOutputStream().close();

        } catch (ReportSDKException e) {
            e.printStackTrace();
        }
    }
}
