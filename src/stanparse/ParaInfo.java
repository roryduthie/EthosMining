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
public class ParaInfo {
   
    public int paraID;
    public String pText;
    public String fName;
    public String sName;
    
    public int getID()
    {
        return paraID;
    }
    
    public void setID(int newID)
    {
        this.paraID = newID;
    }
    
    public String getText()
    {
        return pText;
    }
    
    public void setText(String newP)
    {
        this.pText = newP;
    }
    
    public String getFName()
    {
        return fName;
    }
    
    public void setFName(String newName)
    {
        this.fName = newName;
    }
    
    public String getSur()
    {
        return sName;
    }
    
    public void setSur(String newName)
    {
        this.sName = newName;
    }
    
}

