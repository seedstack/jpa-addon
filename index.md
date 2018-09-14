---
title: "JPA"
addon: "JPA"
repo: "https://github.com/seedstack/jpa-addon"
author: Adrien LAUER
description: "Provides configuration, injection and transactions for Java Persistence API."
tags:
    - persistence
    - transactions
zones:
    - Addons
noMenu: true    
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

### JPA units

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
 
To allow SeedStack to associate auto-detected JPA classes to a particular JPA unit, you must configure them with a 
[class configuration]({{< ref "docs/core/configuration.md#class-configuration" >}}) tag:

```yaml
classes:
  org:
    myorg:
      myapp:
        domain:
          model:
            jpaUnit: unit1
```

This will assign every class in the `org.myorg.myapp.domain.model` package and its sub-packages to the 
JPA unit `unit1`.

## Usage

To use the Entity Manager directly, simply inject it:

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
All JPA interactions have to be done inside a transaction. Refer to the [transaction documentation]({{< ref "docs/core/transactions.md" >}}) for details. 
{{% /callout %}}

### With the business framework

With the [business framework]({{< ref "docs/business/index.md" >}}), you do your persistence interactions within 
[Repositories]({{< ref "docs/business/repositories.md" >}}). To obtain a JPA-capable implementation of a repository, qualify
the injection point with the {{< java "org.seedstack.jpa.Jpa" "@" >}} annotation:

```java
public class SomeClass {
    @Inject
    @Jpa
    private Repository<Customer, CustomerId> customerRepository;
    
    public void doSomething() {
        // do work with customerRepository
    }
}
```

If you need to write a JPA implementation of a custom repository interface, just extend the {{< java "org.seedstack.jpa.BaseJpaRepository" >}} 
class and only add your custom method(s) implementation(s):

```java
public class CustomerJpaRepository extends BaseJpaRepository<Customer, CustomerId> 
                                   implements CustomerRepository {
    @Override
    public Customer findCustomerByName(String name) {
        EntityManager entityManager = getEntityManager();
        // do work with entityManager
    }
}
```

## Sequence generators

The JPA add-on provides several implementations of the business framework {{< java "org.seedstack.business.util.SequenceGenerator" >}}
to enable the use of a database sequence to generate an identity. 

This is done by adding the {{< java "org.seedstack.business.domain.Identity" "@" >}} annotation along with a qualifier
corresponding to the chosen database implementation:

```java
public class MyAggregate extends BaseAggregateRoot<Long> {
    @Identity(generator = SequenceGenerator.class)
    @Named("postgreSqlSequence")
    private Long id;
}
```

Available implementations are:

* **PostgreSQL**: use the `@Named("postgreSqlSequence")` qualifier.
* **Oracle**: use the `@Named("oracleSequence")` qualifier.

The database sequence name must be specified in [class configuration]({{< ref "docs/core/configuration.md#class-configuration" >}})
as the `identitySequenceName` tag:

```yaml
classes:
  org:
    myorg:
      myapp:
        domain:
          model:
            myaggregate:
              identitySequenceName: MY_SEQUENCE
``` 

{{% callout info %}}
Refer to the [business framework identity generation documentation]({{< ref "docs/business/factories.md#identity-generation" >}}) for 
instructions about how to actually use the chosen sequence generator when creating entities. 
{{% /callout %}}


## Full example

### Connection pool

In addition to the JPA add-on and the Hibernate dependencies, we'll add an HikariCP connection pool:

{{< dependency g="com.zaxxer" a="HikariCP" >}}

### Configuration

Assuming we are using Hibernate and an Hikari connection pool, the configuration below: 

* Defines a unit named `unit1`,
* Using the data-source `datasource1` defined with the [JDBC add-on]({{< ref "addons/jdbc/index.md" >}}),
* Affects every JPA entity of packages `org.myorg.myapp.domain.model.*` to `unit1`. 

```yaml
jdbc:
  datasources:
    datasource1:
      provider: org.seedstack.jdbc.internal.datasource.HikariDataSourceProvider
      url: jdbc:hsqldb:mem:testdb1
jpa:
  units:
    unit1:
      datasource: datasource1
      properties:
        hibernate.dialect: org.hibernate.dialect.HSQLDialect
        hibernate.hbm2ddl.auto: update
classes:
  org:
    myorg:
      myapp:
        domain:
          model:
            jpaUnit: unit1
```

### DDD Aggregate with JPA annotations

This aggregate models a `Customer` with its identity being the `CustomerId` value-object.

The `CustomerId` class: 

```java
import javax.persistence.Embeddable;
import org.seedstack.business.domain.BaseValueObject;

@Embeddable
public class CustomerId extends BaseValueObject {

    private String value;

    private CustomerId() {
        // A default constructor is needed by JPA but can be kept private
    }

    public CustomerId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
```

The `Customer` class: 

```java
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import org.seedstack.business.domain.BaseAggregateRoot;

@Entity
public class Customer extends BaseAggregateRoot<CustomerId> {
    @EmbeddedId
    private CustomerId id;
    private String firstName;
    private String lastName;
    private String address;
    private String deliveryAddress;
    private String password;

    private Customer() {
        // A default constructor is needed by JPA but can be kept private
    }

    public Customer(CustomerId customerId) {
        this.id = customerId;
    }

    @Override
    public CustomerId getId() {
        return id;
    }
}
```

### Aggregate persistence through a JPA repository

The {{< java "org.seedstack.jpa.Jpa" "@" >}} annotation is used to qualify the repository injection so we get a JPA 
implementation of the repository:

```java
@Service
public interface SomeService {
    void sendEmail(CustomerId customerId, String content);
}
```

```java
public class SomeServiceImpl implements SomeService {
    @Inject
    @Jpa
    private Repository<Customer, CustomerId> customerRepository;
    
    public void sendEmail(CustomerId customerId, String content) {
        Customer customer = customerRepository.get(customerId)
                            .orElseThrow(() -> new CustomerNotFoundException(customerId));
        
        // ... do the work
    }
}
```

## Persistence.xml file

Instead of using JPA auto-configuration, you can choose to use a standard `META-INF/persistence.xml` file instead.
This is **NOT recommended** as your loose a significant number of features: 
 
* In this mode, you don't specify a data-source from the JDBC add-on but configure it in the `persistence.xml` file. 
* The `classes`, `mappingFiles`, `provider`, `transactionType`, `sharedCacheMode` and `validationMode` configuration options
have no effect and must be configured in the `persistence.xml` instead (which is mostly static).
* You still have to list every JPA unit in the configuration with a name corresponding to those in the `persistence.xml` file.
* You can still specify provider properties in the configuration. They override properties declared in the `persistence.xml`
file if any.
