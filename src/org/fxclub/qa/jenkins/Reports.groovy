package org.fxclub.qa.jenkins

class Reports implements Serializable{

    def steps
    Reports(steps){
        this.steps = steps
    }

    def storeCucumberTestNGReport(){
        storeCucumberTestNGReport('target/surefire-reports/junitreports/TEST*.xml')
    }

    def storeCucumberTestNGReport(path){
        steps.echo "Publishing Cucumber JVM reports: ${path}"
        steps.junit allowEmptyResults: true, testResults: "${path}"
    }

    def storeCucumberJSONReport(){
        storeCucumberJSONReport('**/cucumber.json')
    }

    def storeCucumberJSONReport(glob){
        steps.echo "Publishing Cucumber JSON reports: ${glob}"
        steps.step($class: 'CucumberTestResultArchiver', testResults: "${glob}")
    }

    def storeAllureReport(){
        storeAllureReport('Allure2 Commandline','target/allure-results')
    }

    def storeAllureReport(allureCommandlineToolName, resultsPath){
        steps.sh "rm -rf allure-report/"
        steps.echo "Publishing Allure Reports"
        steps.allure commandline: "${allureCommandlineToolName}", jdk: '', results: [[path: "${resultsPath}"]]
    }

}