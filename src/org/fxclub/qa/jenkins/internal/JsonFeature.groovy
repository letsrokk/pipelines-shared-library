package org.fxclub.qa.jenkins.internal

import org.fxclub.qa.jenkins.internal.JsonScenario

class JsonFeature extends JsonBase {

    def line
    List<JsonScenario> elements
    def name
    def description
    def id
    def keyword
    def uri

    JsonFeature(){

    }

    @Override
    String toString() {
        return "JsonFeature{" +
                "name=" + name +
                ", id=" + id +
                ", keyword=" + keyword +
                ", elements=" + elements +
                '}';
    }

    boolean isSame(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        JsonFeature that = (JsonFeature) o

        if (description != that.description) return false
        if (id != that.id) return false
        if (keyword != that.keyword) return false
        if (line != that.line) return false
        if (name != that.name) return false
        if (uri != that.uri) return false

        return true
    }
}
