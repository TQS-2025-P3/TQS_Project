package tqs.project;

import io.cucumber.junit.platform.engine.Constants;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "tqs.project.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, json:reports/cucumber.json")
public class CucumberIT {

}
