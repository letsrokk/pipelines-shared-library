package org.fxclub.qa.jenkins

@Grapes([
        @Grab(group='com.fasterxml.jackson.core', module='jackson-core', version='2.8.7'),
        @Grab(group='com.fasterxml.jackson.core', module='jackson-databind', version='2.8.7')
])
import com.fasterxml.jackson.databind.ObjectMapper
import org.fxclub.qa.jenkins.internal.JsonFeature

class Cucumber implements Serializable {

    List<JsonFeature> parseCucumberJsonReport(path){
        def mapper = new ObjectMapper()
        def json = mapper.readValue(
                new File("${path}").text,
                JsonFeature[]
        )
        return json
    }

    def writeReport(List<JsonFeature> report, path = 'target/cucumber-report'){
        ObjectMapper mapper = new ObjectMapper()
        String jsonInString = mapper.writeValueAsString(report)
        File jsonReport = new File("${path}/cucumber.json")
        jsonReport.getParentFile().mkdirs()
        jsonReport.createNewFile()
        mapper.writeValue(jsonReport, report)
    }

    def mergeReport(Collection<JsonFeature> mergedReport, Collection<JsonFeature>... toMergeCollections){
        for(Collection<JsonFeature> toMergeFeatures : toMergeCollections){
            if(mergedReport.size() == 0){
                mergedReport.addAll(toMergeFeatures)
                continue
            }
            for(JsonFeature toMergeFeature : toMergeFeatures){
                boolean matched = false
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

}