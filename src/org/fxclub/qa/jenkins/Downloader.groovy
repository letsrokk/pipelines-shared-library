package org.fxclub.qa.jenkins

import groovy.io.FileType

class Downloader {

    def steps

    Downloader(steps){
        this.steps = steps
    }

    List<String> downloadJenkinsArtifacts(String buildUrl, def extensions=[], def user='', def passwd=''){
        steps.fileOperations([
                steps.fileDownloadOperation(targetFileName: 'zipBuilds.zip', targetLocation: '', url: "${buildUrl}artifact/*zip*/archive.zip", userName: user, password: passwd),
                steps.fileUnZipOperation(filePath: 'zipBuilds.zip', targetLocation: 'unzipBuilds')
        ])

        def files = []
        if(extensions.size > 0) {
            extensions.each{
                files.addAll(steps.findFiles(glob: "unzipBuilds/**/*{it}"))
            }
        } else {
            files.addAll(steps.findFiles(glob: "unzipBuilds/**/*"))
        }

    }
}
