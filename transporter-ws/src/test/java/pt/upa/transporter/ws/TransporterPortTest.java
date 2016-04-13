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
    private JobView jobview;

    // initialization and clean-up for each test

    @Before
    public void setUp() {
        transporter = new TransporterPort("UpaTransporter1");
        JobView jobview;
        //jobview = transporter.convertJob(new TransporterJob("UpaTransporter1", "1", "Castelo Branco", "Faro", 60, JobState.PROPOSED));
    }

    @After
    public void tearDown() {
        transporter = null;
        jobview=null;
    }

    //testes individuais ao RequestJob
    @Test(expected = BadPriceFault_Exception.class)
    public void testRequestJobNegativePrice() throws Exception  {
      
        transporter.requestJob("Lisboa", "Leiria", -3) ;
        transporter.clearJobs();
       
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownOrigin() throws Exception{
  
        transporter.requestJob("Madrid", "Leiria", 3);
        transporter.clearJobs(); 
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownDestination() throws Exception{

        transporter.requestJob("Lisboa", "Madrid", 3);
        transporter.clearJobs();       
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void testImparIDSouth() throws Exception{

        transporter.requestJob("Braga", "Leiria", 3);
        transporter.clearJobs();        
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void testParIDNorth() throws Exception{

        transporter.setTransporterIdentifier(2);
        transporter.requestJob("Braga", "Setúbal", 3);
        transporter.clearJobs();        
    }

    @Test 
    public void testPriceGreaterThan100() throws Exception{

        jobview = transporter.requestJob("Faro", "Lisboa", 150);    
        assertEquals(null, jobview);
        transporter.clearJobs();    
    }

    @Test 
    public void testPriceMinorThan10() throws Exception{

        jobview = transporter.requestJob("Faro", "Lisboa", 8);   
        assertTrue(0<jobview.getJobPrice()&&jobview.getJobPrice()<8);
        transporter.clearJobs();
           
    }


    @Test 
    public void testImparPriceImparID() throws Exception{

        transporter.setTransporterIdentifier(1);  
        jobview = transporter.requestJob("Leiria", "Lisboa", 31);  
        assertTrue(10<jobview.getJobPrice()&&jobview.getJobPrice()<31);
        transporter.clearJobs();
         
    }

    
    //estes dois testes dão failure!!! descobri que é porque o preço da jobview não é maior do que o preço requested
  
  

    @Test 
    public void testParPriceImparID() throws Exception{

        jobview = transporter.requestJob("Leiria", "Lisboa", 30);   
        assertTrue(30<jobview.getJobPrice()&&jobview.getJobPrice()<100);
        transporter.clearJobs();
       
        
    } 

    
    @Test 
    public void testImparPriceParID() throws Exception{

        transporter.setTransporterIdentifier(2);
        jobview = transporter.requestJob("Leiria", "Lisboa", 31);   
        assertTrue(31<jobview.getJobPrice()&&jobview.getJobPrice()<100);
        transporter.clearJobs();
       
        
    }  

    
    @Test 
    public void testParPriceParID() throws Exception{

        jobview = transporter.requestJob("Leiria", "Lisboa", 30);   
        assertTrue(10<jobview.getJobPrice()&&jobview.getJobPrice()<30);
            
        
    }
      
    
    //testes individuais ao decideJob
   

    @Test(expected = BadJobFault_Exception.class)
    public void testBadJob() throws Exception{
        transporter.clearJobs();
        jobview= transporter.decideJob("20", false);
        

    }   
  
    
   
    @Test 
    public void testRejectJobView() throws Exception{
        transporter.clearJobs();
        jobview = transporter.requestJob("Leiria", "Lisboa", 30); 
        jobview = transporter.decideJob(jobview.getJobIdentifier(), false);

        assertEquals(JobStateView.REJECTED, jobview.getJobState());

    }   

    

    @Test 
    public void testAcceptJobView() throws Exception{
        transporter.clearJobs();
        jobview = transporter.requestJob("Leiria", "Lisboa", 30);  
        jobview = transporter.decideJob(jobview.getJobIdentifier(), true);
        assertEquals(JobStateView.ACCEPTED, jobview.getJobState());
        
    }   


    //teste ao ping

    @Test 
    public void testPing() {
        
        assertNotNull(transporter.ping("Olá"));
        
    }   

    //teste ao listJobs
    
     @Test 
    public void testlistJobs() throws Exception{
        
        jobview = transporter.requestJob("Leiria", "Lisboa", 30);
        assertNotNull(transporter.listJobs());
        
    }   


    //teste ao jobStatus
   
    @Test 
    public void testinvalidID() throws Exception{
        transporter.clearJobs();
        jobview = transporter.requestJob("Leiria", "Lisboa", 30);
        assertNull(transporter.jobStatus("20"));
        
    }    


    //teste ao clearJobs
   
    @Test 
    public void testclearJobs() throws Exception{
        
        jobview = transporter.requestJob("Leiria", "Lisboa", 30);  
        transporter.clearJobs();
        assertTrue(transporter.listJobs().isEmpty());
        //assertNull(transporter.listJobs());
        
    }   


}