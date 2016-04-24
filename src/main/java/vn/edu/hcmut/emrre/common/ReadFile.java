package vn.edu.hcmut.emrre.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmut.emrre.entity.Concept;
import vn.edu.hcmut.emrre.entity.DocLine;
import vn.edu.hcmut.emrre.entity.Relation;

public class ReadFile {
    
    private String folder;
    
    public String getFolder(String folder){
        return this.folder;
    }
    
    public void setFolder(String folder){
        this.folder = folder;
    }
    
    public List<Relation> getAllRelation(List<Concept> concepts, boolean noneRelation){
        List<Relation> relations = new ArrayList<Relation>();
        File folder = new File("src/main/resources/" + this.folder);
        File[] files = folder.listFiles();
        DataReader readFile = new DataReader();
        int size = 3102;
        for (int i = 0; i < files.length; i++){
            if (files[i].isFile()){
                relations.addAll(readFile.readRelations(concepts, this.folder + "/" + files[i].getName()));
            }
        }
        if (noneRelation){
            for (int i = 0; i < concepts.size() - 1; i++)
                for (int j = i + 1; j < concepts.size(); j++) {
                    if (Relation.canRelate(concepts.get(i), concepts.get(j))){
                        if (!Relation.hasRelation(concepts.get(i), concepts.get(j))) {
                            relations.add(new Relation(concepts.get(i).getFileName(), concepts.get(i), concepts.get(j),
                                    Relation.Type.NONE, relations.size()));
                        }
                    }
                }
        }
        for (int i = 0; i < relations.size() - 1; i++)
        {
                Concept first = relations.get(i).getPreConcept();
                Concept second = relations.get(i).getPosConcept();
                if (!Relation.canRelate(first, second)){
                    relations.remove(i);
                }
        }
        return relations;
    }
    
    public List<Concept> getAllConcept(int startIndex){
        List<Concept> concepts = new ArrayList<Concept>();
        File folder = new File("src/main/resources/" + this.folder);
        File[] files = folder.listFiles();
        DataReader readFile = new DataReader();
        for (int i = 0; i < files.length; i++){
            if (files[i].isFile()){
                concepts.addAll(readFile.readConcepts(this.folder + "/" + files[i].getName(), startIndex + concepts.size()));
            }
        }
        return concepts;
    }
    
    public void readAllAssertion(List<Concept> concepts){
        File folder = new File("src/main/resources/" + this.folder);
        File[] files = folder.listFiles();
        DataReader readFile = new DataReader();
        for (int i = 0; i < files.length; i++){
            if (files[i].isFile()){
                readFile.readAssertion(concepts, this.folder + "/" + files[i].getName());
            }
        }
    }
    
    public List<DocLine> getAllDocLine(){
        List<DocLine> doclines = new ArrayList<DocLine>();
        File folder = new File("src/main/resources/" + this.folder);
        File[] files = folder.listFiles();
        DataReader readFile = new DataReader();
        for (int i = 0; i < files.length; i++){
            if (files[i].isFile()){
                doclines.addAll(readFile.readDocument(this.folder + "/" + files[i].getName()));
            }
        }
        return doclines;
    }
    
    public List<double[][]> readDataFromFile(File file, int start, int end) throws IOException{
        List<double[][]> arr = new ArrayList<double[][]>();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        double[][] vector;
        while ((line = br.readLine()) != null){
            String[] arrstr = line.split(" ");
            String[] doustr;
            int size = 0, idx = 1;
            //calculate size of vector
            for (int i = 0; i < arrstr.length; i++){
                doustr = arrstr[i].split(":");
                if(doustr.length == 2 && Double.parseDouble(doustr[0]) >= start && Double.parseDouble(doustr[0]) <= end)
                    size++;
            }
            vector = new double[size + 1][2];
            doustr = arrstr[0].split(":");
            if (doustr.length != 1){
                vector[0][1] = -1;
            }
            else{
                vector[0][1] = Double.parseDouble(doustr[0]);
            }
            for (int i = 0; i < arrstr.length; i++){
                    doustr = arrstr[i].split(":");
                    if(doustr.length == 2 && Double.parseDouble(doustr[0]) >= start && Double.parseDouble(doustr[0]) <= end){
                        vector[idx][0] = Double.parseDouble(doustr[0]);
                        vector[idx++][1] = Double.parseDouble(doustr[1]);
                    }
            }
            arr.add(vector);
        }
        br.close();
        fr.close();
        return arr;
    }
    
    public static void main(String[] args) throws IOException{
        ReadFile rf = new ReadFile();
        File file = new File("file/data-train/combine-datatrain");
        WriteFile wf = new WriteFile("file/data-train/combine1_2-datatrain");
        wf.open(false);
        List<double[][]> arr = null;
        List<double[][]> arr2 = null;
        try {
             arr = rf.readDataFromFile(file, 45384, 45428);
             arr2 = rf.readDataFromFile(file, 1, 16715);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        } 
        if (arr != null)
            for (int i = 0; i < arr.size(); i++){
                wf.write((int)arr.get(i)[0][1]+"");
                for (int j =1; j<arr2.get(i).length; j++){
                    wf.write(" " + (int)(arr2.get(i)[j][0]) + ":" + arr2.get(i)[j][1]);
                    System.out.print(" " + (int)(arr2.get(i)[j][0]) + ":" + arr2.get(i)[j][1]);
                }
                for (int j =1; j<arr.get(i).length; j++){
                    wf.write(" " + (int)(arr.get(i)[j][0] - 28668) + ":" + arr.get(i)[j][1]);
                    System.out.print(" " + (int)(arr.get(i)[j][0] - 28668) + ":" + arr.get(i)[j][1]);
                }
                wf.writeln("");
                System.out.println();
            }
        wf.close();
    }
}
