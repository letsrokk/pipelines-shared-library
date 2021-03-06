package org.fxclub.qa.jenkins

import com.fasterxml.jackson.databind.ObjectMapper
import org.fxclub.qa.jenkins.internal.JsonFeature

class Cucumber implements Serializable {

    def steps
    Cucumber(steps){
        this.steps = steps
    }

    List<JsonFeature> parseCucumberJsonReport(file){
        def jsonString = steps.readFile("${file.getPath()}")
        ObjectMapper mapper = new ObjectMapper()
        List<JsonFeature> json = mapper.readValue(
                jsonString,
                JsonFeature[]
        )
        return json
    }

    String writeReport(List<JsonFeature> report){
        writeReport(report, 'target/cucumber-report')
    }

    String writeReport(List<JsonFeature> report, path){
        ObjectMapper mapper = new ObjectMapper()
        String cucumberReportJson = "${path}/cucumber.json"
        steps.echo "Write Cucumber JSON Report: ${cucumberReportJson}"
        String jsonReport = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report)
        steps.writeFile file: "${cucumberReportJson}", text: "${jsonReport}"
        return cucumberReportJson
    }

    List<JsonFeature> mergeReport(List<JsonFeature> mergedReport, List<JsonFeature>... toMergeCollections){
        for(List<JsonFeature> toMergeFeatures : toMergeCollections){
            if(mergedReport.size() == 0){
                mergedReport.addAll(toMergeFeatures)
                continue
            }
            for(JsonFeature toMergeFeature : toMergeFeatures){
                def matched = false
                for(JsonFeature baseFeature : mergedReport){
                    if(baseFeature.equals(toMergeFeature)){
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

    List<JsonFeature> getReport(){
        getReport('target/cucumber-parallel')
    }

    List<JsonFeature> getReport(path){
        List<JsonFeature> mergedReport = new ArrayList<JsonFeature>()
        steps.echo "Merge Cucumber JSON reports: ${path}"
        def files = steps.findFiles glob: "${path}/*.json"
        for(int i = 0; i < files.length; i++){
            List<JsonFeature> features = parseCucumberJsonReport(files[i])
            mergedReport = mergeReport(mergedReport, features)
        }
        return mergedReport
    }

    String mergeCucumberJSONReports(){
        mergeCucumberJSONReports('target/cucumber-parallel')
    }

    String mergeCucumberJSONReports(path){
        List<JsonFeature> mergedJSON = getReport(path)
        return writeReport(mergedJSON)
    }

}