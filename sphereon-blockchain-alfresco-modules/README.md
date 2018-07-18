# Alfresco Sphereon Blockchain AIO Project

This is an All-In-One (AIO) project for Alfresco Sphereon Blockchain integration 

It adds share buttons to register and verify files interactively. Also an aspect is registered so you can view additional blockchain information regarding a file. To view this aspect you have to change the file type to a "Blockchain registered file". This doesn't happen automatically at this time.
The advanced search has been expanded to search for blockchain specific metadata. Allowing you to search for all blockchain registered files (from Alfresco).

# Workflow
Preliminary support for the workflow is there. This has not been fully tested yet, so may not fully work as intended.

```
var blockchainRegistration = actions.create("blockchain-proof-register-file");
for (var i = 0; i &lt; bpm_package.children.length; i++) {
    if (!bpm_package.children[i].hasAspect('spbcc:blockchainAspectBase')) {
        bpm_package.children[i].addAspect('spbcc:blockchainAspectBase');
    }
    logger.log("Registering node:" + bpm_package.children[i].nodeRef);
   blockchainRegistration.execute(bpm_package.children[i]);
}
```

# Configuration
Please look at the alfresco-global.properties file for config options. It uses defaults from the sphereon-base AMPs!

# Architecture
Most of the implementation is done using a delegate class in the Platform module. This delegate is also exposes as a REST endpoint, So even without share you should be able to register and verify blockchain registration states.


# Installation
Install the platform and share AMPs. Please make sure you install the respective sphereon-base AMPs first,

# Development
Run with `mvn clean install -DskipTests=true alfresco:run` or `./run.sh` and verify that it 

 * Runs the embedded Tomcat + H2 DB 
 * Runs Alfresco Platform (Repository)
 * Runs Alfresco Solr4
 * Runs Alfresco Share
 * Packages both as JAR and AMP assembly for modules
 
# Few things to notice

 * This project needs the sphereon-base-alfresco-modules
 * No WAR projects, all handled by the Alfresco Maven Plugin 
 * No runner project - it's all in the Alfresco Maven Plugin
 * Standard JAR packaging and layout
 * Works seamlessly with Eclipse and IntelliJ IDEA
 * JRebel for hot reloading, JRebel maven plugin for generating rebel.xml, agent usage: `MAVEN_OPTS=-Xms256m -Xmx1G -agentpath:/home/martin/apps/jrebel/lib/libjrebel64.so`
 * AMP as an assembly
 * [Configurable Run mojo](https://github.com/Alfresco/alfresco-sdk/blob/sdk-3.0/plugins/alfresco-maven-plugin/src/main/java/org/alfresco/maven/plugin/RunMojo.java) in the `alfresco-maven-plugin`
 * No unit testing/functional tests just yet
 * Resources loaded from META-INF
 * Web Fragment (this includes a sample servlet configured via web fragment)
 
   
  
 
