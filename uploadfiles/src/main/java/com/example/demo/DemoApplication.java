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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
		
		String file = "C:\\dev\\lab\\uploadfiles\\DANFE_GUILHERME_COELHO_MACHADO.pdf";
		String authorization = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZjdlMzY0NThiYTUyM2RiZTc5NjUzMDEwYzEzYTYxZiIsImF1ZCI6IndlYiIsImV4cCI6MTY4Nzk1MzgyNywiaWF0IjoxNjg3ODY3NDI3fQ.rlFkYBBXruPfyyAn2tc1Tv6eDYinU6WnPgFU7ywtgdRnQSp2vK9imxh8q9i0jA9JAFxplpdkC98zFLJpPsN2kg";
		
		String folderPath = "G:\\Meu Drive\\samsung\\danfes\\prd";
		var fileNames = listFiles(folderPath);
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		fileNames.parallelStream().forEach(fileName -> {
			executorService.execute(() -> {
				try {
					enviarArquivo(authorization, folderPath + "/" + fileName);
				} catch (IOException e) {
					e.printStackTrace();
					log.error("Erro ao processar...", e);
				}
			});
		});
		executorService.shutdown();
	}
	
	private void enviarArquivo(String authorizationHeader, String filePath) throws IOException {
		// Implemente o método para enviar o arquivo com base na lógica desejada.
		log.info("[{}] - [{}]", authorizationHeader, filePath);
		MultipartFile multipartFile = openFile(filePath);
		
		log.info("Enviando requisição");
		Response response = nFeUploadService.sentFile(authorizationHeader, multipartFile);
		log.info("Status da requisição: [{}]\n ===>> [{}]", response.status(), response.reason());
		if(response.status() == 200){
			log.info("::OK:: Arquivo [{}] --> [{}]", filePath, response.status());
			log.info(new String(response.body().asInputStream().readAllBytes()));
		} else {
			log.error("::ERRO:: Arquivo: [{}] --> [{}]", filePath, response.reason());
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
