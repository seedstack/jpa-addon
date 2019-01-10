# Version 4.0.5 (2019-01-10)

* [fix] During translation of composite specifications (or, and), set the JPA expression for each member (avoid "No expression has been set" exception).  

# Version 4.0.4 (2018-12-04)

* [new] If only one JPA unit is configured, automatically make all JPA entities belong to it. 
* [chg] Built and tested with OpenJDK 11 (minimum Java version still being 8).

# Version 4.0.3 (2018-08-06)

* [chg] Only use JSR-330 injection in sequence generators.
* [chg] Moved `OracleSequenceGenerator` and `PostgreSequenceGenerator` to `org.seedstack.jpa.identity` package and made them public. 

# Version 4.0.2 (2018-07-30)

* [chg] Always bind entity manager even without JPA unit configured.
* [fix] Do not detect classes implementing `JpaRepositoryFactory` if business framework is not available.

# Version 4.0.1 (2018-02-22)

* [new] Adds a sequence generator for PostgreSQL.

# Version 4.0.0 (2017-11-30)

* [new] Implements automatic translation of business specifications to JPA criteria. 

# Version 3.0.2 (2017-08-01)

* [new] If [Flyway add-on](http://seedstack.org/addons/flyway) is available, make sure that JPA is initialized after it had the opportunity to migrate the database(s).
* [chg] Improved error message when no JPA unit is specified.

# Version 3.0.1 (2017-02-26)

* [fix] Fix transitive dependency to poms SNAPSHOT.

# Version 3.0.0 (2016-12-13)

* [brk] Update to SeedStack 16.11 new configuration system.
* [brk] Remove deprecated `BaseJpaRangeFinder`
* [brk] Remove the possibility of NOT specifying the data source in `@JpaUnit` if only one is present. Use the `defaultUnit` configuration property instead.
* [brk] Qualifier for Oracle sequence identity handler is renamed from `identity.sequence-name` to `identitySequenceName`.
* [brk] Class configuration attribute for Oracle sequence name is renamed from `identity.oracle-sequence` to `oracleSequence`.

# Version 2.1.3 (2016-04-26)

* [chg] Update for SeedStack 16.4
* [fix] Correctly cleanup `ThreadLocal` in `EntityManagerLink`

# Version 2.1.2 (2016-01-21)

* [chg] Error messages were referencing outdated information.
* [chg] `BaseJpaRangeFinder` is deprecated and replaced by persistence-agnostic `BaseRangeFinder` in the business framework.

# Version 2.1.1 (2015-11-25)

* [chg] Update to work with JDBC add-on 2.1.1+
* [chg] Don't bind EntityManager if no JPA unit is configured

# Version 2.1.0 (2015-11-15)

* [chg] Refactored as an add-on and updated to work with Seed 2.1.0+

# Version 2.0.0 (2015-07-30)

* [new] Initial Open-Source release.
