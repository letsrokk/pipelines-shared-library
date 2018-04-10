package org.fxclub.qa.jenkins

import groovy.io.FileType

class Downloader {

    def steps

    Downloader(steps){
        this.steps = steps
    }

    List<File> downloadJenkinsArtifacts(String buildUrl, def extensions=[], def user=null, def passwd=null){
        def workspace = steps.pwd()
        def localZipPath = "${workspace}/zipBuilds.zip"
        File localZipFile = new File(localZipPath)
        steps.echo "Downloaded artifacts: " + localZipFile.getAbsolutePath()

        localZipFile.createNewFile()

        localZipFile.withOutputStream { out ->
            def url = new URL("${buildUrl}artifact/*zip*/archive.zip").openConnection()
            if((user?.trim())) {
                def remoteAuth = "Basic " + "${user}:${passwd}".bytes.encodeBase64()
                url.setRequestProperty("Authorization", remoteAuth)
            }
            out << url.inputStream
        }

        def unzipFolder = "${workspace}/unzipBuilds"
        def ant = new AntBuilder()
        ant.unzip(
                src: localZipPath,
                dest: unzipFolder,
                overwrite: "true"
        )

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
