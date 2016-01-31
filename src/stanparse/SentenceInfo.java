/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stanparse;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author Rory
 */
public class SentenceInfo {
    
   public int sentenceId;
   public String text;
   public String fName;
   public String surname;
   public String speakerF;
   public String speakerS;
   public String sentiment;
    
   
   @Override
    public int hashCode() {
        return new HashCodeBuilder(). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(sentenceId).
            append(text).
            append(fName).
            append(surname).
            append(speakerF).
            append(speakerS).
            append(sentiment).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof SentenceInfo))
            return false;
        if (obj == this)
            return true;

        SentenceInfo rhs = (SentenceInfo) obj;
        return new EqualsBuilder().
            // if deriving: appendSuper(super.equals(obj)).
            append(sentenceId, rhs.sentenceId).
            append(text, rhs.text).
            append(fName, rhs.fName).
            append(surname, rhs.surname).
            append(speakerF, rhs.speakerF).
            append(speakerS, rhs.speakerS).
            append(sentiment, rhs.sentiment).
            isEquals();
    }
    
    public int getSentenceID()
    {
        return sentenceId;
    }
    public void setSentenceID(int newID)
    {
        sentenceId = newID;
    }
    
    public String getText()
    {
        return text;
    }
    public void setText(String newText)
    {
        text = newText;
    }
    
    public String getFName()
    {
        return fName;
    }
    public void setFName(String newFName)
    {
        fName = newFName;
    }
    
    public String getSur()
    {
        return surname;
    }
    public void setSur(String newSur)
    {
        surname = newSur;
    }
    
    public String getSF()
    {
        return speakerF;
    }
    public void setSF(String newF)
    {
        this.speakerF = newF;
    }
    
    public String getSS()
    {
        return speakerS;
    }
    public void setSS(String newS)
    {
        this.speakerS = newS;
    }
    
    public String getSentiment()
    {
        return sentiment;
    }
    public void setSentiment(String newS)
    {
        this.sentiment = newS;
    }
    
       @Override
   public String toString() {
        return ("Sentence No: " + this.getSentenceID() + " " + "Name: " +this.getFName()+ " " + this.getSur() + " " + "Text: " + this.getText() + " Speaker: " + this.getSF() + " " + this.getSS() );
                   
   }
    
    
}
