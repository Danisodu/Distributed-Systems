package pt.upa.broker.ws;

import org.junit.*;
import static org.junit.Assert.*;

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

    BrokerJob brokerjob;

    // initialization and clean-up for each test

    @Before
    public void setUp() {

        brokerjob = new BrokerJob("2", "Porto","Beja", 58, JobState.ACCEPTED);
    }

    @After
    public void tearDown() {
        brokerjob = null;
    }


    // tests

    @Test(expected = UnknownLocationFault_Exception)
    public void testUnknownOrigin() throws Exception{
            brokerjob.requestTransport("Barreiro", "Faro", 45);

    }

    @Test(expected = UnknownLocationFault_Exception)
    public void testUnknownDestinity() throws Exception{
            brokerjob.requestTransport("Vila Real", "Amadora", 45);

    }

}