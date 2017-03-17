package org.fxclub.qa.jenkins

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

def reports = new Reports()
Collection<JsonFeature> json1 = reports.parseCucumberJsonReport('13.json')
Collection<JsonFeature> json2 = reports.parseCucumberJsonReport('15.json')

List<JsonFeature> mergedReport = new ArrayList<>()
mergedReport = mergeReport(mergedReport, json1, json2)
println(mergedReport)
reports.writeReport(mergedReport)
