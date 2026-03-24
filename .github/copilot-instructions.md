# Copilot instructions for quanlydienmay

## Project snapshot
- Java desktop app (JavaFX + Hibernate), **not** Spring Boot.
- Main UI entrypoint: `com.mycompany.quanlydienmay.main.JavaFxApp`.
- Data seeding/bootstrap entrypoint: `com.mycompany.quanlydienmay.main.MainApp`.
- Layering is simple: JavaFX controllers -> DAO classes -> Hibernate entities/MySQL.

## Architecture and data flow
- Login flow is controller-driven: `LoginController.handleLogin()` -> `AccountDAO.findByUsernameAndPassword()` -> `SessionManager.login()` -> open `dashboard.fxml`.
- Dashboard is a single large controller (`DashboardController`) orchestrating all tabs (Category/Product/Order/Account).
- Role-based UI permissions are enforced in controller logic (`SessionManager.isAdmin()`), including tab disable and action guards.
- DAOs own persistence calls; controller methods call DAO `findAll/save/delete` directly (no service layer).
- Order totals are computed from item rows (`Order.getTotalAmount()`), matching BCNF notes (no stored order total column).

## Persistence conventions (important)
- Hibernate is configured in both:
  - `src/main/resources/hibernate.cfg.xml` (DB URL, credentials, mapped classes)
  - `HibernateUtils` (explicit `.addAnnotatedClass(...)` list)
- If you add a new entity, update **both** places above.
- DAO pattern used across the project:
  - `try (Session session = HibernateUtils.getSessionFactory().openSession()) { ... }`
  - start/commit transaction only for write operations (`saveOrUpdate`, `delete`).
- HQL frequently uses `LEFT JOIN FETCH` (see `ProductDAO.findAll()`, `OrderDAO.findAll()`) to avoid lazy-loading issues in JavaFX tables.

## UI/controller patterns
- FXML files live in `src/main/resources/com/mycompany/quanlydienmay/`.
- Controllers use `@FXML` handlers and load scenes with classpath resources (`/com/mycompany/quanlydienmay/*.fxml`).
- In dashboard tables, data flow is: base `ObservableList` -> `FilteredList` -> manual paged `ObservableList` (PAGE_SIZE = 10).
- Search behavior normalizes to lowercase+trim via `normalize()` before filtering.
- UI copy/messages are Vietnamese; keep new labels/alerts consistent.

## Build, run, debug workflows
- Run app: `mvn clean javafx:run`
- Debug app (wait for debugger): `mvn clean javafx:run@debug` (JDWP on `localhost:8000`)
- NetBeans IDE debug profile uses `javafx:run@ide-debug` (see `nbactions.xml`).
- Seed sample data via Java main execution: `mvn exec:java@mainapp`
- Tests: `mvn test` (project currently has no test classes under `src/test/java`).

## Project-specific gotchas
- Current login checks username/password directly in DB (plain comparison in `AccountDAO`), so authentication changes impact existing seed/demo credentials.
- `hibernate.hbm2ddl.auto=update` is enabled; schema evolves automatically at startup.
- `Order.orderDate` is marked `@Transient` in entity while SQL docs define `orders.order_date`; keep this mismatch in mind when changing order date behavior.
- DB is expected locally as `ElectronicsStoreDB` on MySQL (`com.mysql.cj.jdbc.Driver`).

## Useful reference files
- `src/main/java/com/mycompany/quanlydienmay/main/DashboardController.java`
- `src/main/java/com/mycompany/quanlydienmay/dao/*.java`
- `src/main/java/com/mycompany/quanlydienmay/model/*.java`
- `docs/bcnf-notes.md` and `docs/bcnf-schema.sql`
