package com.modsen.passengerservice.component;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:features",
        glue = "com/modsen/passengerservice/component"
)
public class CucumberComponentTest {
}
