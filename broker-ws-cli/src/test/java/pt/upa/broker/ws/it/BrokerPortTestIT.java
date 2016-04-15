package pt.upa.broker.ws.it;

import org.junit.*;
import static org.junit.Assert.*;

import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.cli.BrokerClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;



/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class BrokerPortTestIT {

    // static members


    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }


    // members

    private BrokerClient brokerclient;

   TransportView transportview;

    // initialization and clean-up for each test

    @Before
    public void setUp() {

        brokerclient = new BrokerClient("http://localhost:9090", "UpaBroker");
  
    }

    @After
    public void tearDown() {
        brokerclient = null;
    }


    // testes ao requestJob

    @Test(expected = UnknownLocationFault_Exception.class)
    public void testUnknownOrigin() throws Exception{
            brokerclient.requestTransport("Barreiro", "Faro", 45);
            brokerclient.clearTransports();

    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void testUnknownDestinity() throws Exception{
            brokerclient.requestTransport("Vila Real", "Amadora", 45);
            brokerclient.clearTransports();

    }

    @Test(expected = InvalidPriceFault_Exception.class)
    public void testNegativePrice() throws Exception{
            brokerclient.requestTransport("Vila Real", "Portalegre", -45);
            brokerclient.clearTransports();

    }


    @Test(expected = UnavailableTransportFault_Exception.class)
    public void testUnavailableTransport() throws Exception{
            brokerclient.requestTransport("Lisboa", "Leiria", 200);
            brokerclient.clearTransports();
            // nao ha transportes disponiveis se consoante o pedido nao ha ofertas de nenhuma das transportadoras
    }

    @Test(expected = UnavailableTransportPriceFault_Exception.class)
    public void testUnavailablePriceTransport() throws Exception{

            //vamos tratar este caso manipulando o resquestJob da transporter
            //UpaTransporter1 --> ID impar
            //preço par para mandar acima do preço  ---> manda proposta acima do preço  ---> broker rejeita

            brokerclient.requestTransport("Lisboa", "Leiria", 30);

            brokerclient.clearTransports();
            
    }

    @Test 
    public void testImparPriceImparID() throws Exception{
          
        assertNotNull(brokerclient.requestTransport("Leiria", "Lisboa", 31));
        brokerclient.clearTransports();


       
    } 

     //teste ao clearTransports

    @Test 
    public void cleartransports() {

        brokerclient.clearTransports();
        assertTrue(brokerclient.listTransports().isEmpty());

        
    } 


    //deviamos lançar outra transportadora e testar se retorna mesmo a melhor oferta


    


    //testes ao viewtransport   ---> rrecebe o id do brokerjob (que esta na lista)

    @Test(expected = UnknownTransportFault_Exception.class)
    public void testIDTransportUnknown() throws Exception{

          
          brokerclient.viewTransport("3");  

          // nao ha transporte nenhum transporte com esse id por exemplo
    }
    
    //teste ao ping

    @Test 
    public void testPing() {
        
        assertNotNull(brokerclient.ping("Olá"));
        
    }  

    
    //teste ao listTransports

    @Test
    public void testlisttransports() throws Exception{
          


          String id= brokerclient.requestTransport("Leiria", "Lisboa", 31);
                    
          assertNotNull(brokerclient.listTransports());  

         
    }

}