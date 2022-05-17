package com.example.pdffeign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PdfFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfFeignApplication.class, args);
	}

}
