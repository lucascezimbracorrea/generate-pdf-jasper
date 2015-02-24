package jasper;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

public class ReportManager {

	private String reports_path ;
	private JasperReport report;
	private JRDataSource source;
	private Map<String, Object> parameters;
	
	private JasperPrint jasperPrint;
	
	public ReportManager(String reports_path,String report) {
		this(reports_path, report, (Collection<Object>) null);
	}
	
	public ReportManager(String reports_path,String report, Collection<?> data) {
		this(reports_path, report, new JRBeanCollectionDataSource(data == null ? new ArrayList<Object>(0) : data));
	}
	
	public ReportManager(String reports_path, String report, JRDataSource dataSource) {
		if (dataSource == null) {
			dataSource = new JRBeanCollectionDataSource(new ArrayList<Object>(0));
		}
		
		this.reports_path = reports_path;
		this.report = getReport(report);
		this.source = dataSource;
		this.parameters = new HashMap<String, Object>();
	}
	
	public boolean hasParameter(String variable) {
		return parameters.containsKey(variable);
	}
	
	public void putParameter(String variable, Object value) {
		parameters.put(variable, value);
	}
	
	public void putSubReport(String variable, String subReportName) {
		JasperReport subReport = getReport(subReportName);
		putParameter(variable, subReport);
	}
	
	public void putImage(String variable, String local_image) {
		try {
			Image image = ImageIO.read(loadStream(local_image));
			putParameter(variable, image);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void putParameters(Map<String, Object> parameters) {
		parameters.putAll(parameters);
	}
	
	public void setData(Collection<?> data) {
		setDataSource(new JRBeanCollectionDataSource(data == null ? new ArrayList<Object>(0) : data));
	}
	
	public void setDataSource(JRDataSource dataSource) {
		if (dataSource == null) {
			dataSource = new JRBeanCollectionDataSource(new ArrayList<Object>(0));
		}
		
		this.source = dataSource;
	}
	
	public JasperPrint generateReport() {
		try {
			jasperPrint = JasperFillManager.fillReport(report, parameters, source);
			return jasperPrint;
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
	}
	
	private InputStream loadStream(String file) {
		try {
			return new FileInputStream(new File(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JasperReport getReport(String reportName) {
		try {
			reportName = reportName.trim().replaceFirst("\\.jasper$", "");
			
			return (JasperReport) JRLoader.loadObject(loadStream(this.reports_path + String.format("%s.jasper", reportName)));
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void exportReportToPdfFile (String destination) throws JRException {
		JasperExportManager.exportReportToPdfFile(jasperPrint, destination);
	}
	
	public byte[] exportPdfInByte() throws JRException{
		return JasperExportManager.exportReportToPdf(this.jasperPrint); 
	}
	
	public JsonDataSource createJsonDataSource(String json) throws JRException {
		InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		
		return new JsonDataSource(stream);
	}
	
}