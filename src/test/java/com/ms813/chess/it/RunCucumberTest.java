package com.ms813.chess.it;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources",
    plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class RunCucumberTest {
}

