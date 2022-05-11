package com.pdf.example.pdfconvert.web;

import com.pdf.example.pdfconvert.service.FileConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController()
@Slf4j
@RequestMapping("/api/conversor")
@RequiredArgsConstructor
public class ConversorController {

    private final FileConverterService fileStorageService;

    @PostMapping()
    public ResponseEntity<Resource> uploadFile(@RequestParam("file") MultipartFile file) {

        Resource resource = fileStorageService.convert(file);

        String contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{file}")
    public ResponseEntity<Void> delete(@PathVariable("file") String fileName) {
        fileStorageService.deleteFiles(fileName);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
