package org.fxclub.qa.jenkins

import groovy.io.FileType

class Downloader {

    def steps

    Downloader(steps){
        this.steps = steps
    }

    def downloadJenkinsArtifacts(String buildUrl, def extensions=[], def user='', def passwd=''){
        steps.sh "wget --http-user=${user} --http-password=${passwd} -O artifacts.zip ${buildUrl}artifact/*zip*/archive.zip"

        steps.fileOperations([
                steps.fileUnZipOperation(filePath: 'artifacts.zip', targetLocation: 'unzipBuilds')
        ])

        def files = []
        if(extensions.size > 0) {
            extensions.each{
                files.addAll(steps.findFiles(glob: "unzipBuilds/**/*${it}"))
            }
        } else {
            files.addAll(steps.findFiles(glob: "unzipBuilds/**/*"))
        }

        return files
    }
}
