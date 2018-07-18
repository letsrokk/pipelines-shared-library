package org.fxclub.qa.jenkins

import hudson.model.ParametersAction

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

    def publishTestNGReport(){
        steps.junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
        steps.publishHTML(target: [allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'target/surefire-reports', reportFiles: 'emailable-report.html', reportName: 'TestNG Report'])
    }

    def storeAllureReport(){
        storeAllureReport('Allure2 Commandline','target/allure-results')
    }

    def storeAllureReport(allureCommandlineToolName, resultsPath){
        steps.sh "rm -rf allure-report/"

        steps.echo "Adding Allure Categories"
        try{
            def categories = new File(getClass().getResource('/allure/categories.json').toURI()).getText('UTF-8')
            steps.writeFile encoding: 'UTF-8', file: "${resultsPath}/categories.json", text: categories
        }catch (Exception e){
            steps.echo "${e}"
        }
        steps.echo "Publishing Allure Reports"
        steps.allure commandline: "${allureCommandlineToolName}", jdk: '', results: [[path: "${resultsPath}"]]
        archiveAllureResults(resultsPath)
    }

    def archiveAllureResults(resultsPath){
        def exists = steps.fileExists 'allure-results.zip'
        if(exists){
            steps.fileOperations([steps.fileDeleteOperation(excludes: '', includes: 'allure-results.zip')])
        }
        steps.zip archive: true, dir: "${resultsPath}", glob: '', zipFile: "allure-results.zip"
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

    def converJobParamsToMap(){
        def paramsMap = [:]
        def myparams = steps.currentBuild.rawBuild.getAction(ParametersAction)
        for( p in myparams ) {
            paramsMap[p.name.toString()] = p.value.toString()
        }
        steps.echo "Job Params: " + paramsMap
        return ["job_parameters":paramsMap]
    }

    def exportToInfluxDb(){
        exportToInfluxDb(null)
    }

    def exportToInfluxDb(String customPrefix){
        def allureMap = convertAllureInfluxDbExportToMap()
        def paramsMap = converJobParamsToMap()
        def customMap = allureMap << paramsMap
        exportToInfluxDb('test-executions', customPrefix, null, customMap)
    }

    def exportToInfluxDb(target, customPrefix, customData, customMap){
        steps.echo("InfluxDB: " + target)
        steps.echo("Map: " + customMap)
        steps.step([$class: 'InfluxDbPublisher',
              customData: customData,
              customDataMap: customMap,
              customPrefix: customPrefix,
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