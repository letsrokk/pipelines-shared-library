package org.fxclub.qa.jenkins

@Grapes([
        @Grab(group='com.fasterxml.jackson.core', module='jackson-core', version='2.8.7'),
        @Grab(group='com.fasterxml.jackson.core', module='jackson-databind', version='2.8.7')
])
import com.fasterxml.jackson.databind.ObjectMapper
import org.fxclub.qa.jenkins.internal.JsonFeature
import com.cloudbees.groovy.cps.NonCPS

class Cucumber implements Serializable {

    def steps

    Cucumber(steps){
        this.steps = steps
    }

    @NonCPS
    List<JsonFeature> parseCucumberJsonReport(path){
        def mapper = new ObjectMapper()
        def json = mapper.readValue(
                new File("${path}").text,
                JsonFeature[]
        )
        return json
    }

    def writeReport(report){
        writeReport(report, 'target/cucumber-report')
    }

    @NonCPS
    def writeReport(report, path){
        ObjectMapper mapper = new ObjectMapper()
//        def jsonInString = mapper.writeValueAsString(report)
//        File jsonReport = new File("${path}/cucumber.json")
//        jsonReport.getParentFile().mkdirs()
//        jsonReport.createNewFile()
//        mapper.writeValue(jsonReport, report)
        def cucumberReportJson = "${path}/cucumber.json"
        steps.echo "${cucumberReportJson}"
        def jsonReport = mapper.writeValueAsString(report)
        steps.writeFile file: "${cucumberReportJson}", text: "${jsonReport}"
    }

    @NonCPS
    def mergeReport(List<JsonFeature> mergedReport, List<JsonFeature>... toMergeCollections){
        for(List<JsonFeature> toMergeFeatures : toMergeCollections){
            if(mergedReport.size() == 0){
                mergedReport.addAll(toMergeFeatures)
                continue
            }
            for(JsonFeature toMergeFeature : toMergeFeatures){
                def matched = false
                for(JsonFeature baseFeature : mergedReport){
                    if(baseFeature.isSame(toMergeFeature)){
                        baseFeature.elements.addAll(toMergeFeature.elements)
                        matched = true
                        break
                    }
                }
                if(!matched){
                    mergedReport.add(toMergeFeature)
                }
            }
        }
        return mergedReport
    }

    @NonCPS
    def getReport(){
        def mergedReport = new ArrayList()
        def reportsDir = new File('/Users/majer-dy/Documents/IDEA/registration-services/target/cucumber-parallel')
        reportsDir.eachFileRecurse(groovy.io.FileType.FILES) {
            if(it.name.endsWith('.json')) {
                List<JsonFeature> features = parseCucumberJsonReport(it.getAbsolutePath())
                mergedReport = mergeReport(mergedReport, features)
            }
        }
        return mergedReport
    }

}