package br.com.flucas.relatorio;

import java.io.OutputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

public class RelatorioPdfExporter extends AbstractRelatorioExporter implements RelatorioExporter {

	@Override
    protected void exportarRelatorio(Relatorio relatorio, JasperPrint jasperPrint, OutputStream outputStreamRelatorio) throws JRException {
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStreamRelatorio);
    }

}
