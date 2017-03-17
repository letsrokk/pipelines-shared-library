package org.fxclub.qa.jenkins

class JsonStep {

    JsonStepResult result
    def line
    def name
    JsonStepMatch match
    int[] matchedColumns
    def keyword

    class JsonStepResult{
        def duration
        def status

        JsonStepResult(){

        }
    }

    @Override
    String toString() {
        return "JsonStep{" +
                "name=" + name +
                '}';
    }

}