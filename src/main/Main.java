package main;

import jasper.ReportManager;

import java.io.FileReader;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {

	public static void main(String[] args) {

		try {
			
			String params = "";
			for ( String arg : args ) {
				params += arg;
			}
			
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(params);
			
			String file = (String) jsonObject.get("file");
			String path = (String) jsonObject.get("path");
			
			ReportManager reportManager = new ReportManager(path,file);
			
			if (jsonObject.containsKey("images")) {
				JSONArray jsonImages = (JSONArray) jsonObject.get("images");
				
				for (int i = 0; i < jsonImages.size(); i++) {
					JSONObject obj = (JSONObject) jsonImages.get(i);
					reportManager.putImage((String) obj.get("variable"),(String) obj.get("image_local"));
				}
			}			
			
			if (jsonObject.containsKey("params")) {
				JSONObject paramsObject = (JSONObject) jsonObject.get("params");
				reportManager.setDataSource(reportManager.createJsonDataSource(paramsObject.toJSONString()));
				
			} else if (jsonObject.containsKey("json_params")) {
				JSONObject jsonParams = (JSONObject) jsonParser.parse(new FileReader((String) jsonObject.get("json_params")));
				reportManager.setDataSource(reportManager.createJsonDataSource(jsonParams.toJSONString()));
			}
			
			if (jsonObject.containsKey("parameters")) {
				JSONArray jsonParamenters = (JSONArray) jsonObject.get("parameters");
				
				for (int i = 0; i < jsonParamenters.size(); i++) {
					JSONObject obj = (JSONObject) jsonParamenters.get(i);
					reportManager.putParameter((String) obj.get("variable"),(JSONObject) obj.get("value"));
				}
			}
			
			if (jsonObject.containsKey("sub_reports")) {
				JSONArray subreports = (JSONArray) jsonObject.get("sub_reports");
				
				for (int i = 0; i < subreports.size(); i++) {
					JSONObject obj = (JSONObject) subreports.get(i);
					reportManager.putSubReport((String) obj.get("variable"),(String) obj.get("subreport_name"));
				}
			}
			
			//Generate Report
			JasperPrint print = reportManager.generateReport();
			
			//Export to PDF
			if (jsonObject.containsKey("destination_file")) {
				String destination_file = (String) jsonObject.get("destination_file");
				reportManager.exportReportToPdfFile(destination_file);
			} else {
				JasperExportManager.exportReportToPdfStream(print, System.out);
			}
			
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

}
