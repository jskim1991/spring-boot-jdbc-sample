## JDBC vs JPA

### JPA: 
- tracks changes to entities
- does lazy loading for you
- Difficult...

### JDBC:
- Load an entity = SQL statements get run. Completely loaded (no lazy loading or caching)
- Save an entity = gets saved. No dirty checking and no sessions
- Maybe only for simple table structures?


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
-H 'Content-Type: Application/json' | jq
```

2. Update the customer
```shell
curl -X PATCH http://localhost:8080/customers/Jay | jq
```

3. Query customer
```shell
curl http://localhost:8080/customers | jq
```
```shell
curl http://localhost:8080/customers/Jay | jq
```