---
title: "JPA"
repo: "https://github.com/seedstack/jpa-addon"
author: Adrien LAUER
description: "Provides configuration, injection and transactions for Java Persistence API."
tags:
    - persistence
    - transactions
zones:
    - Addons
menu:
    AddonJPA:
        weight: 10
---

SeedStack JPA add-on supports any JPA-compliant [ORM](https://en.wikipedia.org/wiki/Object-relational_mapping) to allow 
your application to interface with relational databases.<!--more--> 

# Dependencies

{{< dependency g="org.seedstack.addons.jpa" a="jpa" >}}

A JPA provider is also required in the classpath. We recommend the popular [Hibernate ORM](http://hibernate.org/orm/), that
you can add with the following Maven dependency:

{{< dependency g="org.hibernate" a="hibernate-entitymanager">}}

If you need to have the JPA API without implementation in some project modules, you can use the following dependency:
 
{{< dependency g="org.hibernate.javax.persistence" a="hibernate-jpa-2.1-api" v="1.0.0.Final" s="provided" >}}
 
# Configuration

## JPA units

By default, SeedStack is able to automatically detect the JPA classes in your project. No `persistence.xml` must be present
in this mode. You just have to declare the JPA units:

{{% config p="jpa" %}}
```yaml
jpa:
  # Configured JPA units with the name of the JPA unit as key
  units: 
    unit1:
      # The name of the data-source declared in the JDBC add-on to use
      datasource: (String)
      
      # The JPA and/or provider properties
      properties:
        property1: value1
      
      # Explicit list of classes belonging to the unit (not needed when using auto-detection)
      classes: (List<Class<?>>)
      
      # List of mapping files paths (orm.xml) if not using annotation-based mapping 
      mappingFiles: (List<String>)
      
      # The fully qualified name of the JPA provider (will be auto-detected if not specified)
      provider: (Class<? extends PersistenceProvider>)
      
      # The transaction type (local or JTA)
      transactionType: (RESOURCE_LOCAL|JTA)
      
      # Specifies how the provider must use a second-level cache
      sharedCacheMode: (ALL|NONE|ENABLE_SELECTIVE|DISABLE_SELECTIVE|UNSPECIFIED)
      
      # The validation mode to be used by the provider
      validationMode: (AUTO|CALLBACK|NONE)
      
      # The fully qualified class name of the exception handler (optional)
      exceptionHandler: (Class<? extends JpaExceptionHandler>)

  # The name of the configured unit to use if nothing is specified in the '@JpaUnit' annotation    
  defaultUnit: (String)
```
{{% /config %}}   
 
## Class configuration

To allow SeedStack to assign auto-detected JPA classes to the right unit, you must configure them with a [class configuration]({{< ref "docs/seed/configuration.md#class-configuration" >}}) 
property:

```yaml
classes:
  org:
    myorg:
      myapp:
        domain:
          model:
            jpaUnit: unit1
```

This configuration will assign every class in the `org.myorg.myapp.domain.model` package and its sub-packages to the 
JPA unit `unit1`.

## Example

Assuming we are using Hibernate, the following configuration defines a unit named `unit1` using the data-source 
`datasource1` defined in the [JDBC add-on]({{< ref "addons/jdbc/index.md" >}}):

```yaml
jpa:
  units:
    unit1:
      datasource: datasource1
      properties:
        hibernate.dialect: org.hibernate.dialect.HSQLDialect
        hibernate.hbm2ddl.auto: create
```

# Usage

To use the Entity Manager in your code, simply inject it:

```java
public class MyRepository {
    @Inject
    private EntityManager entityManager;
    
    @Transactional
    @JpaUnit("unit1")
    public void doSomethingWithMyJpaUnit() {
        // do something
    }
}
```

{{% callout info %}}
All JPA interactions have to be done inside a transaction. Refer to the [transaction support documentation]({{< ref "docs/seed/manual/transactions.md" >}}) for details. 
{{% /callout %}}

# Using a persistence.xml file

Instead of using JPA auto-configuration, you can choose to use a standard `META-INF/persistence.xml` file instead.
This is **NOT recommended** as your loose a significant number of features: 
 
* In this mode, you don't specify a data-source from the JDBC add-on but configure it in the `persistence.xml` file. 
* The `classes`, `mappingFiles`, `provider`, `transactionType`, `sharedCacheMode` and `validationMode` configuration options
have no effect and must be configured in the `persistence.xml` instead (which is mostly static).
* You still have to list every JPA unit in the configuration with a name corresponding to those in the `persistence.xml` file.
* You can still specify provider properties in the configuration. They override properties declared in the `persistence.xml`
file if any.

## Example

Configuration:

```yaml
jpa:
  units:
    unit1:
      properties:
        hibernate.dialect: org.hibernate.dialect.HSQLDialect
```

The `persistence.xml` file:

```xml
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
             http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd"
             version="2.1">
    <persistence-unit name="unit1" transaction-type="RESOURCE_LOCAL">
        <non-jta-data-source>java:comp/env/jdbc/my-datasource</non-jta-data-source>
        <class>org.myorg.myapp.domain.model.SomeClass</class>
        <properties>
            <property name="hibernate.hbm2ddl.auto" value="create"></property>
        </properties>
    </persistence-unit>
</persistence>
```