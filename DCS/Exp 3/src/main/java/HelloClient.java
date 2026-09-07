import HelloApp.Hello;
import HelloApp.HelloHelper;

import org.omg.CORBA.ORB;

import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class HelloClient {

    public static void main(String[] args) {

        try {

            // Create and initialize ORB
            ORB orb = ORB.init(args, null);

            // Get Naming Service
            org.omg.CORBA.Object nameService =
                    orb.resolve_initial_references("NameService");

            NamingContextExt namingContext =
                    NamingContextExtHelper.narrow(nameService);

            // Find the remote object
            String serviceName = "HelloService";

            Hello helloRef =
                    HelloHelper.narrow(
                            namingContext.resolve_str(serviceName)
                    );

            System.out.println("----------------------------------");
            System.out.println("        CORBA CLIENT");
            System.out.println("----------------------------------");

            // Call remote method
            String response = helloRef.sayHello();

            System.out.println("Response from Server:");
            System.out.println(response);

            System.out.println("----------------------------------");

        } catch (Exception e) {

            System.out.println("Client Error:");
            e.printStackTrace();
        }
    }
}