/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stanparse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import libsvm.svm_node;
import libsvm.svm_problem;

/**
 *
 * @author Rory
 */
public class TextClassificationProblemBuilder {
    
    public svm_problem CreateProblem(Iterable<String> x, double[] y, List<String> vocabulary)
    {
        svm_problem sp = new svm_problem();
        List<svm_node[]> sn = new ArrayList<>();
       svm_node[][] m = new svm_node[y.length][];
       
        
        sp.y = y;
        int count = 0;
        for(String s : x)
        {
            
            svm_node[] arr = CreateNode(s,vocabulary);
            for(int i = 0; i < arr.length; i++)
            {
                sn.add(CreateNode(s, vocabulary));
            }
            svm_node[] a = sn.toArray(new svm_node[sn.size()]);
            for(int z = 0; z < sn.size(); z++)
            {
                m[count] = a;
            }
            
            count++;
        }
        
        sp.x = m;
        sp.l = y.length;
        System.out.println("Got to here");
        
        
        
        return sp;
    }
    
    public static svm_node[] CreateNode(String x, List<String> vocabulary)
    {
        List<svm_node> node = new ArrayList<svm_node>(vocabulary.size());
        
        List<String> allWords = new ArrayList<>();
        x = x.replace(",", "");
        
        Bigram b = new Bigram();
        
        String[] stopwords = new String[]{"hon.","gentleman","member","friend","lady","a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "i","ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};

        List<String> stop = Arrays.asList(stopwords);
        
        
            
            x = x.replace(",", "");
            x = x.toLowerCase();
            String[] sp = x.split(" ");
            String sent = "";
            
            for(int z = 0; z < sp.length; z++)
            {
                String word = sp[z];
                if(stop.contains(word))
                {
                    System.out.println("Stop Word");
                }
                else
                {
                    sent += word;
                    sent += " ";
                }
                    
            }
            sent = sent.trim();
            
            //sent = "";
        
        allWords.addAll(b.getNG(sent));
        String[] words = new String[allWords.size()];
        //String[] words = allWords.toArray(new String[allWords.size()]);
        allWords.toArray(words);
        
        for(int  i = 0; i < vocabulary.size(); i++)
        {
            int occuranceCount = 0;
            for (String word : words) {
                if (vocabulary.contains(word)) {
                    occuranceCount = occuranceCount++;
                    
                }
            }
            
            if(occuranceCount == 0)
            {
                continue;
            }
            else
            {
                svm_node n = new svm_node();
                n.index = i + 1;
                n.value = occuranceCount;
                
                node.add(n);
            }
        }
        
        svm_node[] a = node.toArray(new svm_node[node.size()]);
        return a;
    }
    
}
