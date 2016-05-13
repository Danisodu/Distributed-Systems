# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 53 - Campus AL

Margarida Correia 78352 margarida.correia@ist.utl.pt  
Daniela Duarte 78542 danisodu@gmail.com  
Ana Cláudia Amorim 78576 ana-claudia1995@hotmail.com 



Repositório:
[tecnico-distsys/A_53-project](https://github.com/tecnico-distsys/A_53-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente  

[0] Iniciar sistema operativo    

Windows 8.1  
  
[1] Iniciar servidores de apoio  

JUDDI:  
 cd juddi-3.3.2_tomcat-7.0.64_9090/bin  
  startup.bat 
    
[2] Criar pasta temporária    
mkdir Projecto

[3] Obter código fonte do projeto (versão entregue)  

git clone https://github.com/tecnico-distsys/A_53-project/   

git checkout -b < tagname > (no nosso caso Project)
  
  
[4] Instalar módulos de bibliotecas auxiliares  
  
cd uddi-naming
mvn clean install  
(Usar uddi publicado no repositório do grupo - versão 1.1 com uma alteraçao: lookup retorna null quando não encontra nenhum
serviço publicado com o nome dado)


-------------------------------------------------------------------------------  

### Serviço TRANSPORTER    

[1] Construir e executar **servidor**    

cd transporter-ws  
mvn clean install  
mvn -Dws.i=X exec:java  
("Substituir X pelo número da transportadora que se pretende lançar")  

[2] Construir **cliente** e executar testes

cd transporter-ws-cli  
mvn clean install

-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor** primário

cd broker-ws  
mvn clean install    
mvn exec:java  

[2] Construir e executar **servidor** secundário  
Executar [1]  
  
[2] Construir **cliente** e executar testes (1)  
  
cd broker-ws-cli    
mvn clean install    
mvn exec:java   
  
(1) Para se verificara a salvaguarda do estado no broker secundário, correr cliente  
como mvn clean install -Dmaven.test.skip=true exec:java

-------------------------------------------------------------------------------
**FIM**
