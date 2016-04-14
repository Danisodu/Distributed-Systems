package pt.upa.transporter.ws.cli;

import org.junit.*;
import static org.junit.Assert.*;


import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;


/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class TransporterPortTestIT {

    // static members

    // one-time initialization and clean-up

    @BeforeClass //pode ser usado para fazer a ligação com o servidor
    public static void oneTimeSetUp() {

    }

    @AfterClass //depois de realizar o teste posso querer limpar as viagens todas que existem, por exemplo
    public static void oneTimeTearDown() {

    }

    // members

    private TransporterClient transporterclient;
    private JobView jobview;

    // initialization and clean-up for each test

    @Before
    public void setUp() {
        transporterclient = new TransporterClient("http://localhost:9090"); //é isto??
        transporterclient.setEndpointAddress("http://localhost:8081/transporter-ws/endpoint");
        transporterclient.initServiceSearch(); 

        //jobview = transporterclient.convertJob(new TransporterJob("UpaTransporter1", "1", "Castelo Branco", "Faro", 60, JobState.PROPOSED));
    }

    @After
    public void tearDown() {
        transporterclient = null;
        jobview=null;
    }

    //testes individuais ao RequestJob
    @Test(expected = BadPriceFault_Exception.class)
    public void testRequestJobNegativePrice() throws Exception  {
      
        transporterclient.requestJob("Lisboa", "Leiria", -3) ;
        transporterclient.clearJobs();
       
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownOrigin() throws Exception{
  
        transporterclient.requestJob("Madrid", "Leiria", 3);
        transporterclient.clearJobs(); 
    }

    @Test(expected = BadLocationFault_Exception.class)
    public void testUnknownDestination() throws Exception{

        transporterclient.requestJob("Lisboa", "Madrid", 3);
        transporterclient.clearJobs();       
    }

    @Test
    public void testImparIDSouth() throws Exception{

        transporterclient.requestJob("Braga", "Leiria", 3);
        assertNull(transporterclient.requestJob("Braga", "Leiria", 3));
        transporterclient.clearJobs();        
    }


    @Test 
    public void testPriceGreaterThan100() throws Exception{

        jobview = transporterclient.requestJob("Faro", "Lisboa", 150);    
        assertEquals(null, jobview);
        transporterclient.clearJobs();    
    }

    @Test 
    public void testPriceMinorThan10() throws Exception{

        jobview = transporterclient.requestJob("Faro", "Lisboa", 8);   
        assertTrue(0<jobview.getJobPrice()&&jobview.getJobPrice()<8);
        transporterclient.clearJobs();
           
    }



    
    //estes dois testes dão failure!!! descobri que é porque o preço da jobview não é maior do que o preço requested
  
  

  
   

    @Test(expected = BadJobFault_Exception.class)
    public void testBadJob() throws Exception{
        transporterclient.clearJobs();
        jobview= transporterclient.decideJob("20", false);
        

    }   
  
    
   
    @Test 
    public void testRejectJobView() throws Exception{
        transporterclient.clearJobs();
        jobview = transporterclient.requestJob("Leiria", "Lisboa", 30); 
        jobview = transporterclient.decideJob(jobview.getJobIdentifier(), false);
        assertEquals(JobStateView.REJECTED, jobview.getJobState());

    }   

    

    @Test 
    public void testAcceptJobView() throws Exception{
        transporterclient.clearJobs();
        jobview = transporterclient.requestJob("Leiria", "Lisboa", 30);  
        jobview = transporterclient.decideJob(jobview.getJobIdentifier(), true);
        assertEquals(JobStateView.ACCEPTED, jobview.getJobState());
        
    }   


    //teste ao ping

    @Test 
    public void testPing() {
        
        assertNotNull(transporterclient.ping("Olá"));
        
    }   

    //teste ao listJobs
    
     @Test 
    public void testlistJobs() throws Exception{
        
        jobview = transporterclient.requestJob("Leiria", "Lisboa", 30);
        assertNotNull(transporterclient.listJobs());
        
    }   


    //teste ao jobStatus
   
    @Test 
    public void testinvalidID() throws Exception{
        transporterclient.clearJobs();
        jobview = transporterclient.requestJob("Leiria", "Lisboa", 30);
        assertNull(transporterclient.jobStatus("20"));
        
    }    


    //teste ao clearJobs
   
    @Test 
    public void testclearJobs() throws Exception{
        
        jobview = transporterclient.requestJob("Leiria", "Lisboa", 30);  
        transporterclient.clearJobs();
        assertTrue(transporterclient.listJobs().isEmpty());
        //assertNull(transporterclient.listJobs());
        
    }   


}