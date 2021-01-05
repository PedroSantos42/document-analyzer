# Proposta do App

Dado o requisito para um sistema de processamento de documentos com as seguintes características:

1. O sistema deverá suportar diferentes configurações de envio, onde serão determinados os operadores para aquela configuração, quais tipos de arquivo(txt, pdf, doc, imagens...) suportados e quais aplicações de captura permitidas (mobile, desktop, web):
    - Requisições de configuração de envios só poderão ser processadas se forem originadas por operador administrador;
    - O processamento do envio só poderá ser realizado se o operador tiver permissão de envio para o tipo de arquivo e para envios da aplicação de captura em questão (o tipo de arquivo e aplicação de captura podem vir informadas na requisição de envio).

2. Administradores poderão consultar informações que cruzem dados de configuração de envios, aplicações de captura, tipo de arquivo e data:
    - Assuma os campos destacados acima como filtros e retorne a lista de envios resultante.

3. Deverão ser suportados os seguintes tipos de arquivos para processamento: txt, pdf, doc, docx, imagem(jpg, png, gif). O objetivo do processamento é contar o total de ocorrências de cada palavra do documento:
    - Modele e implemente levando em consideração que ao longo do tempo o processamento de novos tipos de arquivos serão requisitados;
    - É permitido uso de libs disponíveis para auxiliar na leitura dos arquivos;
    - No caso dos arquivos de imagens, implemente um mock que dado um binário invoca um serviço externo de conversão/leitura da imagem em texto, use o retorno desse mock para efetuar a contagem.
  
4. Deverá ser fornecido como saída um arquivo de planilha excel, individual por envio, recuperável via endpoint para download com os resultados do processamento.

### Diante disso: defina o modelo de dados e implementa uma API Rest para esta demanda

É importante que você:
    1. Implemente a persistência de dados para Banco de Dados Relacional;
    2. Procure fazer um código limpo e testável alcançando 90% de cobertura no mínimo;
    3. Java 8 ou superior;
    4. Use o Lombok.

Será avaliado:
    1. A organização da solução;
    2. A utilização das API's nativas e de terceiros;
    3. Boas práticas de desenvolvimento (Clean Code e SOLID);
    4. Arquitetura e cobertura.
