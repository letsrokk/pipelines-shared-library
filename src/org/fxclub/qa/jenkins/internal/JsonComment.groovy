package org.fxclub.qa.jenkins.internal

class JsonComment extends JsonBase {

    def line
    def value

    JsonComment(){

    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        JsonComment that = (JsonComment) o

        if (line != that.line) return false
        if (value != that.value) return false

        return true
    }

    int hashCode() {
        int result
        result = (line != null ? line.hashCode() : 0)
        result = 31 * result + (value != null ? value.hashCode() : 0)
        return result
    }
}
