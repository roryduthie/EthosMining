/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stanparse;

import com.sun.org.apache.xml.internal.serializer.Serializer;
import edu.stanford.nlp.io.EncodingPrintWriter;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.*;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.AnnotationOutputter;
import edu.stanford.nlp.pipeline.CoreNLPProtos.Document;
import edu.stanford.nlp.pipeline.XMLOutputter;
import static edu.stanford.nlp.pipeline.XMLOutputter.annotationToDoc;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreePrint;
import java.io.*;
import static java.lang.System.out;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NLP {

    static StanfordCoreNLP pipeline;
    static StanfordCoreNLP pipe;
    static String serializedClassifier = "D:/Docs/StanParse/classifiers/english.conll.4class.distsim.crf.ser.gz";
    static String seCl = "D:/Docs/StanParse/classifiers/english.muc.7class.distsim.crf.ser.gz";

    static AbstractSequenceClassifier<CoreLabel> classifier;
    static AbstractSequenceClassifier<CoreLabel> clas;
    static String first = null;
    static String second = null;
    static String fNa = null;
    static String sNa = null;
    static String sF = null;
    static String sS = null;

    public static List<SentenceInfo> speech = new ArrayList<SentenceInfo>();
    public static List<PersonInfo> locName = new ArrayList<PersonInfo>();
    public static List<ParaInfo> pInfo = new ArrayList<ParaInfo>();

    public static List<Party> party = new ArrayList<Party>();
    public static List<People> people = new ArrayList<People>();
    public static List<String> peep = new ArrayList<String>();
    public static List<PositiveSent> ps = new ArrayList<PositiveSent>();
    public static List<NegativeSent> ns = new ArrayList<NegativeSent>();
    public static List<PartyRel> pr = new ArrayList<PartyRel>();

    public static void init() {
        pipeline = new StanfordCoreNLP("MyPropFile.properties");
        pipe = new StanfordCoreNLP("Prop.properties");
        try {
            classifier = CRFClassifier.getClassifier(serializedClassifier);
            clas = CRFClassifier.getClassifier(seCl);
        } catch (IOException ex) {
            Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int findSentiment(String tweet) {

        int mainSentiment = 0;
        if (tweet != null && tweet.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(tweet);
            for (CoreMap sentence : annotation
                    .get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence
                        .get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }

            }
        }
        return mainSentiment;
    }

    public static String getParse(String sent) {

        Annotation annotation;
        annotation = new Annotation(sent);
        pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        CoreMap sentence = sentences.get(0);

        Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        System.out.println();
        System.out.println("The Sentance Is: ");
        tree.pennPrint(System.out);

        return sent;
    }

    public static String getPOS(String sent) {

        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        int proLoc = 0;

        Annotation document = new Annotation(sent);
        int counter = 0;
        int wordCounter = 0;
        int willIndex = -1;

// run all Annotators on this text
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        System.out.println(sentences.size());

        for (CoreMap sentence : sentences) {
            Boolean check = false;
            //System.out.println(sentences.get(counter));
            counter++;
            if (sentence.toString().contains("Is he aware")) {
                System.out.println("ggggg");
            }
            String n = getNER(sentence.toString());
            if (!n.contains("PERSON")) {
                // traversing the words in the current sentence

                // a CoreLabel is a CoreMap with additional token-specific methods
                for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                    // this is the text of the token
                    String word = token.get(TextAnnotation.class);
                    wordCounter++;
                    // this is the POS tag of the token
                    String pos = token.get(PartOfSpeechAnnotation.class);

                    if (pos.equals("PRP") || pos.equals("PRP$")) {
                        String t = token.originalText();
                        if (token.originalText().equalsIgnoreCase("I") || token.originalText().equalsIgnoreCase("us") || token.originalText().equalsIgnoreCase("we") || token.originalText().equalsIgnoreCase("it") || token.originalText().equalsIgnoreCase("its") || token.originalText().equalsIgnoreCase("our")
                                || token.originalText().equalsIgnoreCase("their") || token.originalText().equalsIgnoreCase("my") || token.originalText().equalsIgnoreCase("these") || token.originalText().equalsIgnoreCase("me") || token.originalText().equalsIgnoreCase("them")) {

                            if (token.originalText().equalsIgnoreCase("it") && wordCounter < 2 || token.originalText().equalsIgnoreCase("we") && wordCounter < 2) {
                                check = false;
                                break;
                            }

                        } else {
                            if (!check) {
                                proLoc = wordCounter * 2;
                            }
                            check = true;
                            //System.out.println(pos + " word: " + t);

                        }

                    }
                    //System.out.println(pos + " word: " + token.originalText());

                }
                wordCounter = 0;
            }

            if (check) {

                String senten = sentence.toString();
                willIndex = -1;
                senten = senten.toLowerCase();
                if (senten.contains("will")) {
                    willIndex = senten.indexOf("will");

                }

                if (proLoc < willIndex || willIndex == -1) {

                    int count = 0;

                    /* for (int z = counter - 2; z >= 0; z--) {
                     count++;
                     if (count > 4) {
                     check = false;
                     break;
                     }
                     String sen = sentences.get(z).toString();
                     if (getName(sen)) {
                     System.out.println("Person: " + first + " " + second);

                     break;
                     }

                     }*/
                    if (first == null || second == null) {
                        int p = 0;

                        p = sentLoc(sentence.toString());
                        System.out.println(p);
                        if (p > 0) {
                            first = pInfo.get(p - 1).getFName();
                            second = pInfo.get(p - 1).getSur();

                            for (int z = p - 2; z >= 0; z--) {
                                if (first.equals(pInfo.get(z).getFName()) && second.equals(pInfo.get(z).getSur())) {

                                } else {
                                    fNa = pInfo.get(z).getFName();
                                    sNa = pInfo.get(z).getSur();
                                    break;
                                }
                            }
                        }

                    }

                    if (first != null && second != null) {
                        SentenceInfo s = new SentenceInfo();

                        s.setFName(fNa);

                        s.setSur(sNa);

                        s.setText(sentence.toString());
                        s.setSentenceID(counter);

                        //sentInP(sentence.toString());
                        s.setSF(first);
                        s.setSS(second);
                        first = null;
                        second = null;
                        System.out.println(sentence.toString());
                        System.out.println(counter);
                        check = false;

                        speech.add(s);

                        willIndex = -1;
                    }
                }
            }

        }
        return sent;
    }

    public static String getNER(String text) {

        text = classifier.classifyToString(text);

        return text;

    }

    public static Boolean getName(String text) {
        List<List<CoreLabel>> out = classifier.classify(text);
        Boolean frst = false;
        Boolean sur = false;
        Boolean person = false;
        first = null;
        second = null;

        for (List<CoreLabel> sentence : out) {

            for (CoreLabel word : sentence) {
                if (word.get(CoreAnnotations.AnswerAnnotation.class).equals("PERSON")) {

                    person = true;
                    if (!frst) {
                        first = word.word();

                        frst = true;
                    } else {
                        second = word.word();

                    }
                }
            }

            if (first.equals("Tory") && second.equals("Whip") || first.equals("Labour") && second.equals("Whip") || first.equals("Liberal") && second.equals("Whip")) {
                first = null;
                second = null;
            }

            if (first != null && second == null) {
                second = first;
                first = "Mr";
                return person;
                //System.out.println("Change Fname");
            }

        }

        return person;
    }

    public static void getNames() {

    }

    public static void getMix(String text) {

        int nameLoc = 0;
        int willIndex = -1;

        String fName = null;
        String sName = null;
        String fSent = "";
        int sentenceId = 0;
        String serializedClassifier = "classifiers/english.conll.4class.distsim.crf.ser.gz";
        AbstractSequenceClassifier<CoreLabel> classifier;
        try {
            classifier = CRFClassifier.getClassifier(serializedClassifier);
            //String fileContents = IOUtils.slurpFile(text);
            List<List<CoreLabel>> out = classifier.classify(text);

            for (List<CoreLabel> sentence : out) {
                int i = 0;
                sentenceId++;
                Boolean first = false;
                Boolean sur = false;
                Boolean check = false;
                SentenceInfo s = new SentenceInfo();

                for (CoreLabel word : sentence) {
                    i++;

                    //System.out.println(i);
                    //System.out.print(word.word() + '/' + word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
                    fSent += word.word();
                    fSent += ' ';

                    if (word.get(CoreAnnotations.AnswerAnnotation.class).equals("PERSON")) {

                        //System.out.print(word.word());
                        String t = word.word();
                        char last = t.charAt(t.length() - 1);
                        if (last == 's') {
                            t = t.substring(0, t.length() - 1);
                        }
                        if (t.equals(t.toUpperCase())) {

                        } else {
                            if (i > 3) {

                                check = true;
                                nameLoc = i;
                                if (!first) {
                                    fName = word.word();

                                    first = true;
                                } else {
                                    sName = word.word();

                                }

                            }
                        }
                    } else {
                        // System.out.print(word.word() + ' ');
                        //fSent +=  word.word();
                    }

                }
                if (fName != null && sName == null) {
                    sName = fName;
                    fName = "Mr";
                    //System.out.println("Change Fname");
                }

                if (fName != null) {
                    //System.out.println("FirstName: " + fName);

                    s.setFName(fName);
                    fName = null;
                }
                if (sName != null) {
                    //System.out.println("SurName: " + sName);
                    s.setSur(sName);
                    sName = null;
                }
                if (check) {
                    //System.out.println(fSent);
                    String f = fSent.toLowerCase();
                    if (f.contains("will")) {
                        willIndex = f.indexOf("will");
                    }
                    if (nameLoc < willIndex || willIndex == -1) {
                        fSent = fSent.replaceAll("-LRB- ", "(");
                        fSent = fSent.replaceAll(" -RRB-", ")");
                        fSent = fSent.replaceAll(" ,", ",");
                        fSent = fSent.replaceAll(" [.]", ".");
                        fSent = fSent.replaceAll("can not", "cannot");
                        s.setText(fSent);
                        s.setSentenceID(sentenceId);

                        check = false;

                        sentInP(fSent);

                        s.setSF(fNa);
                        s.setSS(sNa);
                        fSent = "";
                        speech.add(s);
                        willIndex = -1;
                    }
                } else {
                    fSent = "";
                }

                //System.out.println(fSent);
                //System.out.println();
            }

            // System.out.println(speech);
            System.out.println();
            //System.out.println(classifier.classifyToString(text));
        } catch (IOException | ClassCastException | ClassNotFoundException ex) {
            Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void listToFile() {
        String outputS = "";
        int i = 0;

        File file = new File("Output2.txt");
        File pl = new File("PartyFile.csv");
        File pe = new File("People.csv");
        File ps1 = new File("Positive.csv");
        File ns1 = new File("Negative.csv");
        File pr1 = new File("PartyRel.csv");

        // if file doesnt exists, then create it
        if (!file.exists()) {
            try {
                file.createNewFile();
                pl.createNewFile();
                pe.createNewFile();
                ps1.createNewFile();
                ns1.createNewFile();
                pr1.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {

            file.delete();
            pl.delete();
            pe.delete();
            ps1.delete();
            ns1.delete();
            pr1.delete();

            try {
                file.createNewFile();
                pl.createNewFile();
                pe.createNewFile();
                ps1.createNewFile();
                ns1.createNewFile();
                pr1.createNewFile();

            } catch (IOException ex) {
                Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        FileWriter fw;
        FileWriter f;
        FileWriter f1;
        FileWriter f2;
        FileWriter f3;
        FileWriter f4;

        try {
            fw = new FileWriter(file.getAbsoluteFile(), true);
            f = new FileWriter(pl.getAbsoluteFile(), true);
            f1 = new FileWriter(pe.getAbsoluteFile(), true);
            f2 = new FileWriter(ps1.getAbsoluteFile(), true);
            f3 = new FileWriter(ns1.getAbsoluteFile(), true);
            f4 = new FileWriter(pr1.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            BufferedWriter b = new BufferedWriter(f);
            BufferedWriter b1 = new BufferedWriter(f1);
            BufferedWriter b2 = new BufferedWriter(f2);
            BufferedWriter b3 = new BufferedWriter(f3);
            BufferedWriter b4 = new BufferedWriter(f4);

            for (i = 0; i < speech.size(); i++) {
                outputS = speech.get(i).toString();

                //Gets a field from the class in the list
                Class<?> cl = speech.get(i).getClass();

                try {
                    //System.out.println(outputS);
                    bw.write(outputS);
                    bw.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            bw.close();

            b.write("PartID,Party");
            b.newLine();
            outputS = "";
            for (int z = 0; z < party.size(); z++) {
                outputS = party.get(z).toString();

                try {
                    //System.out.println(outputS);
                    b.write(outputS);
                    b.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            b.close();

            b1.write("PersID,name");
            b1.newLine();

            for (i = 0; i < peep.size(); i++) {

                outputS = peep.get(i).toString();
                outputS = outputS.trim();
                outputS = outputS.replace("\ufeff", "");
                //outputS = formatPeople(outputS);
                try {
                    //System.out.println(outputS);
                    b1.write(outputS);
                    b1.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            b1.close();

            b2.write("SpeakerID,TargetID,Text");
            b2.newLine();
            for (i = 0; i < ps.size(); i++) {
                outputS = ps.get(i).toString();

                try {
                    //System.out.println(outputS);
                    b2.write(outputS);
                    b2.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            b2.close();
            b3.write("SpeakerID,TargetID,Text");
            b3.newLine();
            for (i = 0; i < ns.size(); i++) {
                outputS = ns.get(i).toString();

                try {
                    //System.out.println(outputS);
                    b3.write(outputS);
                    b3.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            b3.close();

            b4.write("PersonID,PartyID");
            b4.newLine();
            for (i = 0; i < pr.size(); i++) {
                outputS = pr.get(i).toString();

                try {
                    //System.out.println(outputS);
                    b4.write(outputS);
                    b4.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            b4.close();

        } catch (IOException ex) {
            Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void formatPeople() {
        String outputS;
        for (int z = 0; z < people.size(); z++) {

            outputS = people.get(z).toString();
            outputS = outputS.trim();
            outputS = outputS.replace("\ufeff", "");

            String[] p = outputS.split(",");
            if (p.length > 0) {

                for (int i = 0; i < p.length; i++) {
                    String fp = p[i];
                    i++;
                    if (i < p.length) {
                        String sent = p[i].toLowerCase();
                        String o = p[i];
                        if (sent.contains("minister") || sent.contains("secretary") || sent.contains("chancellor")) {

                            if (sent.contains("prime minister")) {
                                sent = "Mrs. Margaret Thatcher";
                                sent = fp + "," + sent;
                                peep.add(sent);
                                break;
                            } else {
                                sent = o.substring(o.indexOf("(") + 1, o.indexOf(")"));
                                //System.out.println("Contains");
                                String n = fp + "," + sent;

                                peep.add(n);
                                break;
                            }
                        } else if (sent.contains("government")) {
                            sent = "Government";
                            sent = fp + "," + sent;
                            peep.add(sent);
                            break;
                        } else {
                            if (sent.contains("(")) {
                                int in = sent.indexOf("(");

                                sent = o.substring(0, in);
                                //System.out.println("Bracket: " + in );
                                String n = fp + "," + sent;
                                peep.add(n);
                                break;

                            }
                        }

                        peep.add(outputS);
                        break;

                    }
                }
            }

        }

    }

    public static void sortPeople() {
        for (int i = 0; i < peep.size(); i++) {
            String sent = peep.get(i).toString();
            sent = sent.trim();
            sent = sent.replace("\ufeff", "");
            String[] p = sent.split(",");

            for (int z = 0; z < p.length; z++) {
                String ID = p[z];
                z++;
                String pers = p[z];
                String[] pArr = pers.split(" ");

                int s = pArr.length;

                if (s > 1) {
                    String sur = pArr[s - 1];
                    String fir = pArr[s - 2];
                    doCheck(sur, fir, i, ID);
                }
                System.out.println("Check all");

            }

        }
    }

    public static void doCheck(String sur, String fir, int q, String IDD) {
        for (int i = 0; i < peep.size(); i++) {
            if (q == i) {

            } else {
                String sent = peep.get(i).toString();
                sent = sent.trim();
                sent = sent.replace("\ufeff", "");
                String[] p = sent.split(",");

                for (int z = 0; z < p.length; z++) {
                    String ID = p[z];
                    z++;
                    String pers = p[z];
                    String[] pArr = pers.split(" ");

                    int s = pArr.length;

                    if (s > 1) {
                        String su = pArr[s - 1];
                        String fi = pArr[s - 2];

                        if (sur.equals(su) && fir != fi) {
                            if (fi.equals("Mr.") || fi.equals("Mrs.") || fi.equals("Dr.") || fi.equals("Sir") || fi.equals("Lady")) {
                                changeVals(ID, IDD);
                                peep.remove(i);
                                i--;
                            }
                        }
                    }

                }
            }
        }
    }

    public static void changeVals(String OID, String NID) {
        int OID1 = Integer.parseInt(OID);
        int NID1 = Integer.parseInt(NID);
        for (int i = 0; i < ps.size(); i++) {
            int AID = ps.get(i).getAID();
            int SID = ps.get(i).getSID();

            PositiveSent p = new PositiveSent();
            NegativeSent n = new NegativeSent();

            p.setText(ps.get(i).getText());
            n.setText(ns.get(i).getText());

            if (AID == OID1) {
                p.setAID(NID1);
                n.setAID(NID1);

            } else {
                p.setAID(AID);
                n.setAID(AID);
            }

            if (SID == OID1) {
                p.setSID(NID1);
                n.setSID(NID1);
            } else {
                p.setSID(SID);
                n.setSID(SID);
            }

            ps.set(i, p);
            ns.set(i, n);
        }
    }

    public static void getNameInstances(String sent) {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation document = new Annotation(sent);
        int counter = 0;

// run all Annotators on this text
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        Boolean check = false;
        int count = 0;
        String n;

        for (CoreMap sentence : sentences) {

            PersonInfo p = new PersonInfo();
            SentenceInfo s = new SentenceInfo();
            n = sentence.toString();
            count++;

            if (n.contains("and learned")) {
                n = n.replace(" and learned", "");
            }

            if (n.contains("Livingstone")) {
                System.out.println("");
            }

            if (n.matches(".*\\bhon Member\\b.*") || n.matches(".*\\bhon. Member\\b.*"))//n.contains("hon Member") || n.contains("hon. Member"))
            {
                //System.out.println("Contains Hon");
                //System.out.println(n);
                check = true;
                int nameLoc = 0;
                int willIndex = -1;
                int OID = idInP(n);
                String OS = surInP(n);
                String location = getLocation("Member", n, p);
                if (checkId(count)) {
                    if (n.contains("it is so important")) {
                        System.out.println("");
                    }
                    nameLoc = n.indexOf("hon. Member");
                    if (nameLoc == -1) {
                        nameLoc = n.indexOf("hon Member");
                    }
                    String tN = n;
                    tN = n.toLowerCase();
                    if (tN.contains("will")) {
                        willIndex = tN.indexOf("will");
                    }

                    if (nameLoc < willIndex || willIndex == -1 || nameLoc == -1) {
                        //System.out.println("contines");
                        System.out.println(n);
                        if (location.equals("")) {
                            int nID = idInP(n);
                            String nS = surInP(n);
                            String nF = firInP(n);

                            if (nID != OID) {
                                if (nS.equals(OS)) {
                                    OID = nID;
                                } else {
                                    //get the speaker name
                                    s.setFName(nF);
                                    s.setSur(nS);
                                    s.setSentenceID(count);
                                    s.setText(n);
                                    sentInP(n);
                                    s.setSF(fNa);
                                    s.setSS(sNa);
                                    speech.add(s);
                                    willIndex = -1;
                                    break;
                                }
                            }
                        } else {
                            getLName(location);
                            s.setFName(first);
                            s.setSur(second);
                            s.setSentenceID(count);
                            s.setText(n);

                            sentInP(n);
                            s.setSF(fNa);
                            s.setSS(sNa);

                            speech.add(s);
                            first = null;
                            second = null;
                            willIndex = -1;
                        }
                    }
                    //check = false;
                } else {
                    //System.out.println("Aready In list");
                    check = false;
                }

            } else if (n.matches(".*\\bhon Gentleman\\b.*") || n.matches(".*\\bhon. Gentleman\\b.*")) {
                if (n.contains("right-thinking")) {
                    System.out.print("");
                }
                //System.out.println(n);
                int nameLoc = 0;
                int willIndex = -1;

                check = true;
                int OID = idInP(n);
                String OS = surInP(n);
                if (checkId(count)) {
                    nameLoc = n.indexOf("hon. Gentleman");
                    if (nameLoc == -1) {
                        nameLoc = n.indexOf("hon Gentleman");
                    }
                    String tN = n;
                    tN = n.toLowerCase();
                    if (tN.contains("will")) {
                        willIndex = tN.indexOf("will");
                        String[] t = tN.split(" ");
                        for (int i = 0; i < t.length; i++) {
                            if (t[i].equals("will")) {
                                if (t[i + 1].equals("be") || t[i + 1].equals("flow") || t[i + 1].equals("have")) {
                                    willIndex = -1;
                                    break;
                                }
                            }
                        }
                    }

                    if (nameLoc < willIndex || willIndex == -1 || nameLoc == -1) {
                        //System.out.println("contines");
                        for (int i = count - 2; i >= 0; i--) {
                            String pS = sentences.get(i).toString();
                            if (pS.matches(".*\\bhon Member\\b.*") || pS.matches(".*\\bhon. Member\\b.*")) {
                                String location = getLocation("Member", pS, p);
                                getLName(location);
                                s.setFName(first);
                                s.setSur(second);
                                s.setSentenceID(count);
                                s.setText(n);
                                sentInP(n);
                                s.setSF(fNa);
                                s.setSS(sNa);
                                speech.add(s);
                                willIndex = -1;

                                break;
                            } else {
                                int nID = idInP(pS);
                                String nS = surInP(pS);
                                String nF = firInP(pS);

                                if (nID != OID) {
                                    if (nS.equals(OS)) {
                                        OID = nID;
                                    } else {
                                        //get the speaker name
                                        s.setFName(nF);
                                        s.setSur(nS);
                                        s.setSentenceID(count);
                                        s.setText(n);
                                        sentInP(n);
                                        s.setSF(fNa);
                                        s.setSS(sNa);
                                        speech.add(s);
                                        willIndex = -1;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //System.out.println(n);
                    // check = false;
                } else {
                    //System.out.println("Aready In list");
                    check = false;
                }

            } else if (n.matches(".*\\bhon Lady\\b.*") || n.matches(".*\\bhon. Lady\\b.*")) {
                //System.out.println("Contains Lady");
                //System.out.println(n);
                int willIndex = -1;
                int nameLoc = 0;
                check = true;
                int OID = idInP(n);
                String OS = surInP(n);
                check = true;
                if (checkId(count)) {
                    //System.out.println("contines");
                    System.out.println(n);
                    nameLoc = n.indexOf("hon. Lady");
                    if (nameLoc == -1) {
                        nameLoc = n.indexOf("hon Lady");
                    }
                    String tN = n;
                    tN = n.toLowerCase();
                    if (tN.contains("will")) {
                        willIndex = tN.indexOf("will");
                    }

                    if (nameLoc < willIndex || willIndex == -1 || nameLoc == -1) {

                        for (int i = count - 2; i >= 0; i--) {
                            String pS = sentences.get(i).toString();
                            if (pS.matches(".*\\bhon Member\\b.*") || pS.matches(".*\\bhon. Member\\b.*")) {
                                String location = getLocation("Member", pS, p);
                                getLName(location);
                                s.setFName(first);
                                s.setSur(second);
                                s.setSentenceID(count);
                                s.setText(n);
                                sentInP(n);
                                s.setSF(fNa);
                                s.setSS(sNa);
                                speech.add(s);
                                willIndex = -1;

                                break;
                            } else {
                                int nID = idInP(pS);
                                String nS = surInP(pS);
                                String nF = firInP(pS);

                                if (nID != OID) {
                                    if (nS.equals(OS)) {
                                        OID = nID;
                                    } else {
                                        //get the speaker name
                                        s.setFName(nF);
                                        s.setSur(nS);
                                        s.setSentenceID(count);
                                        s.setText(n);
                                        sentInP(n);
                                        s.setSF(fNa);
                                        s.setSS(sNa);
                                        speech.add(s);
                                        willIndex = -1;
                                        break;

                                    }
                                }
                            }
                        }
                    }
                    // check = false;
                } else {
                    //System.out.println("Aready In list");
                    check = false;
                }
            } else if (n.matches(".*\\bthe Minister\\b.*")) {
                //System.out.println("Contains Lady");
                //System.out.println(n);

                int willIndex = -1;
                int nameLoc = 0;
                check = true;
                int OID = idInP(n);
                String OS = surInP(n);
                check = true;
                if (checkId(count)) {
                    //System.out.println("contines");
                    System.out.println(n);
                    nameLoc = n.indexOf("the Minister");

                    String tN = n;
                    tN = n.toLowerCase();
                    if (tN.contains("will")) {
                        willIndex = tN.indexOf("will");
                    }

                    if (nameLoc < willIndex || willIndex == -1 || nameLoc == -1) {

                        for (int i = count - 2; i >= 0; i--) {
                            String pS = sentences.get(i).toString();
                            if (pS.matches(".*\\bhon Member\\b.*") || pS.matches(".*\\bhon. Member\\b.*")) {
                                String location = getLocation("Member", pS, p);
                                getLName(location);
                                s.setFName(first);
                                s.setSur(second);
                                s.setSentenceID(count);
                                s.setText(n);
                                sentInP(n);
                                s.setSF(fNa);
                                s.setSS(sNa);
                                speech.add(s);
                                willIndex = -1;

                                break;
                            } else {
                                int nID = idInP(pS);
                                String nS = surInP(pS);
                                String nF = firInP(pS);

                                if (nID != OID) {
                                    if (nS.equals(OS)) {
                                        OID = nID;
                                    } else {
                                        //get the speaker name
                                        s.setFName(nF);
                                        s.setSur(nS);
                                        s.setSentenceID(count);
                                        s.setText(n);
                                        sentInP(n);
                                        s.setSF(fNa);
                                        s.setSS(sNa);
                                        speech.add(s);
                                        willIndex = -1;
                                        break;

                                    }
                                }
                            }
                        }
                    }
                    // check = false;
                } else {
                    //System.out.println("Aready In list");
                    check = false;
                }
            } else if (n.matches(".*\\bthe Government\\b.*") || n.matches(".*\\bthe British Government\\b.*") || n.matches("The Government\\b.*") || n.matches("The British Government\\b.*")) {
                //System.out.println("Contains Lady");
                //System.out.println(n);
                int willIndex = -1;
                int nameLoc = 0;
                check = true;
                int OID = idInP(n);
                String OS = surInP(n);
                check = true;
                if (checkId(count)) {
                    //System.out.println("contines");
                    System.out.println(n);
                    nameLoc = n.indexOf("the Government");
                    if (nameLoc == -1) {
                        nameLoc = n.indexOf("the British Government");
                    }
                    String tN = n;
                    tN = n.toLowerCase();
                    if (tN.contains("will")) {

                        willIndex = tN.indexOf("will");
                        String[] t = tN.split(" ");
                        for (int i = 0; i < t.length; i++) {
                            if (t[i].equals("will")) {
                                if (t[i + 1].equals("be")) {
                                    willIndex = -1;
                                    break;
                                }
                            }
                        }
                    }
                    int parNum = 0;

                    if (nameLoc < willIndex || willIndex == -1 || nameLoc == -1) {

                        parNum = sentLoc(n);

                        if (parNum > 2) {
                            s.setFName("Government");
                            s.setSur("Government");
                            s.setSentenceID(count);
                            s.setText(n);
                            sentInP(n);
                            s.setSF(fNa);
                            s.setSS(sNa);
                            speech.add(s);
                            willIndex = -1;
                        }

                    }
                    // check = false;
                } else {
                    //System.out.println("Aready In list");
                    check = false;
                }
            } else if (n.matches(".*\\bPrime Minister\\b.*")) {
                //System.out.println("Contains Lady");
                //System.out.println(n);
                int willIndex = -1;
                int nameLoc = 0;
                check = true;
                int OID = idInP(n);
                String OS = surInP(n);
                check = true;
                if (checkId(count)) {
                    //System.out.println("contines");
                    System.out.println(n);
                    nameLoc = n.indexOf("Prime Minister");

                    String tN = n;
                    tN = n.toLowerCase();
                    if (tN.contains("will")) {

                        willIndex = tN.indexOf("will");
                        String[] t = tN.split(" ");
                        for (int i = 0; i < t.length; i++) {
                            if (t[i].equals("will")) {
                                if (t[i + 1].equals("be")) {
                                    willIndex = -1;
                                    break;
                                }
                            }
                        }
                    }
                    int parNum = 0;

                    if (nameLoc < willIndex || willIndex == -1 || nameLoc == -1) {

                        parNum = sentLoc(n);

                        if (parNum > 2) {
                            s.setFName("Prime");
                            s.setSur("Minister");
                            s.setSentenceID(count);
                            s.setText(n);
                            sentInP(n);
                            s.setSF(fNa);
                            s.setSS(sNa);
                            speech.add(s);
                            willIndex = -1;
                        }

                    }
                    // check = false;
                } else {
                    //System.out.println("Aready In list");
                    check = false;
                }
            } else if (n.matches(".*\\bhon Friend\\b.*") || n.matches(".*\\bhon. Friend\\b.*")) {

                //System.out.println("Contains Friend");
                //System.out.println(n);
                if (n.contains("Is my right")) {
                    System.out.println("");
                }
                int nameLoc = 0;
                int willIndex = -1;
                check = true;
                check = true;
                int OID = idInP(n);
                String OS = surInP(n);
                check = true;
                if (checkId(count)) {
                    //System.out.println("contines");
                    if (n.contains("This will be")) {
                        System.out.print("");
                    }
                    nameLoc = n.indexOf("hon. Friend");
                    if (nameLoc == -1) {
                        nameLoc = n.indexOf("hon Friend");
                    }
                    String tN = n;
                    tN = n.toLowerCase();
                    if (tN.contains("will")) {

                        willIndex = tN.indexOf("will");
                        String[] t = tN.split(" ");
                        for (int i = 0; i < t.length; i++) {
                            if (t[i].equals("will")) {
                                if (t[i + 1].equals("be")) {
                                    willIndex = -1;
                                    break;
                                }
                            }
                        }
                    }

                    if (nameLoc < willIndex || willIndex == -1 || nameLoc == -1) {
                        for (int i = count - 2; i >= 0; i--) {
                            String pS = sentences.get(i).toString();
                            if (pS.matches(".*\\bhon Member\\b.*") || pS.matches(".*\\bhon. Member\\b.*")) {
                                String location = getLocation("Member", pS, p);
                                getLName(location);
                                if (first != null) {
                                    s.setFName(first);
                                    s.setSur(second);
                                    s.setSentenceID(count);
                                    s.setText(n);
                                    sentInP(n);
                                    s.setSF(fNa);
                                    s.setSS(sNa);
                                    speech.add(s);
                                    willIndex = -1;

                                    break;
                                }
                            } else {
                                int nID = idInP(pS);
                                String nS = surInP(pS);
                                String nF = firInP(pS);

                                if (nID != OID && nS != "") {
                                    if (nS.equals(OS)) {
                                        OID = nID;
                                    } else {
                                        //get the speaker name
                                        s.setFName(nF);
                                        s.setSur(nS);
                                        s.setSentenceID(count);
                                        s.setText(n);
                                        sentInP(n);
                                        s.setSF(fNa);
                                        s.setSS(sNa);
                                        speech.add(s);
                                        willIndex = -1;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    //System.out.
                    //check = false;
                } else {
                    //System.out.println("Aready In list");
                    check = false;
                }

            }

            if (check == true) {

                check = false;
            }

            // for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
            // }
        }

    }

    public static boolean checkId(int sentenceId) {
        for (Object speech1 : speech) {
            Class<?> cl = speech1.getClass();
            try {
                Field f = cl.getField("sentenceId");
                Object fv = f.get(speech1);

                //System.out.println(fv);
                if ((Integer) fv == sentenceId) {
                    //System.out.println("Already added");
                    return false;
                }
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(NLP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return true;
    }

    public static String getLocation(String type, String sent, PersonInfo p) {
        String[] parts = sent.split(" ");
        String location = "";

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(type)) {
                if (i + 2 < parts.length) {
                    String loc = parts[i + 2];
                    int last = loc.length();
                    String con = parts[i + 3];

                    if (Character.isUpperCase(loc.charAt(0))) {
                        System.out.print(parts[i + 2]);
                        location = loc;
                        if (loc.charAt(last - 1) == ',') {
                            String loc1 = parts[i + 3];
                            location += " " + loc1;
                            System.out.println(loc1);
                        }
                        if (con.equals("and")) {
                            System.out.print(" " + "and" + " ");
                            location += " " + "and" + " ";
                            String loc1 = parts[i + 4];
                            if (Character.isUpperCase(loc1.charAt(0))) {
                                System.out.print(loc1);
                                location += loc1;
                            }
                            if (loc1.charAt(loc1.length() - 1) == ',') {
                                String loc2 = parts[i + 5];
                                System.out.print(loc2);
                                location += " " + loc2;
                            } else if (parts[i + 5].charAt(0) == '(') {
                                first = parts[i + 5].substring(1);
                                second = parts[i + 6].substring(0, parts[i + 6].length() - 2);

                                System.out.println(first + " " + second);
                                p.setLoc(location);
                                p.setFName(first);
                                p.setSur(second);
                                break;
                            } else {
                                first = null;
                                second = null;
                            }

                        } else if (parts[i + 4].charAt(0) == '(') {
                            first = parts[i + 4].substring(1);
                            second = parts[i + 5].substring(0, parts[i + 5].length() - 1);

                            if (second.charAt(second.length() - 1) == ')') {
                                second = second.substring(0, second.length() - 1);
                            }
                            System.out.println(first + " " + second);
                            p.setLoc(location);
                            p.setFName(first);
                            p.setSur(second);
                        } else {
                            first = null;
                            second = null;
                        }
                    }
                }
            }
        }

        boolean check = true;

        for (int i = 0; i < locName.size(); i++) {

            if (second != null) {
                if (second.equals(locName.get(i).getSur())) {
                    check = false;
                    break;
                }
            } else {
                check = false;
                break;
            }
        }
        if (check == true) {
            locName.add(p);
        }

        return location;
    }

    public static void getLName(String location) {
        if (first == null) {
            for (int i = 0; i < locName.size(); i++) {
                if (location.equals(locName.get(i).getLoc())) {
                    first = locName.get(i).getFName();
                    second = locName.get(i).getSur();
                    break;
                }
            }
        }
    }

    public static void getParaGraphs(ArrayList list) {
        String PF = "";
        boolean check = false;
        String PS = "";
        for (int i = 0; i < list.size(); i++) {
            String fN = "";
            String sur = "";
            ParaInfo pi = new ParaInfo();
            String text = list.get(i).toString();
            pi.setText(text);
            String find = ":";
            if (text.contains(":")) {
                text = text.substring(0, (text.indexOf(find) + find.length()) - 1);
                if (text.contains(".")) {
                    int pos = text.indexOf(".");
                    PF = text.substring(0, pos + 1);
                    PS = text.substring(pos + 2, text.length());
                    check = true;
                }
            }
            if (text.contains("The Prime Minister")) {
                PF = "Mrs.";
                PS = "Thatcher";
                check = true;
            }
            if ("".equals(text)) {
                check = true;
            }

            if (!check) {
                String ner = getNER(text);
                String[] nerA = ner.split(" ");

                if (nerA[1].contains("PERSON")) {
                    String f = nerA[1].substring(0, nerA[1].length() - 7);
                    for (int z = 0; z < pInfo.size(); z++) {
                        String sn = pInfo.get(z).getSur();
                        if (sn.equals(f)) {
                            fN = pInfo.get(z).getFName();
                            sur = pInfo.get(z).getSur();

                            PF = fN;
                            PS = sur;

                            pi.setFName(fN);
                            pi.setSur(sur);
                            check = true;
                            break;
                        }
                    }
                    if (!check) {
                        f = nerA[1];
                        String s = "";
                        if (nerA.length > 2) {
                            if (nerA[2].contains("PERSON")) {
                                s = nerA[2];
                                fN = f.substring(0, f.length() - 7);
                                sur = s.substring(0, s.length() - 7);
                            }
                        } else {
                            s = nerA[1];
                            f = nerA[0];
                            fN = f.substring(0, f.length() - 2);
                            sur = s.substring(0, s.length() - 7);
                        }

                        PF = fN;
                        PS = sur;

                        pi.setFName(fN);
                        pi.setSur(sur);
                        check = true;
                    }

                }

                if (!check) {

                    for (int z = 0; z < nerA.length; z++) {
                        if (nerA[z].contains("PERSON")) {
                            String f = nerA[z];
                            String s = "";
                            if (z + 1 > nerA.length - 1) {
                                s = nerA[z];
                                f = nerA[z - 1];
                                fN = f.substring(0, f.length() - 2);
                                sur = s.substring(0, s.length() - 7);
                            } else {
                                if (nerA[z + 1].contains("PERSON")) {
                                    s = nerA[z + 1];
                                    fN = f.substring(0, f.length() - 7);
                                    if (s.contains("RRB")) {
                                        sur = s.substring(0, s.length() - 14);
                                    } else {
                                        sur = s.substring(0, s.length() - 7);
                                    }
                                } else {
                                    s = nerA[z];
                                    f = nerA[z - 1];
                                    fN = f.substring(0, f.length() - 2);
                                    sur = s.substring(0, s.length() - 7);
                                }
                            }

                            PF = fN;
                            PS = sur;

                            pi.setFName(fN);
                            pi.setSur(sur);

                            break;
                        }
                    }

                }
            }
            if (PF.equals("") || PS.equals("")) {

            }
            pi.setFName(PF);
            pi.setSur(PS);
            int id = i + 1;
            pi.setID(id);

            if (PS.equals("Speaker")) {
                System.out.println("Speaker Impartial");
            } else {
                pInfo.add(pi);
            }

            check = false;

        }

        System.out.print("");
    }

    public static void sentInP(String sent) {
        int id = 0;
        String sur = "";
        for (int i = 0; i < pInfo.size(); i++) {
            String nn = pInfo.get(i).getText();
            if (pInfo.get(i).getText().contains(sent)) {
                fNa = pInfo.get(i).getFName();
                sNa = pInfo.get(i).getSur();

                break;
            }
        }

    }

    public static int sentLoc(String sent) {
        int par = 0;
        if (sent.contains("'s")) {
            sent = sent.replace(" 's", "");
            sent = sent.replace("'s", "");
            sent = sent.trim();
        }
        for (int i = 0; i < pInfo.size(); i++) {
            String nn = pInfo.get(i).getText();

            if (nn.contains("'s")) {
                nn = nn.replace("'s", "");
            }
            nn = nn.trim();
            if (nn.contains(sent)) {
                par = pInfo.get(i).getID();

                break;
            }
        }

        return par;
    }

    public static int idInP(String sent) {
        int id = 0;
        String sur = "";
        for (int i = 0; i < pInfo.size(); i++) {
            if (pInfo.get(i).getText().contains(sent)) {
                id = pInfo.get(i).getID();
                sur = pInfo.get(i).getSur();

                return id;
            }
        }
        return id;
    }

    public static String surInP(String sent) {
        String sur = "";

        for (int i = 0; i < pInfo.size(); i++) {
            if (pInfo.get(i).getText().contains(sent)) {

                sur = pInfo.get(i).getSur();
                return sur;
            }
        }
        return sur;
    }

    public static String firInP(String sent) {
        String fir = "";

        for (int i = 0; i < pInfo.size(); i++) {
            if (pInfo.get(i).getText().contains(sent)) {

                fir = pInfo.get(i).getFName();
                return fir;
            }
        }
        return fir;
    }

    public static void removeReportedSpeech() {
        for (int i = 0; i < speech.size(); i++) {
            String text = speech.get(i).getText();
            if (text.contains("prime minister")) {
                System.out.print("");
            }
            String te = speech.get(i).getText();
            String[] teA = te.split(" ");
            int size = teA.length;

            text = text.toLowerCase();
            if (text.contains("you say") || text.contains("saying") || text.contains("told me") || text.contains("asked") || text.contains("i can") || text.contains("disagree") || text.contains("wishes")) {
                speech.remove(i);
                i--;
            } else {
                if (size < 6) {
                    speech.remove(i);
                    i--;
                }
            }

        }

        for (int i = 0; i < speech.size(); i++) {
            String t = speech.get(i).getText().toLowerCase();
            String[] text = t.split(" ");
            String last = text[text.length - 1];
            if (text[0].equals("can") || text[0].equals("we") || text[0].equals("will") && !text[1].equals("be") || text[1].equals("will") && !text[2].equals("be") || text[2].equals("will") && !text[3].equals("be")) {
                if (!t.contains("prime minister")) {
                    speech.remove(i);
                    i--;
                }
            } else if (text[0].equals("i") && text[1].equals("am")) {
                //speech.remove(i);
                //i--;
            } else if (t.contains("will") && last.contains("?")) {
                if (!t.contains("will be") && !t.contains("will have") && !t.contains("will not") && !t.contains("will fully")) {
                    speech.remove(i);
                    i--;
                }
            } else if (text[text.length - 1].equals("they") || text[text.length - 2].equals("they") || text[text.length - 3].equals("they")) {
                speech.remove(i);
                i--;
            }

        }

        for (int i = 0; i < speech.size(); i++) {
            int par = sentLoc(speech.get(i).getText());

            if (par == 1 || par == 2) {
                speech.remove(i);
                i--;
            }
        }
    }

    public static void createP(String f, String s) {
        People pe = new People();
        Boolean check = false;
        pe.setFName(f);
        pe.setSur(s);
        if (people.size() < 1) {
            pe.setID(1);
            people.add(pe);
        } else {
            pe.setID(people.size() + 1);
            for (int i = 0; i < people.size(); i++) {
                if (people.get(i).getSur().equals(s) || s == null) {
                    check = false;
                    break;
                } else {

                    check = true;

                }
            }
        }

        if (check) {
            people.add(pe);
        }

    }

    public static void createLists() {
        Party p = new Party();

        p.setID(1);
        p.setParty("Labour");
        party.add(p);
        Party p1 = new Party();
        p1.setID(2);
        p1.setParty("Conservative");
        party.add(p1);
        Party p2 = new Party();
        p2.setID(3);
        p2.setParty("Liberal Democrat");

        party.add(p2);

        for (int i = 0; i < speech.size(); i++) {
            String f = speech.get(i).getFName();
            String s = speech.get(i).getSur();

            createP(f, s);

            f = speech.get(i).getSF();
            s = speech.get(i).getSS();

            createP(f, s);
        }

        for (int z = 0; z < speech.size(); z++) {
            String sent;
            String sur;
            String attacked;
            sent = speech.get(z).getText();
            sur = speech.get(z).getSur();
            attacked = speech.get(z).getSS();
            getSentiment(sent, sur, attacked);

        }

        PartyRel pr00 = new PartyRel();

        pr00.setAID(1);
        pr00.setPPID(2);
        pr.add(pr00);
        PartyRel pr0 = new PartyRel();

        pr0.setAID(2);
        pr0.setPPID(1);
        pr.add(pr0);
        PartyRel pr1 = new PartyRel();

        pr1.setAID(3);
        pr.add(pr1);
        //pr.setPPID(null);
        PartyRel pr2 = new PartyRel();

        pr2.setAID(4);
        pr2.setPPID(2);
        pr.add(pr2);
        PartyRel pr3 = new PartyRel();

        pr3.setAID(5);
        pr3.setPPID(1);
        pr.add(pr3);

        //create method to find MP's parties
        System.out.println("Done");
    }

    public static int surToID(String sur) {
        int ID = 0;

        for (int i = 0; i < people.size(); i++) {
            String sur1;
            sur1 = people.get(i).getSur();
            if (sur1.equals(sur)) {
                ID = people.get(i).getID();
                break;
            }
        }

        return ID;
    }

    public static void getSentiment(String text, String sur, String attacked) {
        //do sentiment stuff
        PositiveSent p = new PositiveSent();
        NegativeSent n = new NegativeSent();

        //if(sentiment.equals("Positive"))
        //{
        p.setAID(surToID(sur));
        p.setSID(surToID(attacked));
        p.setText(text);
        ps.add(p);
        // }
        // else
        // {

        n.setAID(surToID(sur));
        n.setSID(surToID(attacked));
        text = lemma(text);
        text = removePPN(text);
        System.out.println(text);
        n.setText(text);
        ns.add(n);

        // }
    }

    public static String removePPN(String text) {

        Annotation doc = new Annotation(text);

        pipe.annotate(doc);
        String newText = "";

        List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            int i = 0;
            String word1;
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                word1 = token.get(TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(PartOfSpeechAnnotation.class);

                if (pos.equals("PRP") || pos.equals("PRP$") || pos.equals("NNP")) {
                    String t = token.originalText();
                    //System.out.println(t);
                } else {
                    newText += word1;
                    newText += " ";
                }

            }
        }

        System.out.println(newText);

        return newText;
    }

    public static String lemma(String text) {

        String newText = "";
        Annotation doc = new Annotation(text);
        pipeline.annotate(doc);

        List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            int i = 0;
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {

                newText += token.get(LemmaAnnotation.class);
                newText += " ";

            }
        }

        newText = newText.replace("-lrb-", "");
        newText = newText.replace("-rrb-", "");
        newText = newText.replace(",", "");
        newText = newText.replace("  ", " ");
        System.out.println(newText);
        return newText;
    }

    public static void formatList() {
        Set<SentenceInfo> hs = new HashSet<>();
        hs.addAll(speech);
        speech.clear();
        speech.addAll(hs);
    }
}
