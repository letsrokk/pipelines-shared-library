package org.fxclub.qa.jenkins.internal

class JsonScenario extends JsonBase {

    def line
    JsonComment[] comments
    def name
    def description
    def id
    def type
    def keyword
    JsonStep[] steps
    JsonTag[] tags

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
