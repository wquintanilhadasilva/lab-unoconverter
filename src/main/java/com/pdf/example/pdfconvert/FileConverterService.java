package com.pdf.example.pdfconvert;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileConverterService {

    private final Path fileStorageLocation;

    private final UnoConvertService unoConvertService;

    public FileConverterService(FileStorageProperties fileStorageProperties, UnoConvertService unoConvertService) {

        this.unoConvertService = unoConvertService;

        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Resource storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        String fileNamePDF = fileName.substring(0, fileName.lastIndexOf('.')) + ".pdf";

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            boolean ok = this.unoConvertService.convert(fileName, fileNamePDF);
            if(ok){
                Resource rs = this.loadFileAsResource(fileNamePDF);
                return rs;
            }else {
                throw new FileStorageException("Could not convert file " + fileName + ". Please try again!");
            }

        } catch (IOException ex) {
            throw new FileStorageException("Could not convert file " + fileName + ". Please try again!", ex);
        }
    }

    private Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

    public void deleteFiles(String fileName) {

        try {

            String fileNamePDF = fileName.substring(0, fileName.lastIndexOf('.')) + ".pdf";

            Path arquivoOrigem = this.fileStorageLocation.resolve(fileName);
            Path arquivoDestino = this.fileStorageLocation.resolve(fileNamePDF);
            if(Files.exists(arquivoOrigem)){
                Files.delete(arquivoOrigem);
            }
            if(Files.exists(arquivoDestino)){
                Files.delete(arquivoDestino);
            }

        } catch (IOException ex) {
            throw new MyFileNotFoundException("File not deleted " + fileName, ex);
        }

    }

}
