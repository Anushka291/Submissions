import java.rmi.*;
import java.util.Scanner;

public class AddClient {

    public static void main(String args[]) {

        try {
            Scanner sc = new Scanner(System.in);

            System.out.print("Enter first number: ");
            double a = sc.nextDouble();

            System.out.print("Enter second number: ");
            double b = sc.nextDouble();

            AddServerIntf obj =
                (AddServerIntf) Naming.lookup(
                    "rmi://localhost:1099/AddServer");

            double result = obj.add(a, b);

            System.out.println("Sum = " + result);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}