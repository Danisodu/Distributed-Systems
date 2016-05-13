package pt.upa.broker.ws;

import org.junit.*;
import static org.junit.Assert.*;
import mockit.*;



/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" mmlife-cycle phase 
 *  If necessary, should invoke "mock" remote servers 
 */


public class BrokerPortTest {
/*
    // static members


    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }

    // members

    private BrokerPort broker;

    private TransportView transportview;

    // initialization and clean-up for each test

    @Before
    public void setUp() {

        broker = new BrokerPort(1);
        broker.initHandlersSearch();
    }

    @After
    public void tearDown() {
        broker = null;
    }


    // testes ao requestJob

    @Test(expected = UnknownLocationFault_Exception.class)
    public void testUnknownOrigin() throws Exception{
            broker.requestTransport("Barreiro", "Faro", 45);
             broker.clearTransports();
    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void testUnknownDestinity() throws Exception{
            broker.requestTransport("Vila Real", "Amadora", 45);
            broker.clearTransports();
    }

    @Test(expected = InvalidPriceFault_Exception.class)
    public void testNegativePrice() throws Exception{
            broker.requestTransport("Vila Real", "Portalegre", -45);
             broker.clearTransports();

    }


    @Test(expected = UnavailableTransportFault_Exception.class)
    public void testUnavailableTransport() throws Exception{
            broker.requestTransport("Lisboa", "Leiria", 200);
            broker.clearTransports();
            // nao ha transportes disponiveis se consoante o pedido nao ha ofertas de nenhuma das transportadoras
    }
    
    /*
    @Test(expected = UnavailableTransportPriceFault_Exception.class)
    public void testUnavailablePriceTransport() throws Exception{

            //vamos tratar este caso manipulando o resquestJob da transporter
            //UpaTransporter1 --> ID impar
            //preço par para mandar acima do preço  ---> manda proposta acima do preço  ---> broker rejeita

            broker.requestTransport("Lisboa", "Leiria", 30);

            broker.clearTransports();
            
    }

    @Test 
    public void testImparPriceImparID() throws Exception{
          
        assertNotNull(broker.requestTransport("Leiria", "Lisboa", 31));
         broker.clearTransports();


       
    } 

     //teste ao clearTransports

    @Test 
    public void cleartransports() {

        broker.clearTransports();
        assertTrue(broker.listTransports().isEmpty());

        
    } 


    //deviamos lançar outra transportadora e testar se retorna mesmo a melhor oferta


    


    //testes ao viewtransport   ---> rrecebe o id do brokerjob (que esta na lista)

    @Test(expected = UnknownTransportFault_Exception.class)
    public void testIDTransportUnknown() throws Exception{

          
          broker.viewTransport("3");  

          // nao ha transporte nenhum transporte com esse id por exemplo
    }
    
    //teste ao ping

    @Test 
    public void testPing() {
        
        assertNotNull(broker.ping("Olá"));
        
    }  

    
    //teste ao listTransports

    @Test
    public void testlisttransports() throws Exception{
          

          String id = broker.requestTransport("Castelo Branco", "Lisboa", 31);
          assertNotNull(broker.listTransports());  
          broker.clearTransports();

    }
*/
}