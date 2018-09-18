---
title: "JPA with the business framework"
addon: "JPA"
repo: "https://github.com/seedstack/jpa-addon"
author: Adrien LAUER
weight: -1
tags:
    - persistence
    - transactions
    - domain-driven design
zones:
    - Addons
menu:
    JPA:
        parent: "contents"
        weight: 10
---

While you can do plain JPA by injecting `EntityManager` anywhere, it is better to define an architectural layer where you 
encapsulate persistence-related operations. In the business framework, persistence is confined to 
[Repositories]({{< ref "docs/business/repositories.md" >}}).    

## JPA repository

### Default JPA repository

The JPA add-on will provide a default JPA repository implementation for every aggregate that does not have a custom
one. Two cases may occur:

* You don't have any custom repository interface an particular aggregate. In that case a JPA implementation of
the base {{< java "org.seedstack.business.domain.Repository" >}} interface is provided.
* You have a custom repository interface and all its methods are implemented as default methods (no abstract method remaining).
In that case a JPA implementation of your interface is provided.

### Custom JPA repository

To define a custom JPA repository, first write the custom repository interface:
 
```java
public interface CustomerRepository extends Repository<Customer, CustomerId> { 
    Customer findCustomerByName(String name);
}
```

Then implement the interface in a class extending {{< java "org.seedstack.jpa.BaseJpaRepository" >}}. That way, you'll
only have to implement your custom methods: 

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

### Usage

{{% callout info %}}
JPA implementations are always injected by specifying the {{< java "org.seedstack.jpa.Jpa" "@" >}} annotation at the
injection point.
{{% /callout %}}

You can inject a JPA repository implementation through the {{< java "org.seedstack.business.domain.Repository" >}} like this:

```java
public class SomeClass {
    @Inject
    @Jpa
    private Repository<Customer, CustomerId> customerRepository;
}
```

If you have a custom repository interface, you can inject a JPA implementation of it like this:

```java
public class SomeClass {
    @Inject
    @Jpa
    private CustomerRepository customerRepository;
}
```

{{% callout tips %}}
The framework will always choose the most complete implementation available. This means that if you have a custom implementation,
it will always be preferred for injection over any default implementation.
{{% /callout %}}

Refer to the {{< java "org.seedstack.business.domain.Repository" >}} interface documentation for details about its
usage.

## Sequence generation

The JPA add-on provides implementations of the business framework {{< java "org.seedstack.business.util.SequenceGenerator" >}}
to enable the use of a database sequence to generate an identity. 

### Mark the identity field

To mark an entity field as its identity, add the {{< java "org.seedstack.business.domain.Identity" "@" >}} annotation: 

```java
public class MyAggregate extends BaseAggregateRoot<Long> {
    @Identity(generator = SequenceGenerator.class)
    private Long id;
}
```

### Choose a generator implementation

While the {{< java "org.seedstack.business.domain.Identity" "@" >}} annotation tells the framework that it must generate
an identity, it doesn't tell it *how*. This can be done by adding a qualifier annotation on the field:

```java
public class MyAggregate extends BaseAggregateRoot<Long> {
    @Identity(generator = SequenceGenerator.class)
    @Named("postgreSqlSequence")
    private Long id;
}
``` 

The following implementations are available:

* **PostgreSQL**: use the `@Named("postgreSqlSequence")` qualifier.
* **Oracle**: use the `@Named("oracleSequence")` qualifier.

### Specify the database sequence name

The generator implementation must know which database sequence to use. To specify it, use the `identitySequenceName` tag
in [class configuration]({{< ref "docs/core/configuration.md#class-configuration" >}}):

```yaml
classes:
  org:
    generated:
      project:
        domain:
          model:
            myaggregate:
              MyAggregate:
                identitySequenceName: MY_SEQUENCE
``` 

### Usage

Refer to the [business framework identity generation documentation]({{< ref "docs/business/factories.md#identity-generation" >}}) for 
instructions about how to actually use the chosen sequence generator when creating entities. 

## Example

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
    
    @Transactional
    @JpaUnit("myUnit")
    public void sendEmail(CustomerId customerId, String content) {
        Customer customer = customerRepository.get(customerId)
                            .orElseThrow(() -> new CustomerNotFoundException(customerId));
        
        // ... do the work
    }
}
```
