package vn.edu.hcmut.emrre.core.utils;

import vn.edu.hcmut.emrre.core.entity.DocLine;
import vn.edu.hcmut.emrre.main.EMRCore;

public class WordHandle {
	static private String[] tokenizeSentence(String sentence){
		return sentence.trim().split(" ");
	}
	
	static private String getWord(String sentence, int position){
		String[] wordLst = tokenizeSentence(sentence);
		if (wordLst != null && position < wordLst.length)
			return wordLst[position];
		return null;
	}
	
	static private String getWords(String sentence, int start, int end){
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
		DocLine dl = DocLine.getDocLine(EMRCore.getDoclines(), lineIndex, fileName);
		if (dl == null)
			return null;
		return getWord(dl.getContent(), position - 1);
		
	}
	
	static public String getWords(String fileName, int lineIndex, int start, int end){
		DocLine dl = DocLine.getDocLine(EMRCore.getDoclines(), lineIndex, fileName);
		if (dl == null){
			System.out.println("file " + fileName + " line " + lineIndex + " start " + start + " end " + end);
			return null;
		}
		return getWords(dl.getContent(), start - 1, end - 1);
	}
	
	static public String getSentence(String fileName, int lineIdx){
		DocLine dl = DocLine.getDocLine(EMRCore.getDoclines(), lineIdx, fileName);
		if (dl == null){
			return null;
		}
		return dl.getContent();
	}
	
}
