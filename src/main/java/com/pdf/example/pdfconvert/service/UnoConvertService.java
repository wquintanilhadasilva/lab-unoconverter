package com.pdf.example.pdfconvert.service;

import com.pdf.example.pdfconvert.application.FileStorageProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UnoConvertService {

    private final FileStorageProperties fileStorageProperties;

    public boolean convert(String fileNameFrom, String fileNameTo) {

        // unoconvert --convert-to pdf --interface 0.0.0.0 --port 2002 ./file_example_XLSX_100.xlsx txlsx.pdf
        String filePath = fileStorageProperties.getUploadDir();

        String fromFile = String.format("%s/%s", filePath, fileNameFrom);
        String toFile = String.format("%s/%s", filePath, fileNameTo);

        log.info(String.format("Convertendo arquivo origem [%s]", fromFile));
        log.info(String.format("Convertendo arquivo destino [%s]", toFile));

        Runtime rt = Runtime.getRuntime();
        try {
            String commando = String.format("unoconvert --convert-to pdf --interface 0.0.0.0 --port 2002 %s %s", fromFile, toFile);
            log.debug(String.format("Chamando conversor libreoffice [%s]", commando));
            Process pr = rt.exec(commando);
            pr.waitFor();
            log.debug(String.format("Resultado da conversão exitValue: [%s]", pr.exitValue()));
            return pr.exitValue() == 0;
        } catch (Exception e) {
            log.error(String.format("Problema ao converter o arquivo %s em PDF.", fileNameFrom), e);
            throw new RuntimeException(String.format("Problema ao converter o arquivo %s em PDF.", fileNameFrom), e);
        }


    }

}
