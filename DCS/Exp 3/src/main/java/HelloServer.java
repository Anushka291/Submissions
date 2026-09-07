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

            // Create and initialize ORB
            ORB orb = ORB.init(args, null);

            // Get Root POA
            POA rootPOA =
                    POAHelper.narrow(
                            orb.resolve_initial_references("RootPOA")
                    );

            // Activate POA manager
            rootPOA.the_POAManager().activate();

            // Create servant object
            HelloImpl helloImpl = new HelloImpl();

            // Convert servant into CORBA object
            org.omg.CORBA.Object ref =
                    rootPOA.servant_to_reference(helloImpl);

            Hello helloRef =
                    HelloHelper.narrow(ref);

            // Get Naming Service
            org.omg.CORBA.Object nameService =
                    orb.resolve_initial_references("NameService");

            NamingContextExt namingContext =
                    NamingContextExtHelper.narrow(nameService);

            // Register object with Naming Service
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

            // Keep server running
            orb.run();

        } catch (Exception e) {

            System.out.println("Server Error:");
            e.printStackTrace();
        }
    }
}