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
public class People {
    public String fName;
    public String sName;
    public int ID;
    
    public String getFName()
    {
        fName = fName.replace(",", "");
        return fName;
    }
    
    public void setFName(String newName)
    {
        this.fName = newName;
    }
    
    public String getSur()
    {
        sName = sName.replace(",", "");
        return sName;
    }
    
    public void setSur(String newName)
    {
        this.sName = newName;
    }
    
    public int getID()
    {
        return ID;
    }
    public void setID(int newID)
    {
        this.ID = newID;
    }
    
    @Override
   public String toString() {
        return (this.getID() + "," + this.getFName() + " " + this.getSur());
                   
   }
}
