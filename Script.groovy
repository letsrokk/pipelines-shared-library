package org.fxclub.qa.jenkins

import org.fxclub.qa.jenkins.internal.cucumber.JsonFeature
Cucumber cucumber = new Cucumber()

List<JsonFeature> mergedReport = new ArrayList<>()
def reportsDir = new File('/Users/majer-dy/Documents/IDEA/registration-services/target/cucumber-parallel')
reportsDir.eachFileRecurse(groovy.io.FileType.FILES) {
    if(it.name.endsWith('.json')) {
        List<JsonFeature> features = cucumber.parseCucumberJsonReport(it.getAbsolutePath())
        mergedReport = cucumber.mergeReport(mergedReport, features)
    }
}

cucumber.writeReport(mergedReport)
