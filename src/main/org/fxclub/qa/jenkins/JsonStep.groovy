package org.fxclub.qa.jenkins

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class JsonStep {

    JsonStepResult result
    def line
    def name
    JsonStepMatch match
    int[] matchedColumns
    JsonStepRow[] rows
    def keyword

    @JsonInclude(JsonInclude.Include.NON_NULL)
    class JsonStepResult{
        def duration
        def status
        def error_message

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