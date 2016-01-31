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
class PersonInfo {
    public String fName;
    public String sName;
    public String loc;
    public int ID;
    
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
    
    public String getLoc()
    {
        return loc;
    }
    
    public void setLoc(String nLoc)
    {
        this.loc = nLoc;
    }
    
    public int getID()
    {
        return ID;
    }
    public void setID(int newID)
    {
        this.ID = newID;
    }
    
}

