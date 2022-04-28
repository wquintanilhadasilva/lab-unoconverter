package com.pdf.example.pdfconvert;

import com.pdf.example.pdfconvert.application.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
	FileStorageProperties.class
})
public class PdfConvertApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfConvertApplication.class, args);
	}

}
