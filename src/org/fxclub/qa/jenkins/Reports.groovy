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

    def buildAllureReport(){
        buildAllureReport('Allure Commandline','target/allure-results','target/allure-report')
    }

    def buildAllureReport(allureCommandlineToolName, resultsPath, reportsPath){
        steps.echo "Building Allure Report: using ${allureCommandlineToolName}"
        steps.echo "results: ${resultsPath}, reports: ${reportsPath}"

        def allureHome = steps.tool "${allureCommandlineToolName}"
        def allure = "${allureHome}/bin/allure"

        steps.sh "${allure} generate ${resultsPath} -o ${reportsPath}"
    }

    def storeAllureReport(){
        storeAllureReport('target/allure-report')
    }

    def storeAllureReport(reportsPath){
        steps.echo "Publishing Allure Reports: ${reportsPath}"
        steps.publishHTML([allowMissing: false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: "${reportsPath}", reportFiles: 'index.html', reportName: 'Allure Report'])
    }

}