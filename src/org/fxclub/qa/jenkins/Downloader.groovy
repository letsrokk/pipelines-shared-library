package org.fxclub.qa.jenkins

import groovy.io.FileType

class Downloader {

    def steps

    Downloader(steps){
        this.steps = steps
    }

    List<File> downloadJenkinsArtifacts(String buildUrl, def extensions=[], def user=null, def passwd=null){
        steps.fileOperations([
                steps.fileDownloadOperation(password: '', targetFileName: 'zipBuilds.zip', targetLocation: '', url: "${buildUrl}artifact/*zip*/archive.zip", userName: ''),
                steps.fileUnZipOperation(filePath: 'zipBuilds.zip', targetLocation: 'unzipBuilds')
        ])

        def list = []
        def dir = new File(unzipFolder)
        dir.eachFileRecurse (FileType.FILES) { file ->
            if((extensions.size > 0)) {
                def match = extensions.any{file.getAbsolutePath().endsWith(it)}
                if(match) {
                    list << file
                }
            } else {
                list << file
            }
        }

        return list
    }
}
