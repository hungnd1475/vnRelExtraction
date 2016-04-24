package vn.edu.hcmut.emrre.common;

public class Constant {
	public static final String MODEL_FILE_PATH				= "file/model/model.txt";
	public static final String DATA_TRAIN_FILE_PATH 		= "file/data-train/data-train.txt";
	public static final String MODEL_FILE_PATH_CROSS		= "file/model/model-train.txt";
	public static final String DATA_TRAIN_FILE_PATH_CROSS 	= "file/data-train/data-train-cross.txt";
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
	public static final String PTR_MATCHER_2				= "(lành)|(khỏi)|(đỡ)|(tốt)|(xuất_viện)|(hết)";
	
	/* Concept extraction constant */
	public static final String CONCEPT_MODEL_FILE_PATH		= "file/model/concept-model.txt";
	public static final String CONCEPT_MODEL_FILE_PATH_TRAIN = "file/model/concept-model-train.txt";
	public static final String CONCEPT_DATA_TRAIN_FILE_PATH	= "file/data-train/concept-data-train.txt";
	public static final String CONCEPT_CONCEPT_DICTIONARY_FILE_PATH 	= "file/dictionary/concept-concept-dictionary.txt";
	public static final String PROBLEM_CONCEPT_DICTIONARY_FILE_PATH 	= "file/dictionary/problem-dictionary.txt";
	public static final String TEST_CONCEPT_DICTIONARY_FILE_PATH 		= "file/dictionary/test-dictionary.txt";
	public static final String TREATMENT_CONCEPT_DICTIONARY_FILE_PATH 	= "file/dictionary/treatment-dictionary.txt";
	public static final String CONCEPT_WORD_DICTIONARY_FILE_PATH		= "file/dictionary/word-beside-dictionary.txt";
			
	public static final int BIO_B_PR						= 1;
	public static final int BIO_B_TE						= 2;
	public static final int BIO_B_TR						= 3;
	public static final int BIO_I_PR						= 4;
	public static final int BIO_I_TE						= 5;
	public static final int BIO_I_TR						= 6;
	public static final int BIO_O							= 7;
	
	public static final int CF_BIO_TAG_SIZE					= 7;
	public static final int CF_IS_BEGIN_SEN_SIZE			= 1;
	public static final int CF_DELIMITER_SIZE				= 2;
	public static final String DELIMITER_MATCHER			= "[.,;:]";
	
}
