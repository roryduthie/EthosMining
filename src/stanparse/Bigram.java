/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stanparse;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rory
 */
public class Bigram {
    public static List<String> ngrams(int n, String str)
    {
        List<String> ngrams = new ArrayList<>();
        String[] words = str.split(" ");
        for(int i = 0; i < words.length; i++)
        {
            if(n == 2 && (i + 1) >= words.length)
            {
                break;
            }
            ngrams.add(concat(words, i, i+ n));
        }
       
        return ngrams;
    }
    
    public static String concat(String[] words, int start, int end)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = start; i < end; i++)
        {
            sb.append((i > start ? " " : " ") + words[i]);
        }
        return sb.toString();
    }
    
    public List<String> getNG(String sentence)
    {
        List<String> vocab = new ArrayList<>();
        for(int n = 1; n <=2; n++)
        {
            for(String ngram : ngrams(n, sentence))
            {
                vocab.add(ngram);
            }
        }
        return vocab;
    }
    
}
