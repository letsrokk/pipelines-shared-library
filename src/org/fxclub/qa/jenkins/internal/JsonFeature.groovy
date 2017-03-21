package org.fxclub.qa.jenkins.internal

class JsonFeature extends JsonBase {

    Set<JsonComment> comments
    def line
    List<JsonScenario> elements
    def name
    def description
    def id
    def keyword
    def uri
    Set<JsonTag> tags

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

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        JsonFeature that = (JsonFeature) o

        if (!comments.equals(that.comments)) return false
        if (description != that.description) return false
        if (id != that.id) return false
        if (keyword != that.keyword) return false
        if (line != that.line) return false
        if (name != that.name) return false
        if (!tags.equals(that.tags)) return false
        if (uri != that.uri) return false

        return true
    }

    int hashCode() {
        int result
        result = (comments != null ? comments.hashCode() : 0)
        result = 31 * result + (line != null ? line.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        result = 31 * result + (description != null ? description.hashCode() : 0)
        result = 31 * result + (id != null ? id.hashCode() : 0)
        result = 31 * result + (keyword != null ? keyword.hashCode() : 0)
        result = 31 * result + (uri != null ? uri.hashCode() : 0)
        result = 31 * result + (tags != null ? tags.hashCode() : 0)
        return result
    }

}
