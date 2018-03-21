package br.com.flucas.relatorio;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;

public interface Relatorio {

	public void addParametros(Map<Object, Object> parametros);
	
	public void addParametro(Object parametro, Object object);

	public void setCollectionDataSource(Collection<?> dataSource);
	
	public void setSingleObjectDataSource(Object dataSource);
	
	public void setDataSource(Object dataSource);

	public JasperPrint getReportInstance();
	
	public InputStream toInputStream(FormatoRelatorio formato);
	
	public File toFile(FormatoRelatorio formato);
	
	public File toFile(FormatoRelatorio formato, File file);

}
