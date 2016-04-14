package pt.upa.broker.ws;

import org.junit.*;
import static org.junit.Assert.*;
import mockit.*;



/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */


public class BrokerPortTest {

    // static members


    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }


    // members

    BrokerPort broker;

    TransportView transportview;

    // initialization and clean-up for each test

    @Before
    public void setUp() {

        broker = new BrokerPort();
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

    }

    @Test(expected = UnknownLocationFault_Exception.class)
    public void testUnknownDestinity() throws Exception{
            broker.requestTransport("Vila Real", "Amadora", 45);

    }

    @Test(expected = InvalidPriceFault_Exception.class)
    public void testNegativePrice() throws Exception{
            broker.requestTransport("Vila Real", "Portalegre", -45);

    }

    

}