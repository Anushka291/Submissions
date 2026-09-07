import HelloApp.Hello;
import HelloApp.HelloHelper;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class HelloServer {

    public static void main(String[] args) {

        try {

            // Initialize ORB
            ORB orb = ORB.init(args, null);

            // Get RootPOA
            POA rootPOA = POAHelper.narrow(
                    orb.resolve_initial_references("RootPOA")
            );

            // Activate POA
            rootPOA.the_POAManager().activate();

            // Create servant
            HelloImpl helloImpl = new HelloImpl();

            // Convert servant to CORBA object
            org.omg.CORBA.Object ref =
                    rootPOA.servant_to_reference(helloImpl);

            Hello helloRef = HelloHelper.narrow(ref);

            // Get Naming Service
            org.omg.CORBA.Object nameService =
                    orb.resolve_initial_references("NameService");

            NamingContextExt namingContext =
                    NamingContextExtHelper.narrow(nameService);

            // Register service
            String serviceName = "HelloService";

            namingContext.rebind(
                    namingContext.to_name(serviceName),
                    helloRef
            );

            System.out.println("----------------------------------");
            System.out.println("      CORBA SERVER STARTED");
            System.out.println("----------------------------------");
            System.out.println("Service Name : " + serviceName);
            System.out.println("Waiting for client requests...");
            System.out.println("----------------------------------");

            // Wait for client requests
            orb.run();

        } catch (Exception e) {
            System.out.println("Server Error:");
            e.printStackTrace();
        }
    }
}