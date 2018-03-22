package br.com.flucas.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ApplicationCommandLine {

	public static CommandLine parse(String[] args) {
		boolean needHelp = false;
		
		Option arquivoJasper = Option.builder("f")
				.longOpt("jasperFile")
				.argName("arquivo")
				.hasArg()
				.valueSeparator()
				.desc("Caminho completo para o arquivo .jasper")
				.required().build();

		Option parametrosJasper = Option.builder("P")
				.argName("PARAMETRO=valor")
				.numberOfArgs(2)
				.valueSeparator()
				.desc("Parâmetro para ser enviado ao Jasper")
				.optionalArg(false).build();

		Option semDados = Option.builder("n")
				.longOpt("noData")
				.hasArg(false)
				.desc("Gerar relatório sem dados")
				.build();
		
		Option diretorioArquivo = Option.builder("l")
				.longOpt("outputLocation")
				.argName("diretorio")
				.hasArg()
				.valueSeparator()
				.desc("Pasta de destino onde será armazenado o relatório")
				.build();
		
		Option imprimirNomeArquivo = Option.builder()
				.longOpt("outputFileName")
				.hasArg(false)
				.desc("Flag que indica se deve gerar e salvar o relatorio para um arquivo temporario")
				.build();

		Options options = new Options();
		options.addOption(arquivoJasper);
		options.addOption(parametrosJasper);
		options.addOption(semDados);
		options.addOption(diretorioArquivo);
		options.addOption(imprimirNomeArquivo);
		options.addOption("h", "help", false, "Exibe essa mensagem de ajuda");

		CommandLineParser parser = new DefaultParser();
		CommandLine cli = null;
		
		try {
			cli = parser.parse(options, args);
			needHelp = cli.hasOption("help");
		} catch (ParseException e) {
			needHelp = true;
		}
		
		if (needHelp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("gerador-jasper", options);
			
			System.exit(1);
		}
		
		return cli;
	}

}
