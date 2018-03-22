package br.com.flucas.relatorio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

public abstract class AbstractRelatorioExporter implements RelatorioExporter {

    @Override
    public InputStream exportarRelatorio(Relatorio relatorio) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            exportarRelatorio(relatorio, outputStream);
            
            byte[] bytesRelatorio = outputStream.toByteArray();
            
            if (bytesRelatorio.length <= 0) {
                return null;
            }
            
            return new ByteArrayInputStream(bytesRelatorio);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
				outputStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
        }
    }
    
    private void exportarRelatorio(Relatorio relatorio, OutputStream outputStreamRelatorio) throws Exception {
        Object reportInstance = relatorio.getReportInstance();
        
        if (reportInstance == null) {
            return;
        }
        
        if (reportInstance instanceof JasperPrint) {
            exportarRelatorio(relatorio, (JasperPrint) reportInstance, outputStreamRelatorio);
        }
    }

    protected abstract void exportarRelatorio(Relatorio relatorio, JasperPrint jasperPrint, OutputStream outputStreamRelatorio) throws JRException;

}
