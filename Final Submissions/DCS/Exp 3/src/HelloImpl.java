import HelloApp.HelloPOA;

public class HelloImpl extends HelloPOA {

    @Override
    public String sayHello() {
        System.out.println("Client requested sayHello()");
        return "Hello from CORBA Server!";
    }
}