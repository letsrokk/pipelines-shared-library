package org.fxclub.qa.jenkins.internal.cucumber

class JsonTag extends JsonBase {

    def line
    def name

    JsonTag(){

    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        JsonTag jsonTag = (JsonTag) o

        if (line != jsonTag.line) return false
        if (name != jsonTag.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (line != null ? line.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        return result
    }
}
