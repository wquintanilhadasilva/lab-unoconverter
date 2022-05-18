package com.example.pdffeign;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

@RestController()
@Slf4j
@RequestMapping("/client/teste")
@RequiredArgsConstructor
public class TesteConversorController {

    private final PdfConvert pdfConvert;

    @Value("${application.file.pdf}")
    private String localToWrite;

    @GetMapping()
    public ResponseEntity<Void> teste(@RequestParam(value = "file", required = true) String file) throws IOException {

        log.info("Enviando requisição");
        MultipartFile multipartFile = openFile(file);
        Response response = pdfConvert.getPdf(multipartFile);

        log.info(String.format("Status da requisição: [%s]\n [%s]", response.status(), response.reason()));
        if(response.status() == 200){
            InputStream inputStream = response.body().asInputStream();
            Path fileout = Path.of(localToWrite);
            Files.copy(inputStream, fileout, StandardCopyOption.REPLACE_EXISTING);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            log.error(String.format("Erro na requisição para converter o arquivo: [%s] --> [%s]", file, response.reason()));
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }

    public MultipartFile openFile(String fileName) throws IOException {
        final String PREFIX = "stream2file";
        final String SUFFIX = ".tmp";
        final String CONTENT_TYPE = "text/plain";
        final String FIELD_NAME = "file";

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
        // log.info("Abrindo arquivo para ser enviado...");
        // File file = new File(fileName);
        // InputStream input =  new FileInputStream(file);
        // DiskFileItem fileItem = new DiskFileItem("file",  "text/plain", false, file.getName(), (int) file.length() , file.getParentFile());
        // OutputStream os = fileItem.getOutputStream();
        // IOUtils.copy(input, os);
        // MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        // return multipartFile;
    }


}
