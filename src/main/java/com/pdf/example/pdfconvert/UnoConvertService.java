package com.pdf.example.pdfconvert;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class UnoConvertService {

    private final FileStorageProperties fileStorageProperties;

    public boolean convert(String fileNameFrom, String fileNameTo) {
        // unoconvert --convert-to pdf --interface 0.0.0.0 --port 2002 ./file_example_XLSX_100.xlsx txlsx.pdf
        String filePath = fileStorageProperties.getUploadDir();

        String arquivoOrigem = String.format("%s/%s", filePath, fileNameFrom);
        String arquivoDestino = String.format("%s/%s", filePath, fileNameTo);

        Runtime rt = Runtime.getRuntime();
        try {
            String commando = String.format("unoconvert --convert-to pdf --interface 0.0.0.0 --port 2002 %s %s", arquivoOrigem, arquivoDestino);
            Process pr = rt.exec(commando);
            pr.waitFor();
            return pr.exitValue() == 0;
        } catch (IOException e) {
            throw new RuntimeException(String.format("Problema ao converter o arquivo %s em PDF.", fileNameFrom), e);
        } catch (InterruptedException e) {
            throw new RuntimeException(String.format("Problema ao converter o arquivo %s em PDF.", fileNameFrom), e);
        }


    }

}
