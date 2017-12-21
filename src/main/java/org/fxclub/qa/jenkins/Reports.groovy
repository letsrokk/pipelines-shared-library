#!/usr/bin/groovy
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

    def convertAllureInfluxDbExportToMap(){
        convertAllureInfluxDbExportToMap('allure-report/export/influxDbData.txt')
    }

    def convertAllureInfluxDbExportToMap(path){
        steps.echo("Allure InfluxDB export:" + path)
        def stringMap = steps.readFile(path)
        steps.echo(stringMap)
        def customMap = [:]
        stringMap.tokenize('\n').each {
            def lines = it.tokenize(' ')
            customMap.put(lines[0], [:])
        }
        stringMap.tokenize('\n').each {
            def lines = it.tokenize(' ')
            def measure = customMap.get(lines[0])
            def value = lines[1].tokenize('=')

            measure.put(value[0],parseValue(value[1]))
            customMap.put(lines[0], measure)
        }
        steps.echo("Allure InfluxDB export map:" + customMap)
        return customMap
    }

    def exportToInfluxDb(){
        exportToInfluxDb('test-executions',convertAllureInfluxDbExportToMap())
    }

    def exportToInfluxDb(target, customMap){
        steps.echo("InfluxDB: " + target)
        steps.echo("Map: " + customMap)
        steps.step([$class: 'InfluxDbPublisher',
              customData: null,
              customDataMap: customMap,
              customPrefix: null,
              target: target])
    }

    def parseValue(value){
        try{
            return Boolean.parseLong(value)
        }catch (Exception ignore){}
        try{
            return Long.parseLong(value)
        }catch (Exception ignore){}
        try{
            return Double.parseLong(value)
        }catch (Exception ignore){}
        return value
    }

}