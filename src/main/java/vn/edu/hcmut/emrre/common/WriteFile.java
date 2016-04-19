package vn.edu.hcmut.emrre.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFile {
    private String file;
    private BufferedWriter bw;
    
    public WriteFile(String fileName){
        this.file = fileName;
        bw = null;
    }
    public WriteFile(){
        this.file = "file.txt";
    }
    
    public void open(boolean append) throws IOException{
        File file = new File(this.file);
        if (!file.exists()){
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file, append);
        this.bw = new BufferedWriter(fw);
    }
    
    public void write(String message) throws IOException{
        this.bw.write(message);
    }
    
    public void writeln(String message) throws IOException{
        this.bw.write(message + "\n");
    }
    
    public void close() throws IOException{
        this.bw.close();
    }
}
