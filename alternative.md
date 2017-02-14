---
title: "Alternate configuration"
name: "JPA"
repo: "https://github.com/seedstack/jpa-addon"
tags:
    - "jpa"
    - "persistence"
    - "data"
    - "database"
    - "relational"
    - "unit"
    - "xml"
zones:
    - Addons
menu:
    AddonJPA:
        weight: 20
---

As an alternative to the [automatic configuration](<{{ ref "addons/jpa/index.md#configuration" >}}) you can let JPA manage its own datasource instead of referencing
one defined in the JDBC add-on. In this case you must provide a `persistence.xml` file. This file has to be placed under
the `META-INF` directory of your classpath (for instance in `src/main/resources/META-INF`):

```xml
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
             http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd"
             version="2.1">

    <persistence-unit name="my-jpa-unit" transaction-type="RESOURCE_LOCAL">
        <class>org.seedstack.jpa.sample.Item1</class>
    </persistence-unit>

</persistence>
```

In the example above you can find:

* The JPA version (2.1 in this example)
* A unit named `my-jpa-unit`
* A local transaction type (`RESOURCE_LOCAL`)
* The list of persistence classes to map

You can declare as many units as required in a `persistence.xml` file. You can also add configuration properties directly
in this file, although it is recommended to specify them in the configuration. When using a `persistence.xml` file, you
must either specify a datasource via properties or via JNDI.

# Option 1: datasource via properties

The datasource can be specified through properties, either in the configuration:

```ini
[org.seedstack.jpa.unit.my-jpa-unit]
property.javax.persistence.jdbc.driver = ...
property.javax.persistence.jdbc.url = ...
property.javax.persistence.jdbc.user = ...
property.javax.persistence.jdbc.password = ...
```

Or in the directly in the `persistence.xml` file:

```xml
<persistence-unit name="my-jpa-unit" transaction-type="RESOURCE_LOCAL">
    ...

    <properties>
        <property name="..." value="..."/>
    </properties>

    ...
</persistence-unit>
```

The specification of properties in the configuration is recommended as it allows greater flexibility (access to
environment variables and system properties, usage of configuration profiles, macros, ...).

# Option 2: datasource via JNDI

In some environments like in a Web server, it may be preferable to use JNDI instead of configuration properties. You can
do so by specifying the JNDI name of the datasource in the `persistence.xml` file:

```xml
<non-jta-data-source>java:comp/env/jdbc/my-datasource</non-jta-data-source>
```

In case of a JTA data source, use following line instead:

```xml
<jta-data-source>java:comp/env/jdbc/my-datasource</jta-data-source>
```

In case of a Web application, add the following JNDI reference in your `web.xml` file:

```xml
<resource-ref>
    <res-ref-name>jdbc/my-datasource</res-ref-name>
    <res-type>javax.sql.DataSource</res-type>
    <res-auth>Container</res-auth>
</resource-ref>
```

You may need to add additional files depending on your Web container. Please refer to the the dedicated container
documentation.
