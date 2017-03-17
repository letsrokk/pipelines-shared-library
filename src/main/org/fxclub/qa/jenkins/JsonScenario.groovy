package org.fxclub.qa.jenkins

class JsonScenario {

    def line
    def name
    def description
    def id
    def type
    def keyword
    JsonStep[] steps

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
