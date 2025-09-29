
package br.unicamp.meca.system1.codelets.rosservice;

import pinorobotics.jros2services.JRos2Service;
import pinorobotics.jros2services.JRos2ServicesFactory;
import pinorobotics.jros2services.ServiceHandler;
import id.jros2client.JRos2Client;
import id.jros2client.JRos2ClientFactory;
import troca_ros.AddTwoIntsRequestMessage;
import troca_ros.AddTwoIntsResponseMessage;
import troca_ros.AddTwoIntsServiceDefinition;

/**
 * @author jrborelli
 *
 */
public class ROS2_AddTwoIntService implements Runnable {

    private volatile boolean stopflag = false;
    private Thread thread;

    private JRos2Client client;
    private JRos2Service service;

    public ROS2_AddTwoIntService() {
    }

    @Override
    public void run() {
        // Create client and service factory
        JRos2ClientFactory clientFactory = new JRos2ClientFactory();
        JRos2ServicesFactory serviceFactory = new JRos2ServicesFactory();

        client = clientFactory.createClient();

        // Define service handler
        ServiceHandler<AddTwoIntsRequestMessage, AddTwoIntsResponseMessage> handler =
            request -> new AddTwoIntsResponseMessage(request.a + request.b);

        service = serviceFactory.createService(client, new AddTwoIntsServiceDefinition(), "add_two_ints", handler);
        service.start();

        System.out.println("Service started...");

        while (!stopflag) {
            // keep running
        }

        service.close();
        client.close();
        System.out.println("Service stopped...");
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