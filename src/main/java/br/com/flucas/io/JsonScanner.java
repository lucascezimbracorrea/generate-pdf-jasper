package br.com.flucas.io;

import java.io.InputStream;
import java.util.Scanner;

import br.com.flucas.util.StringUtils;

public class JsonScanner {

	private final Scanner scanner;
	
	private boolean jsonRead = false;
	private String jsonString = "";
	
	public JsonScanner(InputStream in) {
		this.scanner = new Scanner(in);
	}

	public boolean hasJson() {
		if (!jsonRead) {
			readNextJson();
		}
		
		return !jsonString.trim().isEmpty();
	}

	public String nextJsonString() {
		try {
			if (jsonRead) {
				return jsonString;
			}
			
			readNextJson();
			return jsonString;			
		} finally {
			jsonRead = false;
		}
	}
	
	private void readNextJson() {
		StringBuilder json = new StringBuilder();
		
		String linha = null;
		String jsonAteAgora = null;
		
		while ((linha = scanner.nextLine()) != null) {
			json.append(linha).append("\n");
			jsonAteAgora = json.toString();
			
			if (jsonAteAgora.trim().isEmpty() && jsonAteAgora.endsWith("\n\n")) {
				break;
			}
			
			int chavesAbertas = StringUtils.countOccurences(jsonAteAgora, "{");
			int chavesFechadas = StringUtils.countOccurences(jsonAteAgora, "}");

			if (chavesAbertas == chavesFechadas) {
				break;
			}
		}
		
		jsonRead = true;
		jsonString = json.toString();
	}
	
}
