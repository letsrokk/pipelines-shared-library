package org.fxclub.qa.jenkins.internal

@Grapes(
        @Grab(group='com.fasterxml.jackson.core', module='jackson-annotations', version='2.9.0')
)
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class JsonBase implements Serializable {

}
