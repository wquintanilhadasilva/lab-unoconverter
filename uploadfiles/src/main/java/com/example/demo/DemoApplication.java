package com.example.demo;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		
		String file = "DANFE_GUILHERME_COELHO_MACHADO.pdf";
		String authorization = "Bearer eyJhbGciOiJIUzUxMiJ9..rlFkYBBXruPfyyAn2tc1Tv6eDYinU6WnPgFU7ywtgdRnQSp2vK9imxh8q9i0jA9JAFxplpdkC98zFLJpPsN2kg";

		var fileNames = listFiles("C:\\dev\\lab\\uploadfiles");
		// Imprimir os nomes dos arquivos
		fileNames.forEach(System.out::println);
		
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

	private List<String> listFiles(String directoryPath) {
		try (Stream<Path> filesStream = Files.list(Paths.get(directoryPath))) {
			List<String> fileNames = filesStream
					.filter(Files::isRegularFile)
					.map(Path::getFileName)
					.map(Path::toString)
					.collect(Collectors.toList());
			return fileNames;
		} catch (IOException e) {
			log.error("Erro ==>", e);
			throw new RuntimeException(e);
		}
	}
	
	
}
