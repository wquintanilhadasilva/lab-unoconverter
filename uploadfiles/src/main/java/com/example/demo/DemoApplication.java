package com.example.demo;

import feign.Response;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
@EnableFeignClients
@Slf4j
@RequiredArgsConstructor
public class DemoApplication implements CommandLineRunner {
	
	private final NFeUploadService nFeUploadService;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		String file = "C:\\dev\\lab\\uploadfiles\\DANFE_GUILHERME_COELHO_MACHADO.pdf";
		String authorization = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZjdlMzY0NThiYTUyM2RiZTc5NjUzMDEwYzEzYTYxZiIsImF1ZCI6IndlYiIsImV4cCI6MTY4Nzk1MzgyNywiaWF0IjoxNjg3ODY3NDI3fQ.rlFkYBBXruPfyyAn2tc1Tv6eDYinU6WnPgFU7ywtgdRnQSp2vK9imxh8q9i0jA9JAFxplpdkC98zFLJpPsN2kg";
		
		MultipartFile multipartFile = openFile(file);
		
		log.info("Enviando requisição");
		Response response = nFeUploadService.sentFile(authorization, multipartFile);
		log.info("Status da requisição: [{}]\n ===>> [{}]", response.status(), response.reason());
		if(response.status() == 200){
			log.info("Enviado com sucesso: [{}]", response.status());
			log.info(new String(response.body().asInputStream().readAllBytes()));
		} else {
			log.error("Erro na requisição para converter o arquivo: [{}] --> [{}]", file, response.reason());
		}
	}
	
	public MultipartFile openFile(String fileName) throws IOException {
		
		final String PREFIX = "stream2file";
		final String CONTENT_TYPE = "application/octect-stream";
		final String FIELD_NAME = "file";
		String SUFFIX = ".pdf";
		
		Pattern pattern = Pattern.compile("\\.([^.]+)$");
		Matcher matcher = pattern.matcher(fileName);
		
		if (matcher.find()) {
			String extension = matcher.group(1);
			
			Pattern patternContent = Pattern.compile("pdf|jpg|png");
			Matcher matcherContent = patternContent.matcher(extension);
			if (!matcherContent.find()) {
				throw new RuntimeException("Extensão não é do tipo válido: PDF, PNG ou JPG!");
			}
			SUFFIX = "." + extension;
		} else {
			throw new RuntimeException("Extensão do arquivo não encontrada!");
		}
		
		File tempFile = File.createTempFile(PREFIX, SUFFIX);
		tempFile.deleteOnExit();
		try {
			log.info("Abrindo arquivo para ser enviado...");
			InputStream input = new FileInputStream(fileName);
			DiskFileItem fileItem = new DiskFileItem(FIELD_NAME, CONTENT_TYPE, false, tempFile.getName(), (int) tempFile.length(), tempFile.getParentFile());
			OutputStream os = fileItem.getOutputStream();
			IOUtils.copy(input, os);
			MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
			return multipartFile;
		}catch (IOException ex) {
			log.error("Erro ao converter InputStream em MultipartFile", ex);
			throw ex;
		} finally {
			tempFile.delete();
		}
	}
	
	
}
