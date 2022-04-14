import java.io.File

class Example {

    static void main(String[] args) {
        // List files
        println '---start'
        new File('c:/temp').eachFile() {
         file->println file.getAbsolutePath()
        }
        // Add a file
        println '---add'
        File hello = new File('C:/temp/hello.txt')
        hello.createNewFile()
        hello.write 'hello world'
        // Print file
        println '---read'
        println hello.text
        // PAppend file
        println '---append'
        hello.append '\nfrom file'
        println hello.text
        // File size using templated println string with double quotes
        println "File ${hello.getAbsolutePath()} has size ${hello.length()} bytes"
        // List files
        println '---finish'
        new File('c:/temp').eachFile() {
         file->println file.getAbsolutePath()
        }
    }

}
