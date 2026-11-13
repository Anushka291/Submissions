# Experiment 3: CORBA Hello Application (JacORB)

A simple CORBA client-server application. The server registers a `Hello` object with the CORBA Naming Service, and the client looks it up and calls `sayHello()` remotely.

## Project Structure

```
Exp 3/
├── idl/
│   └── Hello.idl            # IDL interface definition
├── lib/                     # JacORB 3.9 jars (no extra downloads needed)
└── src/
    ├── HelloApp/            # Classes generated from Hello.idl (stub, skeleton, helper)
    ├── javax/rmi/CORBA/     # Stub.java compatibility class (removed from modern JDKs)
    ├── HelloImpl.java       # Servant: implements sayHello()
    ├── HelloServer.java     # Server: registers "HelloService" in the Naming Service
    └── HelloClient.java     # Client: looks up "HelloService" and calls sayHello()
```

`src/main/java` and `src/main/idl` hold duplicate copies of the same files and are not used when running.

## Interface (`idl/Hello.idl`)

```idl
module HelloApp {
    interface Hello {
        string sayHello();
    };
};
```

## Prerequisites

- JDK 17 or newer (tested setup: JDK 24 on Windows 11)
- PowerShell or any terminal
- No Maven or other downloads are needed, since all jars are in `lib/`

> Modern JDKs no longer include CORBA, so every `java` command below passes two `-D` options that make Java use JacORB.

## How to Run

### 1. Open a terminal in the `src` folder

```powershell
cd "<path>\Exp 3\src"
```

### 2. Compile (once)

```powershell
javac -cp "..\lib\*;." HelloApp\*.java javax\rmi\CORBA\Stub.java HelloImpl.java HelloServer.java HelloClient.java
```

No output means success. (`Hello.idl` has already been converted into the `HelloApp` classes, so the IDL compiler does not need to run again.)

### 3. Run, using three terminals

Open three terminals, all in `Exp 3\src`. **Each terminal needs its own `$CP` and `$J` lines**, which are included in every block below. They exist only in the terminal where you set them, so a terminal without them fails with `package does not exist` or `ClassNotFoundException`.

**Terminal 1: Naming Service** (start first, leave running)

```powershell
$CP = "..\lib\*;."
$J  = "-Dorg.omg.CORBA.ORBClass=org.jacorb.orb.ORB","-Dorg.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton"
java -cp "$CP" $J "-DOAPort=1050" org.jacorb.naming.NameServer
```

**Terminal 2: Server**

```powershell
$CP = "..\lib\*;."
$J  = "-Dorg.omg.CORBA.ORBClass=org.jacorb.orb.ORB","-Dorg.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton"
java -cp "$CP" $J HelloServer -ORBInitRef "NameService=corbaloc::localhost:1050/NameService"
```

**Terminal 3: Client**

```powershell
$CP = "..\lib\*;."
$J  = "-Dorg.omg.CORBA.ORBClass=org.jacorb.orb.ORB","-Dorg.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton"
java -cp "$CP" $J HelloClient -ORBInitRef "NameService=corbaloc::localhost:1050/NameService"
```

Always start in this order: Naming Service, then Server, then Client.

## Expected Output

**Server:**

```
----------------------------------
      CORBA SERVER STARTED
----------------------------------
Service Name : HelloService
Waiting for client requests...
----------------------------------
Client requested sayHello()
```

**Client:**

```
----------------------------------
        CORBA CLIENT
----------------------------------
Response from Server:
Hello from CORBA Server!
----------------------------------
```

## Troubleshooting

| Problem | Fix |
|---|---|
| `NameService` not found / corbaloc error | Start the name server with an IOR file instead: `java -cp "$CP" $J "-DOAPort=1050" org.jacorb.naming.NameServer ns.ior`, then use `-ORBInitRef "NameService=file:ns.ior"` for the server and client. |
| `Address already in use` | Port 1050 is taken. Use another port number in all three commands (including the corbaloc URL). |
| `Could not find or load main class` | Run `javac` first, and make sure the terminal is in the `src` folder. |
| Client fails to connect | Check that the Naming Service and Server are both still running. |
| Red errors in VS Code | Editor-only: VS Code does not know about the jars. They do not affect running from the terminal. |

## Notes

- PowerShell requires the `-D` options to be quoted, as in the `$J` variable above.
- `$CP` and `$J` last only for the current terminal window, so set them again in every new terminal (all three here).
