# Alfresco Sphereon Integration

This is a multi module project for all Alfresco integrations from Sphereon.

Please note that the sphereon-base modules are prerequisites for all other Alfresco integrations from Sphereon, as they contain shared libraries and code.

Run with `mvn clean install -DskipTests=true` and verify that it 

 * Builds all individual modules
 * Packages both as JAR and AMP assembly for modules
 

Currently the following integrations are available for Alfresco:

 * Blockchain integration. This uses Sphereon Blockchain-proof and Easy-Blockchain APIs to register fingerprints on multiple blockchain implementations
 * PDF conversion. This uses the Sphereon Conversion2PDF API to convert almost any file to PDF
 
 
# Few things to notice from standard Alfresco SDK projects

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
 
   
  
 
