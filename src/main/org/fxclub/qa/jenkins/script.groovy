package org.fxclub.qa.jenkins

import com.fasterxml.jackson.databind.ObjectMapper

List<JsonFeature> parseCucumberJsonReport(reportName){
    def mapper = new ObjectMapper()
    def json = mapper.readValue(
            new File("/Users/majer-dy/Documents/IDEA/registration-services/target/cucumber-parallel/${reportName}").text,
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

List<JsonFeature> json1 = parseCucumberJsonReport('13.json')
List<JsonFeature> json2 = parseCucumberJsonReport('15.json')

List<JsonFeature> mergedReport = new ArrayList<>()
mergedReport = mergeReport(mergedReport, json1, json2)
println(mergedReport)
writeReport(mergedReport)
