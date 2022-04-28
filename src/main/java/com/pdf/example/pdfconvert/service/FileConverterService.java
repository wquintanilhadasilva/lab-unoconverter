package com.pdf.example.pdfconvert.service;

import com.pdf.example.pdfconvert.exception.FileStorageException;
import com.pdf.example.pdfconvert.application.FileStorageProperties;
import com.pdf.example.pdfconvert.exception.MyFileNotFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    public Resource convert(MultipartFile file) {

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

            // Convert to PDF
            boolean ok = this.unoConvertService.convert(fileName, fileNamePDF);
            if(ok){
                Resource resource = this.loadFileAsResource(fileNamePDF);

                //Delete temp files conversion used
                this.deleteFiles(fileName);
                this.deleteFiles(fileNamePDF);

                return resource;
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
            byte[] data = Files.readAllBytes(filePath.toAbsolutePath());
            Resource resource = new ByteArrayResource(data);
            return resource;
        } catch (IOException e) {
            throw new MyFileNotFoundException("File not found " + fileName, e);
        }
    }

    public void deleteFiles(String fileName) {

        try {

            String fileNamePDF = fileName.substring(0, fileName.lastIndexOf('.')) + ".pdf";

            Path fromFile = this.fileStorageLocation.resolve(fileName);
            Path toFile = this.fileStorageLocation.resolve(fileNamePDF);
            if(Files.exists(fromFile)){
                Files.delete(fromFile);
            }
            if(Files.exists(toFile)){
                Files.delete(toFile);
            }

        } catch (IOException ex) {
            throw new MyFileNotFoundException("File not deleted " + fileName, ex);
        }

    }

}
