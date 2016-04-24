package vn.edu.hcmut.emrre.common;

import vn.edu.hcmut.emrre.main.RelationCore;
import vn.edu.hcmut.emrre.modal.entity.DocLine;

public class WordHandle {
	static public String[] tokenizeSentence(String sentence){
		return sentence.trim().split(" ");
	}
	
	static public String getWord(String sentence, int position){
		String[] wordLst = tokenizeSentence(sentence);
		if (wordLst != null && position < wordLst.length && position >= 0)
			return wordLst[position];
		return null;
	}
	
	static public String getWords(String sentence, int start, int end){
		String[] wordLst = tokenizeSentence(sentence);
		String result = "";
		if (wordLst == null)
			return null;
		for (int i = start; i <= end; i++) {
			if(i < 0 || i >= wordLst.length){
				System.out.println("sentence " + sentence + " start " + start + " end " + end);
				return null;
			}
			result += "_" + wordLst[i]; 
		}		
		return result;	
	}
	
	static public String getWord(String fileName, int lineIndex, int position){
		DocLine dl = DocLine.getDocLine(RelationCore.getDoclines(), lineIndex, fileName);
		if (dl == null)
			return null;
		return getWord(dl.getContent(), position - 1);
		
	}
	
	static public String getWords(String fileName, int lineIndex, int start, int end){
		DocLine dl = DocLine.getDocLine(RelationCore.getDoclines(), lineIndex, fileName);
		if (dl == null){
			System.out.println("file " + fileName + " line " + lineIndex + " start " + start + " end " + end);
			return null;
		}
		return getWords(dl.getContent(), start - 1, end - 1);
	}
	
	static public String getSentence(String fileName, int lineIdx){
		DocLine dl = DocLine.getDocLine(RelationCore.getDoclines(), lineIdx, fileName);
		if (dl == null){
			return null;
		}
		return dl.getContent();
	}
	
}
