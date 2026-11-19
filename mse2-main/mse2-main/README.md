# StudentProfileManagementSystem

A Java Spring Boot REST backend for the OOPJ assessment. Maven builds the application and Spring Data JPA persists profiles and skills in MySQL.

## Set up MySQL

Connect to the local MySQL server as `root` in MySQL Workbench. Open a SQL tab and execute the command from [database.sql](database.sql).

```sql
CREATE DATABASE IF NOT EXISTS student_profile_db;
```

Hibernate creates or updates the `students` and `student_technical_skills` tables when the application starts. The skills table stores each skill's position and links to its student through `student_id`.

## Build and run in PowerShell

Run these commands from the project directory. The Windows wrapper supplies Maven. It can download Maven and dependencies on the first run. The project targets Java 17 bytecode and uses a Spring Boot release that supports the detected JDK 27.

```powershell
java -version
.\mvnw.cmd clean verify
```

In the same PowerShell window that will start the application, run the following commands and enter your actual MySQL root password at the prompt.

```powershell
$env:MYSQL_PASSWORD = (New-Object System.Net.NetworkCredential('', (Read-Host 'MySQL root password' -AsSecureString))).Password
.\mvnw.cmd spring-boot:run
```

The API listens at `http://localhost:8080/api/students`. Stop it with Ctrl+C. To clear the password from the current PowerShell session, run `Remove-Item Env:MYSQL_PASSWORD`.

## Test the APIs

Open [requests.http](requests.http) in VS Code with the REST Client extension. Send the first five POST requests to populate sample records, then send the remaining requests in order using **Send Request**. The sample IDs are 7101 through 7105. If you already have data under those IDs, use unused IDs consistently throughout the file.

| Method | Path under `/api/students` | Expected success |
| --- | --- | --- |
| POST | / | 201 Created |
| GET | / | 200 with all profiles |
| GET | /{id} | 200 with one profile |
| PUT | /{id} | 200 with the updated profile |
| DELETE | /{id} | 204 No Content |
| GET | /merit-list | 200 with highest average first |
| GET | /search/email?email=value | 200 with exact, case-insensitive matches |
| GET | /search/course?course=value | 200 with exact, case-insensitive matches |
| GET | /shared-skills | 200 with groups of at least two students |
| GET | /needs-improvement | 200 with profiles matching the subject-score rule |

PUT requires both scores and a skills list. Use an empty list to clear the skills. The error examples cover duplicate IDs (409), invalid scores (400), and a missing student (404). Before the sample update and delete, the merit list should return IDs 7101, 7102, 7105, 7103, and 7104 in that order. Meera (7103) should appear in the needs-improvement report even though her overall category is Satisfactory.

To check persistence, restart the application and send GET for a sample ID you created and kept. You can also run these queries in Workbench.

```sql
SELECT * FROM student_profile_db.students;
SELECT * FROM student_profile_db.student_technical_skills;
```

## Assessment mapping

- **Q1** uses private fields, constructors, getters, setters and `toString()` in `Student`. JPA maps the assigned ID and the list of skills using `@Id` and `@ElementCollection`. The three computed getters appear in the JSON response.
- **Q2** uses `StudentRepository` for MySQL reads and writes. `StudentService` validates scores, checks IDs, handles searches and builds reports. `Collections.sort` and a `Comparator` sort an `ArrayList` for the merit list. A `HashMap` maps each exact skill label to a `List<Student>`.
- **Q3** uses `StudentController` to map HTTP requests to service methods. Spring injects the service into the controller and the repository into the service through their constructors. The update request accepts scores and skills and the service changes only those fields.

See [VIVA.md](VIVA.md) for a short walkthrough and [AI_PROMPTS.txt](AI_PROMPTS.txt) for the original assessment prompt used for this implementation.
