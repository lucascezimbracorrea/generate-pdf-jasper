package br.com.flucas.relatorio;

public enum FormatoRelatorio {
	
	PDF("pdf", new RelatorioPdfExporter()),
	;
	
	private final String extension;
	private final RelatorioExporter exporter;

	private FormatoRelatorio(String extension, RelatorioExporter exporter) {
		this.extension = extension;
		this.exporter = exporter;
	}

	public String getExtension() {
		return extension;
	}

	public RelatorioExporter getRelatorioExporter() {
		return exporter;
	}
	
}
