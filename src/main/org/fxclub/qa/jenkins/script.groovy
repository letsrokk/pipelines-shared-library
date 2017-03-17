package org.fxclub.qa.jenkins

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import static groovy.io.FileType.FILES

List<JsonFeature> parseCucumberJsonReport(path){
    def mapper = new ObjectMapper()
//    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    def json = mapper.readValue(
            new File("${path}").text,
            JsonFeature[]
    )
    return json
}

def writeReport(List<JsonFeature> report){
    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = mapper.writeValueAsString(report);
    mapper.writeValue(new File("merged.json"), report)
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

List<JsonFeature> mergedReport = new ArrayList<>()

def reportsDir = new File('/Users/majer-dy/Documents/IDEA/registration-services/target/cucumber-parallel')
reportsDir.eachFileRecurse(FILES) {
    if(it.name.endsWith('.json')) {
        List<JsonFeature> features = parseCucumberJsonReport(it.getAbsolutePath())
        mergedReport = mergeReport(mergedReport, features)
    }
}

writeReport(mergedReport)
