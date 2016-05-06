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

-------------------------------------------------------------------------------  

### Serviço TRANSPORTER    

[1] Construir e executar **servidor**    

cd transporter-ws  
mvn clean install  
mvn -Dws.i=X exec:java  
("Substituir X pelo número da transportadora que se quer lançar")  

[2] Construir **cliente** e executar testes

cd transporter-ws-cli  
mvn clean install

-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor**

cd broker-ws  
mvn clean install    
mvn exec:java  

[2] Construir **cliente** e executar testes

cd broker-ws-cli  
mvn clean install  
mvn exec:java  

-------------------------------------------------------------------------------
**FIM**
