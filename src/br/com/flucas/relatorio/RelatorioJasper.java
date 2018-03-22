package br.com.flucas.relatorio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.com.flucas.util.IOUtils;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

public class RelatorioJasper implements Relatorio {
	
	private final File arquivoJasper;
	private final JasperReport jasperReport;
	
	private final Map<String, Object> parametros = new HashMap<String, Object>();
	
	private JRDataSource dataSource;
	
	public RelatorioJasper(File arquivoJasper) {
		this.arquivoJasper = arquivoJasper;
		this.jasperReport = createJasperReport();
	}
	
	private JasperReport createJasperReport() {
		try {
            return (JasperReport) JRLoader.loadObject(arquivoJasper);
        } catch (JRException ex) {
            throw new RuntimeException(ex);
        }
	}

	@Override
	public void addParametros(Map<Object, Object> parametros) {
		for (Object parametro : parametros.keySet()) {
			addParametro(parametro, parametros.get(parametro));
		}
	}

	@Override
	public void addParametro(Object parametro, Object object) {
		parametros.put(parametro.toString(), object);
	}

	@Override
	public void setCollectionDataSource(Collection<?> dataSource) {
		setDataSource(new JRBeanCollectionDataSource(dataSource));
	}

	@Override
	public void setSingleObjectDataSource(Object dataSource) {
		setCollectionDataSource(Arrays.asList(dataSource));
	}

	@Override
	public void setDataSource(Object dataSource) {
		if (dataSource instanceof JRDataSource) {
			this.dataSource = (JRDataSource) dataSource; 
		} else {
			this.dataSource = new JREmptyDataSource();
		}
	}

	@Override
    public JasperPrint getReportInstance() {
        try {
            return JasperFillManager.fillReport(jasperReport, parametros, dataSource);
        } catch (JRException ex) {
            throw new RuntimeException(ex);
        }
    }
	
	@Override
    public InputStream toInputStream(FormatoRelatorio formato) {
        return formato.getRelatorioExporter().exportarRelatorio(this);
    }
	
	@Override
	public File toFile(FormatoRelatorio formato) {
		
		try {
            return toFile(formato, Files.createTempDirectory("relatorios_temporarios"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
	}
	
	@Override
	public File toFile(FormatoRelatorio formato, Path diretorio) {
		try {
			File arquivoTemporario = Files.createTempFile(diretorio, "relatorio_", "." + formato.getExtension()).toFile();
			return toFile(formato, arquivoTemporario);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
    public File toFile(FormatoRelatorio formato, File file) {
        try {
            InputStream inputStream = toInputStream(formato);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
        
            IOUtils.copyStream(inputStream, fileOutputStream);
            
            inputStream.close();
            fileOutputStream.close();
        
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	public static RelatorioJasper criar(String arquivoJasper) {
		return criar(new File(arquivoJasper));
	}
	
	public static RelatorioJasper criar(File arquivoJasper) {
		return new RelatorioJasper(arquivoJasper);
	}

}
