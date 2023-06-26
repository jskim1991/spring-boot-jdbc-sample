## Domain Driven Design and Relational Databases

All Spring Data modules are inspired by the concepts of “repository”, “aggregate”, and “aggregate root” from Domain Driven Design. 
These are possibly even more important for Spring Data JDBC, because they are, to some extent, 
contrary to normal practice when working with relational databases.

An aggregate is a group of entities that is guaranteed to be consistent between atomic changes to it.

Each aggregate has exactly one aggregate root, which is one of the entities of the aggregate. 
The aggregate gets manipulated only through methods on that aggregate root. 
These are the atomic changes mentioned earlier.

A repository is an abstraction over a persistent store that looks like a collection of all the aggregates of a certain type. 
For Spring Data in general, this means you want to have one `Repository` per aggregate root. 
In addition, for Spring Data JDBC this means that all entities reachable from an aggregate root are considered to be part of that aggregate root. 
Spring Data JDBC assumes that only the aggregate has a foreign key to a table storing non-root entities of the aggregate and no other entity points toward non-root entities.

Reference: https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/#jdbc.domain-driven-design

## JDBC vs JPA

### JPA: 
- Tracks changes to entities
- Does lazy loading for you
- Difficult...

### JDBC:
- Load an entity = SQL statements get run. Completely loaded (no lazy loading or caching)
- Save an entity = gets saved. No dirty checking and no sessions
- Maybe only for simple table structures?
- Supports 1:1 and 1:N relationships but not N:1 or N:M


### Entity State Detection Strategies
|Strategy| Description                                                                                                                                                                                                                                                                                                                                            |
|---|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|`@Id`-Property inspection (the default)| By default, Spring Data inspects the identifier property of the given entity. If the identifier property is `null` or `0` in case of primitive types, then the entity is assumed to be new. Otherwise, it is assumed to not be new.                                                                                                                        |
|`@Version`-Property inspection| If a property annotated with `@Version` is present and `null`, or in case of a version property of primitive type `0` the entity is considered new. If the version property is present but has a different value, the entity is considered to not be new. If no version property is present Spring Data falls back to inspection of the identifier property. |
|Implementing `Persistable`| If an entity implements `Persistable`, Spring Data delegates the new detection to the `isNew(…)` method of the entity. See the Javadoc for details. Note: Properties of `Persistable` will get detected and persisted if you use `AccessType.PROPERTY`. To avoid that, use `@Transient`.                                                                         |
|Providing a custom `EntityInformation` implementation|You can customize the EntityInformation abstraction used in the repository base implementation by creating a subclass of the module specific repository factory and overriding the `getEntityInformation(…)` method. You then have to register the custom implementation of module specific repository factory as a Spring bean. Note that this should rarely be necessary.|

## Demo Steps

1. Create a new customer
```shell
curl -X POST http://localhost:8080/customers \
-d '{"name": "Jay"}' \
-H 'Content-Type: Application/json'
```

2. Update the customer
```shell
curl -X PATCH http://localhost:8080/customers/Jay \
-d '{"total": 199}' \
-H 'Content-Type: Application/json' | jq
```

3. Query customer
```shell
curl http://localhost:8080/customers | jq
```
```shell
curl http://localhost:8080/customers/Jay | jq
```


## Questions
- Case sensitive for table names and column names?
- How will be the performace compared to JPA?
- 