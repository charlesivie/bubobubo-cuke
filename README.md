## Cucumber JVM for the bubobubo service


### Maven

Simply run:

```
mvn test
```

This runs Cucumber features using the JUnit runner. The `@RunWith(Cucumber.class)` annotation on the `RunCukes` junit class
kicks off Cucumber.

#### Overriding options

The Cucumber runtime parses command line options to know what features to run, where the glue code lives, what formatters to use etc.
When you use the JUnit runner, these options are generated from the `@Cucumber.Options` annotation on your test.

Sometimes it can be useful to override these options without changing or recompiling the JUnit class. This can be done with the
`cucumber.options` system property. Here are a couple of examples:

With Maven:

```
mvn -Dcucumber.options="--format junit:target/cucumber-junit-report.xml" test
```
