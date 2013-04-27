package uk.co.bubobubo.cuke;

import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@Cucumber.Options(tags = {"@Wip"}, format = {
        "pretty", "html:target/cucumber-html-report", "json-pretty:target/cucumber-json-report.json"
})
public class RunWip {}
