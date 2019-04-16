# PocCameraAzure
 Garante 99.9% de disponibilidade, e caso ocorra algum problema, com base nos cálculos de atividade mensal, a Azure fornece um crédito de 10 a 25%. https://azure.microsoft.com/pt-br/support/legal/sla/cognitive-services/v1_1/

Documentação de fácil entendimento

Fácil implementação, uma vez que os métodos estão disponibilizadas em uma Api rest e bem documentados

Duas requisições, uma para detecção e outra para a verificação

Não possui SDK, a detecção deve ser feita através de recursos nativos de cada sistema operacional IOS/Android, a vantagem disso é que para cada atualização do sistema operacional, a equipe de desenvolvimento consegue dimensionar o impacto sobre a funcionalidade. 

No ambiente mobile, não há como delimitar o tamanho da imagem de uma foto, ou seja, a foto sempre será enviada com a melhor resolução do dispositivo, e isso impacta em dois pontos. 1 - Se a imagem for muito grande, a requisição pode demorar e consumir grande parte da banda de rede. 2 - O serviço da Azure não aceita uma imagem muito grande, pois exige que seja enviado um array de bytes no corpo da requisição nos retornando erro 400 (BadRequest). A solução para isso é limitar o tamanho da imagem na fonte
