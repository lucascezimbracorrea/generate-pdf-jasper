package br.com.flucas.relatorio;

import java.io.InputStream;

public interface RelatorioExporter {

	public InputStream exportarRelatorio(Relatorio relatorio);
	
}
