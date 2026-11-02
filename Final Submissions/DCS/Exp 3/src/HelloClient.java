import HelloApp.Hello;
import HelloApp.HelloHelper;

import org.omg.CORBA.ORB;

import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class HelloClient {

    public static void main(String[] args) {

        try {

            // Initialize ORB
            ORB orb = ORB.init(args, null);

            // Get Naming Service
            org.omg.CORBA.Object nameService =
                    orb.resolve_initial_references("NameService");

            NamingContextExt namingContext =
                    NamingContextExtHelper.narrow(nameService);

            // Locate server object
            Hello helloRef = HelloHelper.narrow(
                    namingContext.resolve_str("HelloService")
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