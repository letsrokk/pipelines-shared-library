package org.fxclub.qa.jenkins.internal

class JsonScenario extends JsonBase {

    def line
    List<JsonComment> comments
    def name
    def description
    def id
    def type
    def keyword
    List<JsonStep> steps
    List<JsonTag> tags

    JsonScenario(){

    }

    @Override
    String toString() {
        return "JsonScenario{" +
                "name=" + name +
                ", keyword=" + keyword +
                ", steps=" + steps +
                '}'
    }
}
