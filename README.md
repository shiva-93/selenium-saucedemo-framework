# selenium-saucedemo-framework

Production-style **Selenium 4 + TestNG + Allure + ExtentReports** UI framework for [SauceDemo](https://www.saucedemo.com).

Java **17**, Maven, AssertJ. Drivers via **Selenium Manager** (no WebDriverManager).

---

## How to run

```bash
# Full suite (uses src/test/resources/testng.xml)
mvn clean test

# Browser / headless overrides (system properties → ConfigReader)
mvn clean test -Dbrowser=chrome -Dheadless=true
mvn clean test -Dbrowser=firefox -Dheadless=true
```

### Reports

| Report | Location / command |
|--------|--------------------|
| **Extent** Spark HTML | `target/extent-reports/ExtentReport_*.html` |
| **Screenshots** on failure | `target/screenshots/` |
| **Allure** results | `target/allure-results/` |
| **Allure** live UI | `mvn io.qameta.allure:allure-maven:${allure-maven.version}:serve` (or `mvn allure:serve` if `io.qameta.allure` is in your Maven `pluginGroups`) |
| **Allure** static report | `mvn io.qameta.allure:allure-maven:2.15.0:report` → open `target/site/allure-maven-plugin/index.html` |

---

## Folder structure

```
src/main/java/com/framework/
  config/       ConfigReader (+ package-info)
  driver/       DriverFactory, DriverManager (ThreadLocal)
  pages/        BasePage + SauceDemo page objects (fluent POM)
  models/       CheckoutInfo builder
  utils/        WaitUtils, ScreenshotUtils, ExcelReader
  listeners/    TestListener, RetryAnalyzer, RetryTransformer
  reports/      ExtentManager, AllureEnvironmentWriter
src/test/java/com/tests/
  BaseTest.java
  LoginTest, ProductsTest, CartTest, CheckoutTest
  dataproviders/TestDataProvider.java
  tools/GenerateTestDataExcel.java   # regenerates TestData.xlsx
src/test/resources/
  config.properties, testng.xml, log4j2.xml
  allure.properties, categories.json
  testdata/TestData.xlsx
```

---

## Design patterns (where used)

**Page Object Model (fluent)**  
Private locators; public methods express user intent and return the next page when the journey moves on (`LoginPage.loginAs` → `ProductsPage`, `openCart` → `CartPage`, `checkout` → `CheckoutPage`). Keeps tests free of Selenium APIs.

**Factory**  
`DriverFactory.createDriver()` is the single place that builds Chrome / Firefox / Edge with headless options and page-load timeout (Selenium Manager resolves binaries).

**Singleton + ThreadLocal**  
`DriverManager` and `ExtentManager` expose static access while each TestNG worker thread owns its own `WebDriver` / `ExtentTest` — required for `parallel="methods"`.

**DataProvider (inline + Excel)**  
`TestDataProvider.invalidLoginCredentials` is a hardcoded `Object[][]` for negative login; `validLoginUsers` reads the Login sheet via `ExcelReader` and keeps rows where `expectedOutcome=success`. Both can run with `parallel=true`.

**Builder**  
`CheckoutInfo.builder().firstName(...).lastName(...).postalCode(...).build()` produces an immutable value object for checkout forms — no telescoping constructors in tests.

---

## How to add a new page + test

1. Create `com.framework.pages.YourPage extends BasePage` with **private** `By` locators and **public** `@Step` methods (fluent returns where natural).
2. Add a test class under `com.tests` extending `BaseTest` — only assertions + page calls (no `By`, no `WebDriver`).
3. Register the class in `src/test/resources/testng.xml`.
4. Optional: add Excel columns/sheets and expose them from `TestDataProvider`.

---

## Allure annotations

| Annotation | Role |
|------------|------|
| `@Step("...")` | Step in the Allure timeline (used on page methods / BasePage helpers). Params like `{username}` are interpolated. |
| `@Attachment` | Embeds bytes/text into the report (e.g. failure screenshot helper in `TestListener`). |
| `@Epic` / `@Feature` / `@Story` | Hierarchy for filtering suites in Allure. |
| `@Severity` | Priority badge (`BLOCKER`, `CRITICAL`, …). |
| `@Description` / `@Owner` | Human-readable summary and ownership on each test. |

`AllureEnvironmentWriter` (from `@BeforeSuite`) writes `environment.properties` and copies `categories.json` into `target/allure-results`.

---

## Parallel + retry

- Suite runs with **`parallel="methods"`** and **`thread-count="3"`**.
- `RetryTransformer` applies `RetryAnalyzer` to every `@Test`; max attempts come from `retryCount` in `config.properties` (override with `-DretryCount=2`).
- Counters are keyed by method + parameters (`ConcurrentHashMap`) so parallel retries do not collide.
- Allure keeps **failed attempts visible**; a later pass does not rewrite earlier failures as green.

---

## Config keys (`config.properties`)

`browser`, `baseUrl`, `explicitWaitSeconds`, `pageLoadTimeoutSeconds`, `headless`, `retryCount` — all overridable via `-D` system properties.
