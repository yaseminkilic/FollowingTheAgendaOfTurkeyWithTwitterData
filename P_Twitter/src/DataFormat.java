
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

/* 
 * This class is directly related to format the Twitter data before it is recorded in database.
 */
public class DataFormat {

	
	private static DbProcess dbprocess = new DbProcess();
	
    String delimiters = " \t,;.?!-:#[](){}_*/\'\"";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    private String formatToTokens(String word) {
        return word.trim().toLowerCase(Locale.ENGLISH);
    }
    
    /*
     * Divide each text using delimiters provided in the “delimiters” variable.
     * Make all the tokens lowercase and remove any tailing and leading spaces.
     * Ignore all common words provided in the “stopWordsArray” variable.
     * 
     * @parameters text:String
     * @return String
     */
	public String formatData(String text) throws Exception {
    	String word = "", newText = "";
    	
    	String[] splitedText = text.trim().split("http");
        StringTokenizer tokenizer = new StringTokenizer(splitedText[0].trim(),delimiters);
    	while (tokenizer.hasMoreElements()) {
    		// Make all the tokens lowercase and remove any tailing and leading spaces.
    		word = formatToTokens(tokenizer.nextElement().toString());
    		// Ignore all common words provided in the “stopWordsArray” variable.
    		if(!Arrays.asList(stopWordsArray).contains(word)){
    			newText += word + " ";
    		}
		}
		return newText;
    }
	
	/*
     * Define wheather the text is in term list.
     * @parameters text:String
     * 
     * @return boolean
     */
	public boolean isInTermList(String text){
		ArrayList<String> list = new ArrayList<String>();
		for (int j = 0; j < (list = dbprocess.getTerm()).size(); j++) {
            if (text.toLowerCase().contains(list.get(j).toLowerCase())) {
                return true;
            }
        }
		return false;
	}
}
