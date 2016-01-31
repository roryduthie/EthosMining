/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stanparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rory
 */
public class StanParse {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        
        
        ArrayList paragraphs = new ArrayList();
        
        NLP.init();
       // System.out.print(NLP.findSentiment(sentence));
        //System.out.println(sentence + " : " + NLP.findSentiment(sentence));
        //System.out.println(sentence + " : " + NLP.getParse(sentence));
        //System.out.println(sentence + " : " + NLP.getNER(sentence));
        String text;
        
        
        //File[] listOFiles = folder.listFiles();
        
           File folder = new File("C:/Users/Rory/Desktop/ScaleUpTxt/");
    for (final File fileEntry : folder.listFiles()) {
        
            System.out.println(fileEntry.getName());
            if(fileEntry.getName().equals("1979-apr-3-nurses-pay.txt"))
            {
                System.out.println();
            }
      
            
        
    
        
        try {
            FileInputStream fs = new FileInputStream("C:/Users/Rory/Desktop/ScaleUpTxt/" + fileEntry.getName());
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            String line;
            String total = null;
            
            while((line = br.readLine())!= null)
            {
                total += line;
                if(!line.equals(""))
                {
                paragraphs.add(line);
                }
                total += " ";
                
               
            }
            
            //System.out.println(total);
            //NLP.getNER(sentence);
             NLP.getParaGraphs(paragraphs);
             String textT = "";
             for(int i = 0; i < paragraphs.size(); i++)
             {
                 String par = paragraphs.get(i).toString();
                 String find = ":";
                 String r = par.substring(par.indexOf(find)+find.length()); 
                 textT+= r;
                 textT += " ";
             }
             textT = textT.trim();
             System.out.println("POS");
            NLP.getPOS(textT);
            System.out.println("MIX");
            NLP.getMix(textT);
            System.out.println("DOMNAMES");
            NLP.getNameInstances(textT);
             System.out.println("Done");
            NLP.removeReportedSpeech();
             br.close();
            fs.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StanParse.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StanParse.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        }
            NLP.formatList();
            NLP.createLists();
            NLP.formatPeople();
            NLP.sortPeople();

            NLP.listToFile();
            
            
            
          
        //NLP.getPOS(sentence);
        //text = NLP.getNER(sentence);
        //NLP.getPOS(text);
    
    }
    
    public static String listFilesForFolder(final File folder) {
    
    return "";
    }
    
}
