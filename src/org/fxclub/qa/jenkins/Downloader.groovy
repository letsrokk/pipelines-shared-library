package org.fxclub.qa.jenkins

import groovy.io.FileType

class Downloader {

    List<File> downloadJenkinsArtifacts(String buildUrl, def extensions=[], def user=null, def passwd=null){
        def localZipPath = "target/zipBuilds.zip"
        new File(localZipPath).withOutputStream { out ->
            def url = new URL("${buildUrl}artifact/*zip*/archive.zip").openConnection()
            if((user?.trim())) {
                def remoteAuth = "Basic " + "${user}:${passwd}".bytes.encodeBase64()
                url.setRequestProperty("Authorization", remoteAuth)
            }
            out << url.inputStream
        }

        def unzipFolder = "target/unzipBuilds"
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