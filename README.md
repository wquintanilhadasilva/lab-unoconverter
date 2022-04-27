# Laboratório UNOCONVERT

[https://github.com/unoconv/unoserver/blob/master/README.rst](https://github.com/unoconv/unoserver/blob/master/README.rst)


## Material de apoio:

https://github.com/unoconv/unoserver/blob/3e30d67387ebfa0041ec9e29a67b52ae0cd49d35/src/unoserver/server.py#L22


```
https://github.com/unoconv/unoserver

https://ourcodeworld.com/articles/read/867/how-to-convert-a-word-file-to-pdf-docx-to-pdf-in-libreoffice-with-the-cli-in-ubuntu-2004

sudo apt-get update

sudo apt-get install libreoffice --no-install-recommends


libreoffice --headless --convert-to pdf <input-word-file.docx> --outdir </path/to/store/files>

libreoffice --headless --convert-to pdf MyWordFile.docx --outdir ./

## convert command line offline
libreoffice --headless --convert-to pdf "31-03-2022-UC021X_Manter_Expediente_Da_Iniciativa - REVISADO 2.docx" --outdir ./

--convert-to pdf:writer_pdf_Export
--convert-to pdf:calc_pdf_Export
--convert-to pdf:draw_pdf_Export
--convert-to pdf:impress_pdf_Export
--convert-to pdf:writer_web_pdf_Export


===>> CONVERT ONLINE <========

## Python install
apt-get install python3-pip

## install server
sudo pip install unoserver

## server launch
unoserver
unoserver --interface 0.0.0.0 --port 2002



## convert file


unoconvert --convert-to pdf ./31-03-2022-UC021X_Manter_Expediente_Da_Iniciativa\ -\ REVISADO\ 2.docx t.pdf

unoconvert --convert-to pdf ./file-sample_100kB.doc tdoc.pdf

unoconvert --convert-to pdf ./file_example_PPT_250kB.ppt tppt.pdf

unoconvert --convert-to pdf ./samplepptx.pptx tpptx.pdf

unoconvert --convert-to pdf ./file_example_XLS_1000.xls txls.pdf

unoconvert --convert-to pdf ./file_example_XLSX_100.xlsx txlsx.pdf

unoconvert --convert-to pdf --interface 0.0.0.0 --port 2002 ./file_example_XLSX_100.xlsx txlsx.pdf


## /mnt/dados/git/wedson.silva/lab-unoserver

!!! Só aceita o protocolo socket

curl -F "data=@file-sample_100kB.doc" https://localhost:9980/lool/convert-to/pdf > out.pdf

```

## Terminal

Converter um arquivo
```
curl --request POST   --url http://localhost:8081/api/conversor/   --header 'content-type: multipart/form-data;'   --form file=@/mnt/dados/git/wedson.silva/lab-unoserver/file-sample_100kB.doc

```

Limpar os arquivos temporários do server
```
curl --request DELETE \
  --url http://localhost:8081/api/conversor/file-sample_100kB.doc
```