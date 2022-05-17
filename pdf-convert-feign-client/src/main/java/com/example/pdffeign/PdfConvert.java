package com.example.pdffeign;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "pdfConvert", url = "${application.feign.fileconverter.url}")
public interface PdfConvert {

    @PostMapping(value = "/api/conversor/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Response getPdf(@RequestPart("file") MultipartFile file);
}
