---
title: "JPA Configuration"
addon: "JPA"
repo: "https://github.com/seedstack/jpa-addon"
author: Adrien LAUER
description: "Provides configuration, injection and transactions for Java Persistence API."
tags:
    - persistence
    - transactions
zones:
    - Addons
menu:
    JPA:
        parent: "contents"
        weight: 10
---

SeedStack JPA add-on supports any JPA-compliant [ORM](https://en.wikipedia.org/wiki/Object-relational_mapping) to allow 
your application to interface with relational databases.<!--more--> 

## Dependencies

{{< dependency g="org.seedstack.addons.jpa" a="jpa" >}}
{{% tabs list="Hibernate|EclipseLink" %}}
{{% tab "Hibernate" true %}}
Hibernate is a very popular JPA implementation. When using Hibernate, SeedStack is able to stream results from the database 
without putting them all in memory (useful when retrieving for result sets).

{{< dependency g="org.hibernate" a="hibernate-entitymanager">}}
{{% /tab %}}
{{% tab "EclipseLink" %}}
Eclipse link is a also popular and is the reference JPA implementation.

{{< dependency g="org.eclipse.persistence" a="eclipselink">}}
{{% /tab %}}
{{% /tabs %}}

## Configuration

SeedStack is able to automatically detect the JPA classes in your project, without `persistence.xml` file. You just have 
to declare the JPA units:

{{% config p="jpa" %}}
```yaml
jpa:
  # Configured JPA units with the name of the JPA unit as key
  units: 
    myUnit:
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
 
To allow SeedStack to associate auto-detected JPA classes to a particular JPA unit, you must configure them with a 
[class configuration]({{< ref "docs/core/configuration.md#class-configuration" >}}) tag:

```yaml
classes:
  org:
    myorg:
      myapp:
        domain:
          model:
            jpaUnit: myUnit
```

This will assign every class in the `org.myorg.myapp.domain.model` package and its sub-packages to the 
JPA unit `myUnit`.

### Persistence.xml file

Instead of using JPA auto-configuration, you can choose to use a standard `META-INF/persistence.xml` file instead. In this
case: 

* Ensure that the JPA units declared in the configuration above are matching the ones specified in the `persistence.xml` file.
* The datasource must be configured in the `persistence.xml` file and not in the configuration above. 
* The `classes`, `mappingFiles`, `provider`, `transactionType`, `sharedCacheMode` and `validationMode` configuration options
above have no effect and must be specified in `persistence.xml` instead.
* You can still specify JPA provider properties in the configuration. They override properties declared in the `persistence.xml`
file if any.

## Usage

To use the Entity Manager directly, simply inject it:

```java
public class MyRepository {
    @Inject
    private EntityManager entityManager;
    
    @Transactional
    @JpaUnit("myUnit")
    public void doSomethingWithMyJpaUnit() {
        // do something
    }
}
```

{{% callout info %}}
All JPA interactions have to be done inside a transaction. Refer to the [transaction documentation]({{< ref "docs/core/transactions.md" >}}) for details. 
{{% /callout %}}

## Example

### Connection pool

In addition to the JPA add-on and the Hibernate dependencies, we'll add an HikariCP connection pool:

{{< dependency g="com.zaxxer" a="HikariCP" >}}

### Configuration

Assuming we are using Hibernate and an Hikari connection pool, the configuration below: 

* Defines a unit named `myUnit`,
* Using the data-source `myDatasource` defined with the [JDBC add-on]({{< ref "addons/jdbc/index.md" >}}),
* Affects every JPA entity of packages `org.generated.project.domain.model.*` to `myUnit`. 

```yaml
jdbc:
  datasources:
    myDatasource:
      provider: org.seedstack.jdbc.internal.datasource.HikariDataSourceProvider
      url: jdbc:hsqldb:mem:testdb1
jpa:
  units:
    myUnit:
      datasource: myDatasource
      properties:
        hibernate.dialect: org.hibernate.dialect.HSQLDialect
        hibernate.hbm2ddl.auto: update
classes:
  org:
    generated:
      project:
        domain:
          model:
            jpaUnit: myUnit
```

