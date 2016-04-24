package vn.edu.hcmut.emrre.feature;

import java.util.List;
import java.util.regex.Pattern;

import vn.edu.hcmut.emrre.common.Constant;
import vn.edu.hcmut.emrre.common.Dictionary;
import vn.edu.hcmut.emrre.common.WordHandle;
import vn.edu.hcmut.emrre.dao.SentenceDAO;
import vn.edu.hcmut.emrre.entity.Concept;
import vn.edu.hcmut.emrre.entity.DocLine;
import vn.edu.hcmut.emrre.entity.Sentence;
import vn.edu.hcmut.emrre.entity.Word;

public class ConceptFeatureVn {
	private int dimension;
	private double[] vector;

	private Dictionary conceptDic;
	private Dictionary wordBesideDic;

	private String[] lstWord;
	private List<Word> WordLst;
	private int wordPos;
	private int[] bio;

	public ConceptFeatureVn() {
	}

	public void setBio(int[] lstBio) {
		this.bio = lstBio;
	}

	public void setWordLst(List<Word> wordLst) {
		WordLst = wordLst;
	}

	public void setLstWord(String[] lstWord) {
		this.lstWord = lstWord;
	}
	
	private int postagEncode(String postag) {
		switch (postag) {
		case "Np":
			return 0;
		case "E":
			return 1;
		case "A":
			return 2;
		case "N":
			return 3;
		case "V":
			return 4;
		case "R":
			return 5;
		case "M":
			return 6;
		case "C":
			return 7;
		case "P":
			return 8;
		case "Nc":
			return 9;
		case "Ny":
			return 10;
		case "X":
			return 11;
		case "T":
			return 12;
		case "L":
			return 13;
		default:
			return -1;

		}
	}

	/**
	 * Context feature
	 */

	private int cfDelimiter(int idx) {
		Pattern deliPatt = Pattern.compile(Constant.DELIMITER_MATCHER);
		String str;
		//pre
		if (this.wordPos - 1 >= 0) {
			str = this.lstWord[this.wordPos - 1];
			if (deliPatt.matcher(str.toLowerCase()).find()) {
				this.vector[idx] = 1;
			}
		}
		//pos
		if (this.wordPos + 1 < this.lstWord.length) {
			str = this.lstWord[this.wordPos + 1];
			if (deliPatt.matcher(str.toLowerCase()).find()) {
				this.vector[idx + 1] = 1;
			}
		}
		return idx + Constant.CF_DELIMITER_SIZE;
	}
	
	private int cfPPMatcher(int idx) {
		Pattern deliPatt = Pattern.compile(Constant.PP_MATCHER);
		String str;
		//pre
		if (this.wordPos - 1 >= 0) {
			str = this.lstWord[this.wordPos - 1];
			if (deliPatt.matcher(str.toLowerCase()).find()) {
				this.vector[idx] = 1;
			}
		}
		//pos
		if (this.wordPos + 1 < this.lstWord.length) {
			str = this.lstWord[this.wordPos + 1];
			if (deliPatt.matcher(str.toLowerCase()).find()) {
				this.vector[idx + 1] = 1;
			}
		}
		return idx + Constant.CF_DELIMITER_SIZE;
	}
	
	private int cfTeMatcher(int idx) {
		Pattern deliPatt1 = Pattern.compile(Constant.PTE_MATCHER_1);
		Pattern deliPatt2 = Pattern.compile(Constant.PTE_MATCHER_2);
		String str;
		//pre
		if (this.wordPos - 1 >= 0) {
			str = this.lstWord[this.wordPos - 1];
			if (deliPatt1.matcher(str.toLowerCase()).find()) {
				this.vector[idx] = 1;
			}
			if (deliPatt2.matcher(str.toLowerCase()).find()) {
				this.vector[idx + 1] = 1;
			}
		}
		//pos
		if (this.wordPos + 1 < this.lstWord.length) {
			str = this.lstWord[this.wordPos + 1];
			if (deliPatt1.matcher(str.toLowerCase()).find()) {
				this.vector[idx + 2] = 1;
			}
			if (deliPatt2.matcher(str.toLowerCase()).find()) {
				this.vector[idx + 3] = 1;
			}
		}
		return idx + 2*Constant.CF_DELIMITER_SIZE;
	}
	
	private int cfWordBeside(int idx) {
		String word;
		int key;
		for (int i = this.wordPos - 3; i <= this.wordPos + 3; i++) {
			if (i != this.wordPos && i >= 0 && i < this.lstWord.length) {
				word = this.lstWord[i].toLowerCase();
				key = this.wordBesideDic.getValue(word);
				if (key != -1) {
					this.vector[key + idx] = 1;
				}
			}
		}
		return idx + this.wordBesideDic.getSize();
	}
	
	private int cfPogtagBeside(int idx) {
		String pos;
		int key;
		for (int i = this.wordPos - 3; i <= this.wordPos + 3; i++) {
			if (i != this.wordPos && i >= 0 && i < this.WordLst.size()) {
				pos = this.WordLst.get(i).getPosTag();
				key = postagEncode(pos);
				if (key != -1) {
					this.vector[key + idx] = 1;
				}
			}
		}
		return idx + Constant.CF_POSTAG_BETWEEN_SIZE;
	}

	private int cfPreBIOTag(int idx) {
		if (this.wordPos - 1 >= 0) {
			this.vector[idx + this.bio[this.wordPos - 1] - 1] = 1;
		}
		return idx + Constant.CF_BIO_TAG_SIZE;
	}

	private int cfIsBeginSen(int idx) {
		if (this.wordPos == 0) {
			this.vector[idx] = 1;
		}
		return idx + Constant.CF_IS_BEGIN_SEN_SIZE;
	}

	/**
	 * Single concept feature
	 */
	private int scContent(int idx) {
		int key;
		key = this.conceptDic.getValue(this.lstWord[this.wordPos].toLowerCase());
		if (key != -1)
			this.vector[key + idx] = 1;
		return idx + this.conceptDic.getSize();
	}
	
	private int scPostag(int idx) {
		if (this.wordPos < this.WordLst.size()) {
			String postag = this.WordLst.get(this.wordPos).getPosTag();
			int key = postagEncode(postag);
			if (key != -1) {
				this.vector[idx + key] = 1;
			}
		}
		return idx + Constant.CF_POSTAG_BETWEEN_SIZE;
	}

	public double[] buildFeatures(int wordPos) {
		this.wordPos = wordPos;
		if (this.conceptDic == null) {
			this.conceptDic = new Dictionary();
			this.conceptDic.getDictionaryFromFile1(Constant.CONCEPT_CONCEPT_DICTIONARY_FILE_PATH);
		}
		if (this.wordBesideDic == null) {
			this.wordBesideDic = new Dictionary();
			this.wordBesideDic.getDictionaryFromFile1(Constant.CONCEPT_WORD_DICTIONARY_FILE_PATH);
		}
		this.dimension = this.conceptDic.getSize() + this.wordBesideDic.getSize() + Constant.CF_BIO_TAG_SIZE
				+ Constant.CF_IS_BEGIN_SEN_SIZE + 2*Constant.CF_POSTAG_BETWEEN_SIZE + 4*Constant.CF_DELIMITER_SIZE;
		this.vector = new double[this.dimension];

		int nextIdx;
		nextIdx = scContent(0);
		nextIdx = cfWordBeside(nextIdx);
		nextIdx = cfPreBIOTag(nextIdx);
		nextIdx = cfIsBeginSen(nextIdx);
		nextIdx = scPostag(nextIdx);
		nextIdx = cfDelimiter(nextIdx);
		nextIdx = cfPogtagBeside(nextIdx);
		nextIdx = cfPPMatcher(nextIdx);
		nextIdx = cfTeMatcher(nextIdx);

		return this.vector;
	}

}
