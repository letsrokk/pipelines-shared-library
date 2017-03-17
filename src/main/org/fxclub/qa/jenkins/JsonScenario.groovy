package org.fxclub.qa.jenkins

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class JsonScenario {

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
