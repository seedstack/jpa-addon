---
title: "Basics"
name: "JPA"
repo: "https://github.com/seedstack/jpa-addon"
author: "SeedStack"
description: "Provides configuration, injection and transactions for Java Persistence API 1.0, 2.0 and 2.1."
min-version: "15.11+"
backend: true
aliases:
    - /docs/seed/manual/persistence/jpa
weight: -1
tags:
    - "jpa"
    - "persistence"
    - "data"
    - "database"
    - "relational"
    - "unit"
zones:
    - Addons
menu:
    AddonJPA:
        weight: 10
---

Seed JPA persistence add-on enables your application to interface with any relational database through a JPA-compliant
ORM. Note that:

* This version doesn't enforce a specific JPA version. It is currently tested with JPA 1.0, JPA 2.0 and JPA 2.1.
* This add-on is compatible with any ORM implementation. 

{{< dependency g="org.seedstack.addons.jpa" a="jpa" >}}

{{% callout tips %}}
If you want to use the popular [Hibernate ORM](http://hibernate.org/orm/), use the following Maven dependency:

    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>...</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-entitymanager</artifactId>
        <version>...</version>
    </dependency>

Only add the JPA specification to declare entity classes in any module that does not have the hibernate dependency:

    <dependency>
        <groupId>org.hibernate.javax.persistence</groupId>
        <artifactId>hibernate-jpa-2.1-api</artifactId>
        <version>1.0.0.Final</version>
        <scope>provided</scope>
    </dependency>
{{% /callout %}}

# Configuration

The JPA add-on doesn't need any `persistence.xml` file in its default mode of operation as it will automatically generate
persistence unit information. First, declare the list of your persistence units in the configuration:

```ini
[org.seedstack.jpa]
units = my-jpa-unit, ...
```
    
The you must reference a JDBC datasource for each JPA unit. To do so, please refer to the [JDBC add-on configuration]
(../jdbc):

```ini
[org.seedstack.jpa.unit.my-jpa-unit]
datasource = my-datasource
```

Note that Seed has no way of knowing to which persistence unit belong each entity class, so you must indicate this with
the following configuration:

```ini
[org.myorganization.myapp.domain.*]
jpa-unit = my-jpa-unit
```

This will put all the entities scanned in the `org.myorganization.myapp.domain` package and its subpackages into the
`my-jpa-unit` persistence unit.

## Options

You can specify the type of transactions by using the following configuration
([more info](http://docs.oracle.com/javaee/6/api/javax/persistence/spi/PersistenceUnitInfo.html#getTransactionType%28%29)):

```ini
[org.seedstack.jpa.unit.my-jpa-unit]
transaction-type = JTA | RESOURCE_LOCAL
```

If you prefer to use XML JPA mapping files instead of annotations you can specify them with the following configuration
([more info](http://docs.oracle.com/javaee/6/api/javax/persistence/spi/PersistenceUnitInfo.html#getMappingFileNames%28%29)):

```ini
[org.seedstack.jpa.unit.my-jpa-unit]
mapping-files = path/to/mapping/file1.xml, path/to/mapping/file2.xml, ...
```

You can specify the validation mode with the following configuration
([more info](http://docs.oracle.com/javaee/6/api/javax/persistence/spi/PersistenceUnitInfo.html#getValidationMode%28%29)):

```ini
[org.seedstack.jpa.unit.my-jpa-unit]
validation-mode = path/to/mapping/file1.xml, path/to/mapping/file2.xml, ...
```

You can specify the shared cache mode with the following configuration
([more info](http://docs.oracle.com/javaee/6/api/javax/persistence/spi/PersistenceUnitInfo.html#getSharedCacheMode%28%29)):

```ini
[org.seedstack.jpa.unit.my-jpa-unit]
shared-cache-mode = ALL | NONE | ENABLE_SELECTIVE | DISABLE_SELECTIVE | UNSPECIFIED
```

## Properties

If you need to pass any property to the persistence unit, you can do so with the following configuration:

```ini
[org.seedstack.jpa.unit.my-jpa-unit]
property.name.of.the.property1 = value-of-the-property1
property.name.of.the.property2 = value-of-the-property2
...
```

# Using the Entity Manager

To use the Entity Manager in your code, simply inject it:

```java
public class MyRepository {

    @Inject
    private EntityManager entityManager;

    ...
}
```

All JPA interactions have to be realized inside a transaction. Refer to the [transaction support 
documentation](/docs/seed/manual/transactions) for more detail. Below is an example using the annotation-based transaction 
demarcation (notice the `persistence.xml` unit name in {{< java "org.seedstack.jpa.JpaUnit" "@" >}} annotation)

```java
public class MyService {

    @Inject
    private MyRepository myRepository;

    @Transactional
    @JpaUnit("my-jpa-unit")
    public void doSomethingWithMyJpaUnit() {

    }
}
```

{{% callout info %}}
Note that the {{< java "org.seedstack.jpa.JpaUnit" "@" >}} annotation is NOT optional as the JPA add-on includes the JDBC add-on as a dependency, so the
conditions that you must have only one type of transactional resources in your application cannot be fulfilled. You can omit the 
name of the unit if you only have one unit in your application, although we recommend you to always specify it explicitly. 
{{% /callout %}}


