package vn.edu.hcmut.emrre.core.utils;

public class Constant {
	public static final String MODEL_FILE_PATH				= "file/model/model.txt";
	public static final String DATA_TRAIN_FILE_PATH 		= "file/data-train/data-train.txt";
	public static final String CONCEPT_DICTIONARY_FILE_PATH = "file/dictionary/concept-dictionary.txt";
	public static final String WORD_DICTIONARY_FILE_PATH	= "file/dictionary/word-between-dictionary.txt";
			
	public static final int SC_TYPE_SIZE 					= 6;
	public static final int SC_CONCEPT_ORDER_SIZE			= 2;
	public static final int SC_CONCEPT_POSTAG_SIZE			= 14;
	public static final int CF_STRING_MATCHER_SIZE			= 5;
	public static final int CF_POSTAG_BETWEEN_SIZE			= 14;
	public static final int CV_CONCEPT_TYPE_SIZE			= 18;
	
	public static final String PP_MATCHER					= ",|;|(và)|(cùng)|(với)|(kèm)|(còn)|(cũng)";
	public static final String PTE_MATCHER_1				= "(được)|(đi)|(đo)|(khám)|(xét)";
	public static final String PTE_MATCHER_2				= "(ra)|(phát_hiện)|(tìm)|(thấy)|(đoán)";
	public static final String PTR_MATCHER_1				= "(không)|(chưa)|(nhưng)|(nhập_viện)";
	public static final String PTR_MATCHER_2				= "(lành)|(khỏi)|(đỡ)|(tốt)|(xuất_viện)";
}
