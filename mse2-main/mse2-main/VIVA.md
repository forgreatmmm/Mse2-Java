# Viva notes

- **Encapsulation.** `Student` keeps its fields private. The constructors create an object, getters read its values, and setters change its values.
- **Average and category.** Add both scores and divide by 2.0. Check the thresholds from highest to lowest. An average of 85 earns Excellent, 70 earns Good, and 50 earns Satisfactory. Anything below 50 earns Needs Improvement.
- **Stronger subject.** Compare the two scores. Equal scores return Both Equal.
- **Persistence.** `StudentRepository` extends `JpaRepository`. Spring Data handles persistence, while Hibernate maps `Student` and its list of skills to MySQL tables. The assigned ID is the primary key. `Persistable` tells Spring Data to insert a newly created student.
- **Merit list.** Copy students into an `ArrayList`. A `Comparator` compares averages from highest to lowest, then IDs from lowest to highest for ties. `Collections.sort` applies this order.
- **Shared skills.** Each `HashMap` key is a skill and each value is a list of students. A student appears once per skill. Return groups with at least two students.
- **Subject-specific improvement.** Use a loop to collect students with programming below 50 and mathematics at least 75. This rule is independent of the student's overall category.
- **Create and update.** Both scores must be within 0 to 100 inclusive. A duplicate ID returns 409. For an update, the service loads the student by ID and sets the two scores and skills. JPA writes the changes when the transaction commits.
- **Dependency injection and REST.** Spring supplies the controller's service and the service's repository through their constructors. The controller maps HTTP methods to operations and responds with JSON or an HTTP status.
