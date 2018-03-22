package main;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;

import br.com.flucas.cli.ApplicationCommandLine;
import br.com.flucas.io.JsonScanner;
import br.com.flucas.relatorio.FormatoRelatorio;
import br.com.flucas.relatorio.Relatorio;
import br.com.flucas.relatorio.RelatorioJasper;
import br.com.flucas.util.IOUtils;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JsonDataSource;

public class Main {

	public static void main(String[] args) {
		try {
			
			gerarRelatorio(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void gerarRelatorio(String[] args) throws JRException, URISyntaxException {
		CommandLine cli = ApplicationCommandLine.parse(args);
		
		String arquivo = cli.getOptionValue("jasperFile");
		Properties parametros = cli.getOptionProperties("P");
		
		JRDataSource dataSource = new JREmptyDataSource();
		
		if (!cli.hasOption("noData")) {
			JsonScanner scanner = new JsonScanner(System.in);
			
			if (scanner.hasJson()) {
				InputStream stream = new ByteArrayInputStream(scanner.nextJsonString().getBytes(StandardCharsets.UTF_8));
				dataSource = new JsonDataSource(stream);
			}
		}
		
		Relatorio relatorio = RelatorioJasper.criar(arquivo);
		relatorio.addParametros(parametros);
		relatorio.setDataSource(dataSource);
		
		outputRelatorio(cli, relatorio);
		
	}
	
	private static void outputRelatorio(CommandLine cli, Relatorio relatorio) throws URISyntaxException {
		if (cli.hasOption("outputLocation")) {
			Path caminhoArquivo = Paths.get(cli.getOptionValue("outputLocation"));
			File arquivoRelatorioGerado = relatorio.toFile(FormatoRelatorio.PDF, caminhoArquivo);
			
			System.out.println(arquivoRelatorioGerado);
			return;
		}
		
		if (cli.hasOption("outputFileName")) {
			File arquivoRelatorioGerado = relatorio.toFile(FormatoRelatorio.PDF);
			
			System.out.println(arquivoRelatorioGerado);
			return;
		}
		
		InputStream source = relatorio.toInputStream(FormatoRelatorio.PDF);
		IOUtils.copyStream(source, System.out);
	}

}
