package br.unicamp.meca.system1.codelets.rosservice;

import id.jros2client.JRos2Client;
import id.jros2client.JRos2ClientFactory;
import pinorobotics.jros2services.JRos2Service;
import pinorobotics.jros2services.JRos2ServicesFactory;
import pinorobotics.jros2services.ServiceHandler;
import troca_ros.AddTwoIntsRequestMessage;
import troca_ros.AddTwoIntsResponseMessage;
import troca_ros.AddTwoIntsServiceDefinition;

/**
 *
 * @author jrborelli
 */

public class AddTwoIntsROS2ServiceProvider implements Runnable {
    
    volatile boolean stopflag = false;
    private Thread thread;
    JRos2ClientFactory clientFactory;
    JRos2ServicesFactory serviceClientFactory;
    ServiceHandler<AddTwoIntsRequestMessage,AddTwoIntsResponseMessage> proc;
    JRos2Client client;
    JRos2Service service;
    public boolean running = false;
    
    public AddTwoIntsROS2ServiceProvider() {
        proc = new ServiceHandler<>() {
            @Override
            public AddTwoIntsResponseMessage execute(AddTwoIntsRequestMessage request) {
                        //System.out.println("Received new request " + request);
                        var response = new AddTwoIntsResponseMessage(request.a + request.b);
                        //System.out.println("Result " + response);
                        return response;
                
            }
        };
    }
    
    @Override
    public void run() {
        running = true;
        clientFactory = new JRos2ClientFactory();
        serviceClientFactory = new JRos2ServicesFactory();
        
        client = clientFactory.createClient();
        service = serviceClientFactory.createService(client,new AddTwoIntsServiceDefinition(),"add_two_ints",proc);
        service.start();
        System.out.println("Service started...");
        while(stopflag == false) {
 
        }
        service.close();
        client.close();
        System.out.println("Service finished ...");
        running = false;
    }
    
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }
    
    public void stop() {
        stopflag = true;
    }
}