package br.com.flucas.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

	public static void copyStream(InputStream source, OutputStream destination) {
        try {
            byte buffer [] = new byte[2048];
            
            int tamanhoBufferLido;
        
            while ((tamanhoBufferLido = source.read(buffer)) > 0) {
                destination.write(buffer, 0 , tamanhoBufferLido);
            }
            
            destination.flush();
            
            buffer = null;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
	
}
