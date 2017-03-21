package org.fxclub.qa.jenkins.internal

class JsonFeature extends JsonBase {

    List<JsonComment> comments
    def line
    List<JsonScenario> elements
    def name
    def description
    def id
    def keyword
    def uri
    List<JsonTag> tags

    JsonFeature(){

    }

    @Override
    String toString() {
        return "JsonFeature{" +
                "name=" + name +
                ", id=" + id +
                ", keyword=" + keyword +
                ", elements=" + elements +
                '}'
    }

    boolean isSame(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        JsonFeature that = (JsonFeature) o

        if (comments != that.comments) return false
        if (description != that.description) return false
        if (id != that.id) return false
        if (keyword != that.keyword) return false
        if (line != that.line) return false
        if (name != that.name) return false
        if (tags != that.tags) return false
        if (uri != that.uri) return false

        return true
    }

}
