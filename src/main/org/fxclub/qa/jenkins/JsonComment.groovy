package org.fxclub.qa.jenkins

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class JsonComment {

    def line
    def value

    JsonComment(){

    }
}
