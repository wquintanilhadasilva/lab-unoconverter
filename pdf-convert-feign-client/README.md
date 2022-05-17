# Laboratório PDF Convert

Client para consumir o serviço de conversão PDF

## Usando o terminal

Após rodar a aplicação, pode utilizar o terminal para enviar arquivos para conversão.

Enviando um arquivo para converter em PDF:

>  A aplicação cliente contém uma configuração em application.yml indicando o local e o nome para gravar o pdf gerado.

```
curl --request GET \
  --url 'http://localhost:8082/client/teste?file={caminho e nome do arquivo a ser convertido}'

```

Exemplo:

```
curl --request GET \
  --url 'http://localhost:8082/client/teste?file=%2Fmnt%2Fdados%2Fgit%2Fwedson.silva%2Fpdf-convert%2Fsamples%2FCADE_LOREM_DOCUMENTO_FUNDAMENTA%C3%87AO_SPRINT2.docx'
```

O valor do parâmetro *file* contém o caminho completo e o nome do arquivo a ser enviado para conversão.

> Esta aplicação spring-boot disponibiliza um endpoint REST API que pode ser invocado por qualquer aplicação ou client http.
