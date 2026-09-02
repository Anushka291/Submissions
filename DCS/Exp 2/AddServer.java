import java.rmi.*;
import java.rmi.registry.LocateRegistry;

public class AddServer {

    public static void main(String args[]) {

        try {
            // Start RMI Registry
            LocateRegistry.createRegistry(1099);

            // Create remote object
            AddServerImpl obj = new AddServerImpl();

            // Register remote object
            Naming.rebind("rmi://localhost:1099/AddServer", obj);

            System.out.println("RMI Server is ready...");
            System.out.println("Waiting for client...");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}