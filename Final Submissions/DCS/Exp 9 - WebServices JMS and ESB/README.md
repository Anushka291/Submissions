# DCS Experiment 9

## Implementing Publish/Subscribe Paradigm using Web Services, ESB and JMS

This README contains the Windows PowerShell commands for running the
three implementations in Experiment 9:

1.  Publish/Subscribe using a Web Service
2.  Publish/Subscribe using JMS (Java Message Service)
3.  Publish/Subscribe using ESB (Enterprise Service Bus)

The commands are based on the practical notebook instructions. Run each
terminal in a separate PowerShell window. Keep the ActiveMQ broker
running while testing Web Services and JMS.

------------------------------------------------------------------------

## 1. Prerequisites

Install or keep the following tools:

-   Java JDK (with `java` and `javac` available in PowerShell)
-   Apache ActiveMQ **6.3.3**
-   Apache Maven **3.9.16** (required for the ESB implementation)

### Folder locations used in these commands

The examples below use the folder locations written in the practical
notebook:
### Make sure for all terminals you run these three commands to configure the path
``` powershell 
$EXP = "D:\AISSMS IOIT\7th semester\Final Submissions\DCS\Exp 9 - WebServices JMS and ESB"
$ACTIVEMQ = "$EXP\apache-activemq-6.3.3"
$MAVEN = "$EXP\ESB\apache-maven-3.9.16"
```

If your folders are stored somewhere else, change these paths before
running the commands.

Check Java:

``` powershell
java -version
javac -version
```

------------------------------------------------------------------------

# Part A -- Publish/Subscribe using Web Services

## Terminal 1 -- Start the ActiveMQ broker

Open PowerShell and execute:

``` powershell
cd "$ACTIVEMQ\bin"
.\activemq.bat start
```

If you need to see the broker logs directly in the terminal, use:

``` powershell
.\activemq.bat console
```

Leave this terminal running. ActiveMQ normally listens for JMS
connections on `tcp://localhost:61616`.

## Terminal 2 -- Compile and run the Web Service

Open a second PowerShell window. Go to the directory containing
`MessageWebService.java`. Set the ActiveMQ library classpath:

``` powershell
$CP = "$ACTIVEMQ\lib\*"
```

Compile the Java file:

``` powershell
javac -cp "$CP" MessageWebService.java
```

Run the Web Service:

``` powershell
java -cp "$CP;." MessageWebService
```

Keep this terminal running. According to the practical, the service is
accessed at:

``` text
http://localhost:8087/publish
```

If the Java source file is not in the current directory, first use `cd`
to enter the folder containing it.

## Terminal 3 -- Send a POST request

Open a third PowerShell window and send a message to the Web Service:

``` powershell
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8087/publish" `
  -Body "Hello from Web Services!" `
  -ContentType "text/plain"
```

The request uses the HTTP **POST** method, sends the message as the
request body, and specifies `text/plain` as the content type.

**Expected result:** The Web Service should accept the POST request and
publish/process the message according to the Java implementation. The
exact response text depends on `MessageWebService.java`.

------------------------------------------------------------------------

# Part B -- Publish/Subscribe using JMS

## Terminal 1 -- Start ActiveMQ

Open PowerShell:

``` powershell
cd "$ACTIVEMQ\bin"
.\activemq.bat start
```

Alternatively, to run it in the foreground and view logs:

``` powershell
.\activemq.bat console
```

Keep the broker running.

## Terminal 2 -- Compile the Publisher and Subscribers

Open a second PowerShell window. Go to the directory containing
`Publisher.java`, `Subscriber1.java`, and `Subscriber2.java`.

Set the classpath and compile all three files:

``` powershell
$CP = "$ACTIVEMQ\lib\*"
javac -cp "$CP" Publisher.java Subscriber1.java Subscriber2.java
```

Compilation should complete without errors before proceeding.

## Terminal 3 -- Run Subscriber 1

Open a third PowerShell window. Go to the directory containing the
compiled `.class` files and execute:

``` powershell
$CP = "$ACTIVEMQ\lib\*"
java -cp "$CP;." Subscriber1
```

Keep Subscriber 1 running so it can receive published messages.

## Terminal 4 -- Run Subscriber 2

Open a fourth PowerShell window and execute:

``` powershell
$CP = "$ACTIVEMQ\lib\*"
java -cp "$CP;." Subscriber2
```

Keep Subscriber 2 running.

## Publish a message

Use the remaining PowerShell window (or open another one) to run the
Publisher:

``` powershell
$CP = "$ACTIVEMQ\lib\*"
java -cp "$CP;." Publisher
```

If the Publisher prompts for a message, enter the message and follow the
program's prompt.

**Expected result:** Both subscribers should receive the message
published to the same JMS topic, provided the Java programs use the same
topic name and the subscribers are active when the message is published.

------------------------------------------------------------------------

# Part C -- Publish/Subscribe using ESB

## Terminal 1 -- Configure Maven and compile the ESB project

Open PowerShell. Set the Maven environment variables:

``` powershell
$env:MAVEN_HOME = "$EXP\ESB\apache-maven-3.9.16"
$env:Path = "$env:MAVEN_HOME\bin;$env:Path"
```

Move to the **ESB project directory containing `pom.xml`**. For example,
if the project files are directly inside the `ESB` folder:

``` powershell
cd "$EXP\ESB"
```

Compile the project:

``` powershell
mvn clean compile
```

> Run Maven commands from the directory containing `pom.xml`, not from
> Maven's `bin` directory. If your `pom.xml` is in a subfolder, change
> to that subfolder instead.

## Terminal 1 -- Start the ESB Router

After compilation succeeds, run:

``` powershell
mvn exec:java "-Dexec.mainClass=ESBRouter"
```

Keep the router running.

If your Maven project uses a package name for `ESBRouter`, use its fully
qualified class name in `-Dexec.mainClass`.

## Terminal 2 -- Run Subscriber 1

Open another PowerShell window. Set the ActiveMQ classpath and go to the
directory containing the compiled subscriber class (or run from the
project directory if the class is there):

``` powershell
$CP = "$ACTIVEMQ\lib\*"
java -cp "$CP;." Subscriber1
```

Keep it running.

## Terminal 3 -- Run Subscriber 2

Open a third PowerShell window and execute:

``` powershell
$CP = "$ACTIVEMQ\lib\*"
java -cp "$CP;." Subscriber2
```

Keep it running.

## Terminal 4 -- Run the Publisher

Open a fourth PowerShell window and execute:

``` powershell
cd "D:\AISSMS IOIT\7th semester\Final Submissions\DCS\Exp 9 - WebServices JMS and ESB\ESB"
$env:MAVEN_HOME = "$EXP\ESB\apache-maven-3.9.16"
$env:Path = "$env:MAVEN_HOME\bin;$env:Path"
mvn exec:java "-Dexec.mainClass=ESBPublisher"
```

If prompted, enter the message to publish.

**Expected result:** The ESB Router receives/routes the published
message, and the subscribers receive it according to the routing and
messaging logic implemented in the ESB project.

------------------------------------------------------------------------

## 2. Terminal arrangement at a glance

  Implementation   Terminal             Command / task
  ---------------- -------------------- -----------------------------------------------
  Web Services     1                    Start ActiveMQ broker
  Web Services     2                    Compile and run `MessageWebService`
  Web Services     3                    Send HTTP POST using `Invoke-RestMethod`
  JMS              1                    Start ActiveMQ broker
  JMS              2                    Compile Publisher and both Subscribers
  JMS              3                    Run `Subscriber1`
  JMS              4                    Run `Subscriber2`
  JMS              5 / another window   Run `Publisher`
  ESB              1                    Configure Maven, compile, and run `ESBRouter`
  ESB              2                    Run `Subscriber1`
  ESB              3                    Run `Subscriber2`
  ESB              4                    Run `Publisher`

The JMS arrangement uses an additional window for the Publisher so that
both subscribers can remain active while the message is sent.

------------------------------------------------------------------------

## 3. Troubleshooting

### `java` or `javac` is not recognized

Check that the JDK is installed and that its `bin` directory is included
in the system `PATH`. Reopen PowerShell and run:

``` powershell
java -version
javac -version
```

### ActiveMQ connection refused

-   Confirm that the ActiveMQ broker is running.
-   Check that the broker is listening on port `61616`.
-   Start the broker before running the Publisher, Subscribers, or Web
    Service.
-   Check that the Java programs use the correct broker URL, normally
    `tcp://localhost:61616`.

### `ClassNotFoundException`

-   Run the command from the directory containing the compiled `.class`
    file, or provide the correct class-directory path.
-   Ensure the classpath contains the ActiveMQ libraries and the current
    directory:

``` powershell
$CP = "$ACTIVEMQ\lib\*"
java -cp "$CP;." ClassName
```

Replace `ClassName` with the required class name.

### `javac` cannot find a source file

Use `cd` to move to the directory containing the `.java` file before
compiling. The Java source files must be present in that directory or be
referenced using their correct paths.

### Maven command is not recognized

Set Maven's environment variables in the current PowerShell window:

``` powershell
$env:MAVEN_HOME = "$EXP\ESB\apache-maven-3.9.16"
$env:Path = "$env:MAVEN_HOME\bin;$env:Path"
mvn -version
```

Then run Maven from the project directory containing `pom.xml`.

### Port 8087 is already in use

The Web Service cannot bind to a port that another process is already
using. Stop the earlier service or configure the Java Web Service to use
a free port. If you change the port, update the POST request URI to use
the same port.

### HTTP 403 or request failure

Confirm that the request is sent to the Web Service endpoint
(`http://localhost:8087/publish`) and not to the ActiveMQ web console.
Check the Web Service terminal for the actual error. The exact endpoint
behavior depends on the Java source code.

------------------------------------------------------------------------

## 4. Important notes

-   Start ActiveMQ before running applications that connect to its
    broker.
-   Keep the broker, Web Service/ESB Router, and subscribers running
    while testing.
-   Use separate PowerShell windows for processes that must run
    simultaneously.
-   The Web Service implementation uses an HTTP POST request.
-   JMS and ESB implementations use the Publisher/Subscriber Java
    programs shown in the practical.
-   The source files (`MessageWebService.java`, `Publisher.java`,
    `Subscriber1.java`, `Subscriber2.java`, and `ESBRouter`) must be
    present in the relevant project directories.
-   The exact output depends on the Java source code and the
    topic/routing configuration used in the project.

## 5. Result

The Publish/Subscribe paradigm is implemented using Web Services, JMS,
and ESB. A publisher sends a message, and the message is delivered or
routed to the corresponding subscribers through the respective
implementation.
