package pt.upa.transporter.ws;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class TransporterPortTest {

    // static members

    // one-time initialization and clean-up

    @BeforeClass //pode ser usado para fazer a ligação com o servidor
    public static void oneTimeSetUp() {

    }

    @AfterClass //depois de realizar o teste posso querer limpar as viagens todas que existem, por exemplo
    public static void oneTimeTearDown() {

    }

    // members

    private TransporterPort transporter;
    private TransporterJob transporterjob;

    // initialization and clean-up for each test

    @Before
    public void setUp() {
        transporter = new TransporterPort(1, "UpaTransporter1");
        transporterjob = new TransporterJob("UpaTransporter1", "1", "Castelo Branco", "Faro", 60, JobState.ACCEPTED);
    }

    @After
    public void tearDown() {
        transporter = null;
    }

    //testes individuais ao RequestJob
    @Test(expected = BadPriceFault_Exception.class)
    public void testRequestJobNegativePrice() {
      
        transporter.requestJob("Lisboa", "Leiria", -3) ;
       
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownOrigin(){
  
        transporter.requestJob("Madrid", "Leiria", 3);  
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownDestination(){

        transporter.requestJob("Lisboa", "Madrid", 3);        
    }

    @Test(expected = BadJobFault_Exception.class)
    public void testImparIDSouth(){

        transporter.requestJob("Braga", "Leiria", 3);        
    }

    @Test(expected = BadJobFault_Exception.class)
    public void testParIDNorth(){
        transporter.setTransporterIdentifier(2);
        transporter.requestJob("Braga", "Setúbal", 3);        
    }

    @Test 
    public void testPriceGreaterThan100(){
        JobView jobview = transporter.requestJob("Braga", "Lisboa", 150);    
        assertEquals(null, jobview);    
    }

    @Test 
    public void testPriceMinorThan10(){
        JobView jobview = transporter.requestJob("Braga", "Lisboa", 8);   
        assertTrue(0<jobview.getJobPrice()&&jobview.getJobPrice()<8);    
    }

    @Test 
    public void testImparPriceImparID(){
        transporter.setTransporterIdentifier(1);
        JobView jobview = transporter.requestJob("Setúbal", "Lisboa", 31);   
        assertTrue(10<jobview.getJobPrice()&&jobview.getJobPrice()<31);    
    }

    @Test 
    public void testImparPriceParID(){
        transporter.setTransporterIdentifier(2);
        JobView jobview = transporter.requestJob("Braga", "Lisboa", 31);   
        assertTrue(31<jobview.getJobPrice()&&jobview.getJobPrice()<100);    
    }

    @Test 
    public void testParPriceParID(){
        transporter.setTransporterIdentifier(2);
        JobView jobview = transporter.requestJob("Braga", "Lisboa", 30);   
        assertTrue(10<jobview.getJobPrice()&&jobview.getJobPrice()<30);    
    }

    @Test 
    public void testParPriceImparID(){
        transporter.setTransporterIdentifier(1);
        JobView jobview = transporter.requestJob("Setúbal", "Lisboa", 30);   
        assertTrue(31<jobview.getJobPrice()&&jobview.getJobPrice()<100);    
    }

    //testes individuais ao decideJob

   
    /*@Test 
    public void testRejectJobView(){
        transporterjob = transporter.decideJob0("1", false);
        JobState js;
        js = transporterjob.getState();
        assertEquals(REJECTED, js);

    }   

    @Test 
    public void testAcceptJobView(){
        transporterjob.setState(REJECTED);
*/

        
    }   

    //testes individuais ao JobStatus


    //teste ao ping


    //teste ao listJobs


    //teste ao clearJobs
