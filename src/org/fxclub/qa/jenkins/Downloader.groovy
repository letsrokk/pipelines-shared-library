package org.fxclub.qa.jenkins

import groovy.io.FileType

class Downloader {

    def steps

    Downloader(steps){
        this.steps = steps
    }

    List<String> downloadJenkinsArtifacts(String buildUrl, def extensions=[], def user=null, def passwd=null){
        steps.fileOperations([
                steps.fileDownloadOperation(password: '', targetFileName: 'zipBuilds.zip', targetLocation: '', url: "${buildUrl}artifact/*zip*/archive.zip", userName: ''),
                steps.fileUnZipOperation(filePath: 'zipBuilds.zip', targetLocation: 'unzipBuilds')
        ])

        steps.findFiles glob: 'unzipBuilds/*.zip'
    }
}
