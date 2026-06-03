package com.empresa.portal_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot.
 * 
 * Esta clase contiene el método main que inicia la aplicación del portal interno.
 */
@SpringBootApplication
public class PortalBackendApplication {

	/**
	 * Método principal que inicia la aplicación Spring Boot.
	 *
	 * @param args Argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(PortalBackendApplication.class, args);
	}

}
