package main;

import jasper.ReportManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import br.com.flucas.relatorio.FormatoRelatorio;
import br.com.flucas.relatorio.Relatorio;
import br.com.flucas.relatorio.RelatorioJasper;
import br.com.flucas.util.StringUtils;

public class Main {

	public static void main(String[] args) throws JRException {
		Option arquivoJasper = Option.builder("f")
									.longOpt("jasper.file")
									.argName("arquivo")
									.hasArg()
									.valueSeparator()
									.desc("Caminho completo para o arquivo .jasper")
									.required()
									.build();
		
		Option parametrosJasper = Option.builder("P")
										.argName("PARAMETRO=valor")
										.numberOfArgs(2)
										.valueSeparator()
										.desc("Parâmetro para ser enviado ao Jasper")
										.optionalArg(false)
										.build();
		
		Option semDados = Option.builder("n")
								.longOpt("no-data")
								.hasArg(false)
								.desc("Gerar relatório sem dados")
								.build();
		
		Options options = new Options();
		options.addOption(arquivoJasper);
		options.addOption(parametrosJasper);
		options.addOption(semDados);
		options.addOption("h", "help", false, "Exibe essa mensagem de ajuda");
		
		
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cli = null;
		
		boolean needHelp = false;
		
		try {
			cli = parser.parse(options, args);
		} catch (ParseException e1) {
			needHelp = true;
		}
		
		if (cli.hasOption("help")) {
			needHelp = true;
		}
		
		if (needHelp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("teste", options);
			
			System.exit(1);
		}
		
		String arquivo = cli.getOptionValue("jasper.file");
		Properties parametros = cli.getOptionProperties("P");
		
		System.out.println(arquivo);
		System.out.println(parametros.toString());
		
		JRDataSource dataSource = new JREmptyDataSource();
		
		if (!cli.hasOption("no-data")) {
			Scanner scanner = new Scanner(System.in);
			StringBuilder dadosString = new StringBuilder();
			String linha = null;
			
			while ((linha = scanner.nextLine()) != null) {
				dadosString.append(linha).append("\n");
				String jsonAtual = dadosString.toString();
				
				if (jsonAtual.endsWith("\n\n")) {
					break;
				}
				
				if (jsonAtual.trim().isEmpty()) {
					continue;
				}
				
				int chavesAbertas = StringUtils.countOccurences(jsonAtual, "{");
				int chavesFechadas = StringUtils.countOccurences(jsonAtual, "}");

				if (chavesAbertas == chavesFechadas) {
					break;
				}
			}
			
			if (!dadosString.toString().trim().isEmpty()) {
				System.out.println(dadosString);
				
				InputStream stream = new ByteArrayInputStream(dadosString.toString().getBytes(StandardCharsets.UTF_8));
				dataSource = new JsonDataSource(stream);
			}
		}
		
		Relatorio relatorio = RelatorioJasper.criar(arquivo);
		relatorio.addParametros(parametros);
		relatorio.setDataSource(dataSource);
		
		File resultadoRelatorio = relatorio.toFile(FormatoRelatorio.PDF);
		
		System.out.println(resultadoRelatorio);
		
		if (1 == 1) {
			return;
		}
		
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
