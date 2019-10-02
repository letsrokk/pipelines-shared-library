package org.fxclub.qa.jenkins

import groovy.io.FileType

class Downloader {

    def steps

    Downloader(steps){
        this.steps = steps
    }
    
    def downloadFile(String buildUrl, def extensions=[]){
        
        steps.fileOperations([
                steps.folderDeleteOperation('builds')
        ])
        
        steps.sh("wget -P builds ${buildUrl}")

        def files = []
        if (extensions.size > 0) {
            extensions.each{
                files.addAll(steps.findFiles(glob: "builds/**/*${it}"))
            }
        } else {
            files.addAll(steps.findFiles(glob: "builds/**/*"))
        }

        return files
    }

    def downloadJenkinsArtifacts(String buildUrl, def extensions=[], def user='', def passwd=''){
        steps.fileOperations([
                steps.fileDeleteOperation(excludes: '', includes: 'artifacts.zip'),
                steps.folderDeleteOperation('unzipBuilds')
        ])

        steps.sh(
                "wget "
                + (user?.trim() ? "--http-user=${user} --http-password=${passwd} " : "")
                + "-O artifacts.zip ${buildUrl}artifact/*zip*/archive.zip"
        )

        steps.fileOperations([
                steps.fileUnZipOperation(filePath: 'artifacts.zip', targetLocation: 'unzipBuilds')
        ])

        def files = []
        if (extensions.size > 0) {
            extensions.each{
                files.addAll(steps.findFiles(glob: "unzipBuilds/**/*${it}"))
            }
        } else {
            files.addAll(steps.findFiles(glob: "unzipBuilds/**/*"))
        }

        return files
    }
}
