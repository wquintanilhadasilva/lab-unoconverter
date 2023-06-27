package com.example.demo;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "nFeUploadService", url = "http://localhost:8081")
public interface NFeUploadService {

    @PostMapping(value = "/api/v1/nfe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Response sentFile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestPart("file") MultipartFile file);
}
