package com.avicenna.flexjcr;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.crystaldecisions.sdk.occa.report.application.DBOptions;
import com.crystaldecisions.sdk.occa.report.application.DatabaseController;
import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.data.ConnectionInfo;
import com.crystaldecisions.sdk.occa.report.data.ConnectionInfoKind;
import com.crystaldecisions.sdk.occa.report.data.FieldValueType;
import com.crystaldecisions.sdk.occa.report.data.Fields;
import com.crystaldecisions.sdk.occa.report.data.FormulaField;
import com.crystaldecisions.sdk.occa.report.data.IConnectionInfo;
import com.crystaldecisions.sdk.occa.report.data.IFormulaField;
import com.crystaldecisions.sdk.occa.report.data.IParameterField;
import com.crystaldecisions.sdk.occa.report.data.ITable;
import com.crystaldecisions.sdk.occa.report.data.Tables;
import com.crystaldecisions.sdk.occa.report.exportoptions.ReportExportFormat;
import com.crystaldecisions.sdk.occa.report.lib.IStrings;
import com.crystaldecisions.sdk.occa.report.lib.PropertyBag;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKExceptionBase;

/**
 * This manages report generation and ouput
 * @author Rashidee
 */
public class ReportProcessor {

    private ReportClientDocument rptDoc;

    private final String reportName;
    private final String param1;
    private final String param2;
    private final String param3;
    private final String param4;
    private final String param5;
    private final String param6;
    private final String param7;
    private final String param8;
    private final String param9;
    private final String param10;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    

    public ReportProcessor(String reportName, String param1, String param2, String param3, String param4, String param5,
                           String param6, String param7, String param8, String param9, String param10) {
        // create report doc. the logger is set due to rptDocument internally change the log level
        Level rootLevel = Logger.getRootLogger().getLevel();
        this.rptDoc = new ReportClientDocument();
        Logger.getRootLogger().setLevel(rootLevel);
        
        this.reportName = reportName;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
        this.param5 = param5;
        this.param6 = param6;
        this.param7 = param7;
        this.param8 = param8;
        this.param9 = param9;
        this.param10 = param10;
    }

    /**
     * Generate PDF and save in file system
     * @return
     * 
     */
    
    public ByteArrayInputStream getPDF(HttpServletRequest req) throws ReportSDKException {

        // open report
        rptDoc.open(ReportConfig.i().getReportPath()+reportName+".rpt", 0);

        // set parameters
        _setParameters();

        
        // override connection
        /*
        1. Using _overrideConnections requires JNDI to be setup then only connection successful
        2. Using _overrideTables manage to connect without JNDI
        3. Once connection is sucessfull. Error occured is
           - com.crystaldecisions.reports.dataengine.DataEngineException: Exception in formula '{@TGLLAHIR}' at '{Command.TGLLAHIR}':
         */
        
        List<IFormulaField> formulaBikinMasalah = new ArrayList<>();
        formulaBikinMasalah.addAll(rptDoc.getDataDefController().getDataDefinition().getFormulaFields());
        if(!formulaBikinMasalah.isEmpty()) {
        	formulaBikinMasalah.forEach(x->{
        		try {
        			System.out.println(x.getText());
					rptDoc.getDataDefController().getFormulaFieldController().modify(x,modifFormula(x,x.getText()));
				} catch (ReportSDKException e) {
					e.printStackTrace();
				}
        	});
        }
        
//        IFormulaField tglBikinMasalah = null;
//        for (int i = 0; i < rptDoc.getDataDefController().getDataDefinition().getFormulaFields().size(); i++) {
//        	if(rptDoc.getDataDefController().getDataDefinition().getFormulaFields().get(i).getName().equals("TglDaftar")
//        			){
//        		tglBikinMasalah = rptDoc.getDataDefController().getDataDefinition().getFormulaFields().get(i);
//        		System.out.println("################################ ");
//        		System.out.println(tglBikinMasalah.getText());
//        		System.out.println("################################ ");
//        		System.out.println("Type : "+tglBikinMasalah.getType());
//        		System.out.println("################################ ");
//        	}
//		}
//        rptDoc.getDataDefController().getFormulaFieldController().modify(tglBikinMasalah,modifFormula(tglBikinMasalah));
        

		_overrideTables();
		
//		rptDoc.getDataDefController().getFormulaFieldController().remove(tglBikinError);
//		getTanggalBikinMasalah(rptDoc.getDataDefController());

        // generate PDF
		int count = rptDoc.getDataDefController().getDataDefinition().getParameterFields().size();
		for (int i = 0; i < count; i++) {
			IParameterField param = rptDoc.getDataDefController().getDataDefinition().getParameterFields().get(i);
			System.out.println(param.getType());
			if(param.getType().value() == FieldValueType._dateField) {
				rptDoc.getDataDefController().getParameterFieldController().setCurrentValue("", "param"+(i+1), parseStrToDate(req.getParameter("param"+(i+1))));
			}else if(param.getType().value() == FieldValueType._numberField) {
				rptDoc.getDataDefController().getParameterFieldController().setCurrentValue("", "param"+(i+1), Integer.valueOf((req.getParameter("param"+(i+1)))));
			}else if(param.getType().value() == FieldValueType._dateTimeField) {

				/**
				 * Masih bermasalah datetime param
				 */
//			 rptDoc.getDataDefController().getParameterFieldController().setCurrentValue("", "param"+(i+1), parseStrToDatetime(req.getParameter("param"+(i+1))));
			 rptDoc.getDataDefController().getParameterFieldController().setCurrentValue("", "param"+(i+1), new Date());
			}else {
				rptDoc.getDataDefController().getParameterFieldController().setCurrentValue("", "param"+(i+1), (req.getParameter("param"+(i+1))));
			}
		}
        ByteArrayInputStream byteArrayInputStream = (ByteArrayInputStream) rptDoc.getPrintOutputController().export(ReportExportFormat.PDF);
        rptDoc.close();
        return byteArrayInputStream;
    }
    
    private void _setParameters() throws ReportSDKException {

        Fields params = rptDoc.getDataDefController().getDataDefinition().getParameterFields();
        Iterator iparamFields = params.iterator();

        while(iparamFields.hasNext()) {
            IParameterField paramField = (IParameterField) iparamFields.next();
            if(StringUtils.equalsIgnoreCase(paramField.getName(), "param1")) {
                _setParameter(paramField, param1);
            } else if(StringUtils.equalsIgnoreCase(paramField.getName(), "param2")) {
                _setParameter(paramField, param2);
            } else if(StringUtils.equalsIgnoreCase(paramField.getName(), "param3")) {
                _setParameter(paramField, param3);
            } else if(StringUtils.equalsIgnoreCase(paramField.getName(), "param4")) {
                _setParameter(paramField, param4);
            } else if(StringUtils.equalsIgnoreCase(paramField.getName(), "param5")) {
                _setParameter(paramField, param5);
            } else if(StringUtils.equalsIgnoreCase(paramField.getName(), "param6")) {
                _setParameter(paramField, param6);
            } else if(StringUtils.equalsIgnoreCase(paramField.getName(), "param7")) {
                _setParameter(paramField, param7);
            } else if(StringUtils.equalsIgnoreCase(paramField.getName(), "param8")) {
                _setParameter(paramField, param8);
            } else if(StringUtils.equalsIgnoreCase(paramField.getName(), "param9")) {
                _setParameter(paramField, param9);
            } else if(StringUtils.equalsIgnoreCase(paramField.getName(), "param10")) {
                _setParameter(paramField, param10);
            }
        }
    }
    
    private void _setParameter(IParameterField paramField, String value) throws ReportSDKException {
        if(value!=null) {
        	if(paramField.getType().equals(FieldValueType.dateField)) {
        		paramField.getCurrentValues().clear();
        	}else {
        		paramField.getCurrentValues().add(value);
        	}
        }
    }

    private void _overrideTables() throws ReportSDKException {

        // update connection di reportnya itu sendiri
    	modifyConnection();
    	
        Tables mainTables = rptDoc.getDatabaseController().getDatabase().getTables();
        for(int i = 0; i < mainTables.size(); i++){
            ITable origTable = mainTables.getTable(i);
            ITable newTable = _getNewTable(origTable);
            // Update the table information
            try {
            	rptDoc.getDatabaseController().setTableLocation(origTable, newTable);
			} catch (ReportSDKExceptionBase ex) {
				ex.printStackTrace();
				}catch (Exception ex) {
				ex.printStackTrace();
				}
        }

        // update sub reports
        IStrings subNames = rptDoc.getSubreportController().getSubreportNames();
        for (int subNum=0; subNum<subNames.size(); subNum++) {
            Tables subTables = rptDoc.getSubreportController().getSubreport(subNames.getString(subNum)).getDatabaseController().getDatabase().getTables();
            for(int i = 0;i < subTables.size();i++){
                ITable origTable = subTables.getTable(i);
                ITable newTable = _getNewTable(origTable);
                // Update the table information
                rptDoc.getSubreportController().getSubreport(subNames.getString(subNum)).getDatabaseController().setTableLocation(origTable, newTable);
            }
        }
    }

    private ITable _getNewTable(ITable originalTable) {

        // clone new table from original table
        ITable newTable = (ITable) originalTable.clone(true);
        newTable.setQualifiedName(originalTable.getAlias());

        // Set new table connection property attributes
        PropertyBag propertyBag = new PropertyBag();
        propertyBag.put("Trusted_Connection", "false");
        propertyBag.put("Server Type", "JDBC (JNDI)");
        propertyBag.put("Use JDBC", "true");
        propertyBag.put("Database DLL", "crdb_jdbc.dll");
        propertyBag.put("JNDI Datasource Name", "");
        propertyBag.put("Connection URL", DBConnectionInfo.i().getUrl());
        propertyBag.put("Database Class Name", DBConnectionInfo.i().getDriver());
        propertyBag.put("PreQEServerName", DBConnectionInfo.i().getUrl());
        propertyBag.put("Server Name", DBConnectionInfo.i().getServerName());

        // Change connection information properties
        newTable.getConnectionInfo().setAttributes(propertyBag);
        newTable.getConnectionInfo().setUserName(DBConnectionInfo.i().getUsername());
        newTable.getConnectionInfo().setPassword(DBConnectionInfo.i().getPassword());
        newTable.getConnectionInfo().setKind(ConnectionInfoKind.SQL);

        return newTable;
    }
    
    
    private void modifyConnection() {

    	PropertyBag propertyBag = new PropertyBag();
        propertyBag.put("Trusted_Connection", "false");
        propertyBag.put("Server Type", "JDBC (JNDI)");
        propertyBag.put("Use JDBC", "true");
        propertyBag.put("Database DLL", "crdb_jdbc.dll");
        propertyBag.put("JNDI Datasource Name", "");
        propertyBag.put("Connection URL", DBConnectionInfo.i().getUrl());
        propertyBag.put("Database Class Name", DBConnectionInfo.i().getDriver());
        propertyBag.put("PreQEServerName", DBConnectionInfo.i().getUrl());
        propertyBag.put("Server Name", DBConnectionInfo.i().getServerName());
        
        try {
        	IConnectionInfo oldConnectionInfo = new ConnectionInfo();
        	IConnectionInfo newConnectionInfo = new ConnectionInfo();
        	
        	DatabaseController dbController = rptDoc.getDatabaseController();
        	oldConnectionInfo = dbController.getConnectionInfos(null).getConnectionInfo(0); 
        	
        	
        	newConnectionInfo.setAttributes(propertyBag);
        	newConnectionInfo.setUserName(DBConnectionInfo.i().getUsername());
        	newConnectionInfo.setPassword(DBConnectionInfo.i().getPassword());
        	 newConnectionInfo.setKind(ConnectionInfoKind.SQL);
        	 
        	 int replaceParams = DBOptions._ignoreCurrentTableQualifiers + DBOptions._doNotVerifyDB;
        	 dbController.replaceConnection(oldConnectionInfo, newConnectionInfo, null, replaceParams);
		} catch (Exception e) {
			// TODO: handle exception
		}
        
	}
    
    private FormulaField modifFormula(IFormulaField old,String value) {
    	try {
    		if(old!=null) {
    			FormulaField cloned = (FormulaField) old.clone(true);
    			cloned.setText("{"+StringUtils.substringBetween(value, "{","}")+"}");
    			return cloned;
    		}
		} catch (NullPointerException e) {
			return null;
		}
    	return null;
    }
    
    private Date parseStrToDate(String date) {
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
    
    private Date parseStrToDatetime(String date) {
    	try {
			return datetimeFormatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
    }
    
    
}
