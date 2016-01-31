/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stanparse;

/**
 *
 * @author Rory
 */
public class PositiveSent {
    public int aID;
    public int sID;
    public String text;
    
    public int getAID()
    {
        return aID;
    }
    public void setAID(int newID)
    {
        this.aID = newID;
    }
     public int getSID()
    {
        return sID;
    }
    public void setSID(int newID)
    {
        this.sID = newID;
    }
    
    public String getText()
    {
        return text;
    }
    public void setText(String newText)
    {
        text = newText;
        text = text.replaceAll(",", "");
    }
    
     @Override
   public String toString() {
        return (this.getSID() + "," + this.getAID() + "," +this.getText());
                   
   }
}
