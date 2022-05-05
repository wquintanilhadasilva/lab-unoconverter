# Laboratório UNOCONVERT

[https://github.com/unoconv/unoserver/blob/master/README.rst](https://github.com/unoconv/unoserver/blob/master/README.rst)


# Material de apoio:

URL: [https://github.com/unoconv/unoserver](https://github.com/unoconv/unoserver)

Apenas no protocolo [socket](https://github.com/unoconv/unoserver/blob/3e30d67387ebfa0041ec9e29a67b52ae0cd49d35/src/unoserver/server.py#L22)

[Artigo](https://ourcodeworld.com/articles/read/867/how-to-convert-a-word-file-to-pdf-docx-to-pdf-in-libreoffice-with-the-cli-in-ubuntu-2004) sobre utilização:

## Instalação local:

Atualize os pacotes do linux:
```
sudo apt-get update
```

Instale o libreoffice:

```
sudo apt-get install libreoffice --no-install-recommends
```

## Utilização

Comando para enviar um documento diretamente para o libreoffice:

```
libreoffice --headless --convert-to pdf <input-word-file.docx> --outdir </path/to/store/files>
```

Ou:

```
libreoffice --headless --convert-to pdf MyWordFile.docx --outdir ./
```


### Exemplo de conversão offline

```
libreoffice --headless --convert-to pdf "arquivo.docx" --outdir ./
```

Tipos de conversão:

```
--convert-to pdf:writer_pdf_Export
--convert-to pdf:calc_pdf_Export
--convert-to pdf:draw_pdf_Export
--convert-to pdf:impress_pdf_Export
--convert-to pdf:writer_web_pdf_Export
```


# ===>> CONVERT ONLINE <========

## Python install

Atualize e instale o python:

```
apt-get install python3-pip
```

## install server

Instale o unoserver:

```
sudo pip install unoserver
```


## server launch

Inicie o server para conversão:

```
unoserver
```

Ou inicie o server informando a interface e porta:

```
unoserver --interface 0.0.0.0 --port 2002
```


## convert file

Após isso, para converter os arquivos, basta usar:

```
unoconvert --convert-to pdf arquivo-origem.|docx|doc|xlsx|xls|pptx|ppt arquivo-destino.pdf
```

### Exemplos práticos:

```
unoconvert --convert-to pdf ./file-sample_100kB.doc tdoc.pdf
```

```
unoconvert --convert-to pdf ./file_example_PPT_250kB.ppt tppt.pdf
```

```
unoconvert --convert-to pdf ./samplepptx.pptx tpptx.pdf
```

```
unoconvert --convert-to pdf ./file_example_XLS_1000.xls txls.pdf
```

```
unoconvert --convert-to pdf ./file_example_XLSX_100.xlsx txlsx.pdf
```

```
unoconvert --convert-to pdf --interface 0.0.0.0 --port 2002 ./file_example_XLSX_100.xlsx txlsx.pdf
```

# Docker

Para gerar a imagem a partir do Dockerfile:

Compile a aplicação:

```
mvn clean package
```

Gere a imagem contendo o arquivo war da aplicação, compilado no comando anterior:

```
docker build -t pdf-convert:v1 .
```

Execute um container da imagem criada com a aplicação:

```
docker run -p 8081:8081 --rm --name="pdf-convert" pdf-convert:v1

```


# Usando o terminal

Após rodar a aplicação, pode utilizar o terminal para enviar arquivos para conversão.

Enviando um arquivo para converter em PDF:

```
curl --request POST --url http://localhost:8081/api/conversor/ --header 'content-type: multipart/form-data;' --form file=@./samples/lab-unoserver/file-sample_100kB.doc --output - > out.pdf

```

Enviando um comando para limpar os arquivos temporários no servidor:

```
curl --request DELETE \
  --url http://localhost:8081/api/conversor/file-sample_100kB.doc
```
