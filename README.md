## Pre-requisites

This project requires
 
* JDK 1.7 or higher.
* Maven to be available and configured.

## Working with the Grid-acolyte.

### Running in the Selenium server standalone mode. 

By default when the Selenium standalone is run under "Server mode", it does not let the end-users 
inject Servlets into it.

But for us to be able to build a clean-up logic of the Shutdown Hooks that are injected into the
```Runtime``` object by Selenium, we would a way to be able to run in the same JVM as the Selenium server.

For this purpose, this project starts off by having its own ```main``` method inside ```CustomServerLauncher```
which just delegates calls to the actual selenium libraries.

So if you need to work with a Selenium server mode, then here's how to do it.

* First build an uber jar out of this code by running ```mvn clean package``` from the command prompt.
You should find an uber jar named ```grid-acolyte.jar``` in your ```target``` folder.
* Now you need to create a property file named ```selenium.properties```. Within this property file you can basically
include all the attributes that you would pass via command line arguments to the Selenium server. For a full list of 
arguments please refer to [org.openqa.selenium.server.SeleniumServer#parseLauncherOptions](https://github.com/SeleniumHQ/selenium/blob/master/java/server/src/org/openqa/selenium/server/SeleniumServer.java)
Below is a sample properties file that can be created to inject two servlets that does the following:

```
servlets=rationale.emotions.servlets.node.ListShutdownHooks,rationale.emotions.servlets.node.OnDemandShutdownhookCleaner
```

* Now we start our selenium server using the command : 
```
java -jar grid-acolyte.jar
```
* This will start our selenium server and also inject our servlets into it.
* The servlets are available in the below URLs (assuming you started the selenium server on the default port of **4444** )
    * **ListShutdownHooks** (http://127.0.0.1:4444/extra/ListShutdownHooks/) - This servlet lists all the shutdown hooks that have been
    added to the JVM after the Selenium Server has started. 
    * **OnDemandShutdownhookCleaner** (http://127.0.0.1:4444/extra/OnDemandShutdownhookCleaner) - This servlet when invoked cleans up all the shutdown hooks that were injected by Selenium, specifically by [org.openqa.selenium.io.TemporaryFilesystem](https://github.com/SeleniumHQ/selenium/blob/master/java/client/src/org/openqa/selenium/io/TemporaryFilesystem.java)

**Here's a sample response from the ListShutdownHooks servlet**
```
[
  {
    "threadName": "Thread-0",
    "threadId": 10,
    "threadGroupName": "main",
    "priority": 5,
    "className": "java.util.logging.LogManager$Cleaner"
  },
  {
    "threadName": "SeleniumServerShutDownHook",
    "threadId": 18,
    "threadGroupName": "main",
    "priority": 5,
    "className": "java.lang.Thread",
    "classLoader": "sun.misc.Launcher$AppClassLoader@3d4eac69"
  }
]    
```

* In order to have the shutdown hooks cleaned up regularly, merely make a ```HTTP GET``` call to the [OnDemandShutdownhookCleaner](http://127.0.0.1:4444/extra/OnDemandShutdownhookCleaner) servlet
after you invoke ```driver.quit()``` in your tests.

### Running in the Grid mode.

If you are planning on using this in the Grid at the node, then you can follow the below set of instructions to do it.

* First build the code using ```mvn clean test``` (there are no unit tests) but this will create a jar named ```grid-acolyte-1.0-SNAPSHOT.jar``` in your ```target``` folder.
(Notice that we are not building an uber jar here because its not required)
* You now copy the above created jar to the same directory as where your selenium server standalone resides.
* You now start your node using the command 
```
java -cp selenium-server-standalone-2.48.2.jar:grid-acolyte-1.0-SNAPSHOT.jar org.openqa.grid.selenium.GridLauncherV3 -role node -hub http://localhost:4444/grid/register -servlets rationale.emotions.servlets.node.ListShutdownHooks,rationale.emotions.servlets.node.OnDemandShutdownhookCleaner
```
* After your test runs to completion, you can follow the instructions that is documented in [this](https://rationaleemotions.wordpress.com/2016/01/15/where-did-my-test-run/)
to figure out to which node was the test routed to and using the IP and Port that was found make a call to the [OnDemandShutdownhookCleaner](http://127.0.0.1:5555/extra/OnDemandShutdownhookCleaner) servlet.
    
### Points to remember

* Since we are running a modified version of Selenium server, you cannot pass any command line arguments to this modified server directly.
All command line parameters are to be included in the properties file.
* By Default this implementation will look for a file named ```selenium.properties``` in the current directory from where ```java -jar```
command is being run. If this file resides in a different directory, then its path can be specified using the JVM argument ```selenium.config```
For e.g., if my configuration file resides in sub directory ```config``` and if its name is ```configuration.properties``` the command would be 
```java -jar grid-acolyte.jar -Dselenium.config=config/configuration.properties```



### Glossary

* Uber Jar - a jar that contains all its dependencies also inside it. Imagine an uber jar to be a self sufficient
jar that can be executed on its own (provided it contains a main class associated with it).

