# Alfresco Sphereon Vision AIO Project

This is an All-In-One (AIO) project for Alfresco Sphereon Vision integration

__Please note that this module is not working currently!__ 

Run with `mvn clean install -DskipTests=true alfresco:run` or `./run.sh` and verify that it 

 * Runs the embedded Tomcat + H2 DB 
 * Runs Alfresco Platform (Repository)
 * Runs Alfresco Solr4
 * Runs Alfresco Share
 * Packages both as JAR and AMP assembly for modules
 
# Few things to notice

 * No parent pom
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
 
   
  
 
