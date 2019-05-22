
package pt.upa.transporter.ws;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.10
 * Generated source version: 2.2
 * 
 */
@WebService(name = "TransporterPortType", targetNamespace = "http://ws.transporter.upa.pt/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface TransporterPortType {


    /**
     * 
     * @param name
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "ping", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.Ping")
    @ResponseWrapper(localName = "pingResponse", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.PingResponse")
    @Action(input = "http://ws.transporter.upa.pt/TransporterPort/pingRequest", output = "http://ws.transporter.upa.pt/TransporterPort/pingResponse")
    public String ping(
        @WebParam(name = "name", targetNamespace = "")
        String name);

    /**
     * 
     * @param price
     * @param origin
     * @param destination
     * @return
     *     returns pt.upa.transporter.ws.JobView
     * @throws BadPriceFault_Exception
     * @throws BadLocationFault_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "requestJob", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.RequestJob")
    @ResponseWrapper(localName = "requestJobResponse", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.RequestJobResponse")
    @Action(input = "http://ws.transporter.upa.pt/TransporterPort/requestJobRequest", output = "http://ws.transporter.upa.pt/TransporterPort/requestJobResponse", fault = {
        @FaultAction(className = BadLocationFault_Exception.class, value = "http://ws.transporter.upa.pt/TransporterPort/requestJob/Fault/BadLocationFault"),
        @FaultAction(className = BadPriceFault_Exception.class, value = "http://ws.transporter.upa.pt/TransporterPort/requestJob/Fault/BadPriceFault")
    })
    public JobView requestJob(
        @WebParam(name = "origin", targetNamespace = "")
        String origin,
        @WebParam(name = "destination", targetNamespace = "")
        String destination,
        @WebParam(name = "price", targetNamespace = "")
        int price)
        throws BadLocationFault_Exception, BadPriceFault_Exception
    ;

    /**
     * 
     * @param id
     * @param accept
     * @return
     *     returns pt.upa.transporter.ws.JobView
     * @throws BadJobFault_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "decideJob", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.DecideJob")
    @ResponseWrapper(localName = "decideJobResponse", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.DecideJobResponse")
    @Action(input = "http://ws.transporter.upa.pt/TransporterPort/decideJobRequest", output = "http://ws.transporter.upa.pt/TransporterPort/decideJobResponse", fault = {
        @FaultAction(className = BadJobFault_Exception.class, value = "http://ws.transporter.upa.pt/TransporterPort/decideJob/Fault/BadJobFault")
    })
    public JobView decideJob(
        @WebParam(name = "id", targetNamespace = "")
        String id,
        @WebParam(name = "accept", targetNamespace = "")
        boolean accept)
        throws BadJobFault_Exception
    ;

    /**
     * 
     * @param id
     * @return
     *     returns pt.upa.transporter.ws.JobView
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "jobStatus", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.JobStatus")
    @ResponseWrapper(localName = "jobStatusResponse", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.JobStatusResponse")
    @Action(input = "http://ws.transporter.upa.pt/TransporterPort/jobStatusRequest", output = "http://ws.transporter.upa.pt/TransporterPort/jobStatusResponse")
    public JobView jobStatus(
        @WebParam(name = "id", targetNamespace = "")
        String id);

    /**
     * 
     * @return
     *     returns java.util.List<pt.upa.transporter.ws.JobView>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "listJobs", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.ListJobs")
    @ResponseWrapper(localName = "listJobsResponse", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.ListJobsResponse")
    @Action(input = "http://ws.transporter.upa.pt/TransporterPort/listJobsRequest", output = "http://ws.transporter.upa.pt/TransporterPort/listJobsResponse")
    public List<JobView> listJobs();

    /**
     * 
     */
    @WebMethod
    @RequestWrapper(localName = "clearJobs", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.ClearJobs")
    @ResponseWrapper(localName = "clearJobsResponse", targetNamespace = "http://ws.transporter.upa.pt/", className = "pt.upa.transporter.ws.ClearJobsResponse")
    @Action(input = "http://ws.transporter.upa.pt/TransporterPort/clearJobsRequest", output = "http://ws.transporter.upa.pt/TransporterPort/clearJobsResponse")
    public void clearJobs();

}