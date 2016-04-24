package vn.edu.hcmut.emrre.core.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import vn.edu.hcmut.emrre.core.entity.Relation;

public class Dictionary {

    private static final String fileDictionary = "dictionary.txt";
    private HashMap<String, Integer> dictionary;
    private int size;
    private int amount;
    
    public Dictionary(){
        this.dictionary = new HashMap<String, Integer>();
        this.size = 0;
        this.amount = 0;
    }
    
    public void addDictionary(String word){
        if (this.dictionary.get(word) == null){
            this.dictionary.put(word, size++);
        }
    }
    
    public void addDictionary1(String word){
        int value = (this.dictionary.get(word) == null)? 1 : this.dictionary.get(word) + 1;
        this.amount ++;
        this.dictionary.put(word, value);
    }
    
    public void remove (String word){
        if (this.dictionary.get(word) != null){
            this.dictionary.remove(word);
            this.dictionary.put(word, -1);
        }
    }
    public int getValue(String word){
        return (this.dictionary.get(word) == null)? -1 : this.dictionary.get(word);
    }
    
    public int getSize(){
        return this.dictionary.size();
    }
    
    public int getAmount(){
        return this.amount;
    }
    
    public void saveDictionary2File(String fileName) throws IOException{
        WriteFile wf = new WriteFile(fileName);
        wf.open(false);
        wf.write(this.dictionary.size() + "\n");
        for (String word: this.dictionary.keySet()){
            wf.write(word + " " + this.getValue(word) + "\n");
        }
        wf.close();
    }
    
    public void getDictionaryFromFile(String fileName) throws IOException{
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        line = br.readLine();
        this.size = Integer.parseInt(line.split(" ")[0]);
        this.amount = Integer.parseInt(line.split(" ")[1]);
        while((line = br.readLine()) != null){
            String[] str = line.split("[ ]+");
            this.dictionary.put(str[0], Integer.parseInt(str[1]));
        }
        br.close();
        fr.close();
        
    }
    
    public void getDictionaryFromFile1(String fileName){
        File file = new File(fileName);
        FileReader fr;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			System.out.println("Error: File not found: " + fileName + "\n");
			return;
		}
        BufferedReader br = new BufferedReader(fr);
        String line;
        try {
			line = br.readLine();
		} catch (IOException e) {
			System.out.println("Error: IOException\n");
			return;
		}
        this.size = Integer.parseInt(line);
        try {
			while((line = br.readLine()) != null){
			    String[] str = line.split("[ ]+");
			    this.dictionary.put(str[0], Integer.parseInt(str[1]));
			}
		} catch (NumberFormatException e) {
			System.out.println("Error: Number format exception \n");
			return;
		} catch (IOException e) {
			System.out.println("Error: IOException\n");
			return;
		}
        try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
    
    public static void main(String[] args) throws IOException {
        Dictionary[] dic = new Dictionary[8];
        for (int i = 0; i < 8; i++){
            dic[i] = new Dictionary();
            dic[i].getDictionaryFromFile("file/dictionary/types/" + Relation.typeOfDouble(i + 1));
            System.out.print(dic[i].getAmount()  + " ");
        }
    }

}
