Accelerate
==========

The aim of this project is to accelerate the development of JSF-CDI-JPA based projects. The target projects are small-to-medium-sized business web applications. 

This project is not yet-another or me-too framework. It only tries to facilitate the development with these standards by adhering to their best or common practices and filling the gaps among them. It also paves the way for development with them by providing guidelines, ready-to-use samples, code generators, and templates.

Accelerate is an open-source project published under Apache 2.0 license. It is written in Java 8 and can be used in any servlet container such as Tomcat and Jetty or JEE application server such as TomEE and JBoss wildfly.

Technology Stack
----------------
We support the following variations in our technology stack.

* JSF 2.2
  * Myfaces
  * Mojarra
* CDI 1.0
  * OWB
  * Weld
* JPA 2.1
  * Eclipselink
  * Hibernate
* Component library
  * Primefaces
  * Richfaces (TODO)
* Server
  * Tomcat
  * Wildfly
  * Jetty (TODO)
  * Glassfish (TODO)
  * TomEE (TODO)
* Scheduler
  * Quartz
  * Executor service
  * Managed executor service (for application servers)
* Cache Provider
  * Eh-cache
  * Guava (TODO)
* Logging
  * SLF4J
* REST (Jax-rs) and Web services (Jax-ws)
  * CXF
  * (application server provided)
* Mail
  * Simplemail
  * (application server provided)
* Report engine (for samples)
  * BIRT
  * Jasper (TODO)
* Workflow engine
  * Jbpm
* IDE (for guidelines)
  * Intellij IDEA
  * Eclipse
* Programming language
  * Java 1.8
  * Groovy (for some samples)
* Stylesheet language
  * LESS
* Javascript library
  * JQuery

 
We also use these projects:
  * Deltaspike
  * Omnifaces


Covered Topics
--------------

* Common UI Tasks
  * Common form templates
  * Page navigation
  * Parameter management
  * User messages
  * Exception handling
  * Validation
* Entity management (CRUD, search, pagination, sort)
  * Simple entity with many-to-one relations to other entities or enums
    * add/edit in a modal dialog
    * add/edit in place 
    * add/edit in a different page
  * Entity with one-to-many relations to weak entities
  * Entity with many-to-many relations to other entities or enums
  * Hierarchical entities (tree based)
  * Ordered (or ranked) entities
  * Initializing database with entities
  * In-memory entity management (TODO)
  * Dynamic entities (TODO)
* Security
  * User and online user management
  * Attribute-based, role-based, and object-based authorization
  * Auditing
* File management
  * File uploading
  * File downloading
* Scheduling
  *  Transient schedulers
  *  Persistent schedulers
  *  Asynchronous execution
* Cache handling
* Configuration

  
  

  





