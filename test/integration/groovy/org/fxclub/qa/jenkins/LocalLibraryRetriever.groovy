package org.fxclub.qa.jenkins

import hudson.FilePath
import hudson.model.Run
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.libs.LibraryRetriever

import javax.annotation.Nonnull

class LocalLibraryRetriever extends LibraryRetriever{

    private final FilePath dir;

    LocalLibraryRetriever(){
        this(new FilePath(new File(".")))
    }

    LocalLibraryRetriever(final FilePath dir) {
        this.dir = dir
    }

    @Override
    void retrieve(
            @Nonnull String name,
            @Nonnull String version,
            @Nonnull boolean changelog,
            @Nonnull FilePath target, @Nonnull Run<?, ?> run, @Nonnull TaskListener listener) throws Exception {
        dir.copyRecursiveTo("src/**/*.groovy,vars/*.groovy,vars/*.txt,resources/", null, target);
    }

    @Override
    void retrieve(
            @Nonnull String name,
            @Nonnull String version,
            @Nonnull FilePath target, @Nonnull Run<?, ?> run, @Nonnull TaskListener listener) throws Exception {
        dir.copyRecursiveTo("src/**/*.groovy,vars/*.groovy,vars/*.txt,resources/", null, target);
    }
}
