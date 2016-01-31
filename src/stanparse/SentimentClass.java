/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stanparse;

import com.opencsv.CSVReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import libsvm.svm_node;
import libsvm.svm_problem;

/**
 *
 * @author Rory
 */
public class SentimentClass {
    
    
    public void mainMethod()
    {
        String dataFilePath = "C:/Users/Rory/Desktop/Han.csv";
        List<Integer> yC = new ArrayList<Integer>();
        List<String> x = new ArrayList<String>();
        String[] stopwords = new String[]{"hon.","gentleman","member","friend","lady","a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "i","ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};

        List<String> stop = Arrays.asList(stopwords);
    
    
        try {
            CSVReader reader = new CSVReader (new FileReader(dataFilePath));
            String[] nextline;
            int counter = 0;
            while((nextline = reader.readNext()) != null)
            {
                if(counter > 0)
                {
                System.out.println(nextline[0]);
                System.out.println(nextline[1]);
                x.add(nextline[0]);
                yC.add(Integer.parseInt(nextline[1]));
                }
                
                counter++;
            }
            
           // x.remove(0);
           // yC.remove(0);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SentimentClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SentimentClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Integer[] yc = yC.toArray(new Integer[yC.size()]);
        double[] y = new double[yc.length];
        for(int i = 0; i < y.length; i++)
        {
            y[i] = (double)yc[i];
        }
        
        Bigram b = new Bigram();
        List<String> v = new ArrayList<>();
        String sent = "";
        for(int i = 0; i < x.size(); i++)
        {
            String c = x.get(i).toString();
            
            c = c.replace(",", "");
            c = c.toLowerCase();
            String[] sp = c.split(" ");
            
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
            v.addAll(b.getNG(sent));
            sent = "";
        }
        System.out.println(v);
       TextClassificationProblemBuilder problemBuilder = new TextClassificationProblemBuilder();
       svm_problem sp;
       sp = problemBuilder.CreateProblem(x, y, v);
       
       WriteProblem("C:/Users/Rory/Desktop/Ou.problem",sp);
       System.out.println("Doooooooone");
    }
    
    public static void WriteProblem(String fn, svm_problem prob)
    {
        
        File file = new File(fn);
        try {
            file.createNewFile();
            FileWriter fw;
            fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            
            for(int i = 0; i < prob.l; i++)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("{0} ", prob.y[i]));
                for(int z = 0; z < prob.x[i].length; z++)
                {
                    svm_node n = prob.x[i][z];
                    sb.append(String.format("[0]:[1] ", n.index, n.value));
                }
                bw.write(sb.toString().trim());
                bw.newLine();
            }
            
            
            bw.close();
            fw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(SentimentClass.class.getName()).log(Level.SEVERE, null, ex);
        }
                
                
               
    }
}
