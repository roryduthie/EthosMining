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
public class Party {
    public int ID;
    public String party;
    
    public int getID()
    {
        return ID;
    }
    public void setID(int newID)
    {
        this.ID = newID;
    }
    
    public String getParty()
    {
        return party;
    }
    public void setParty(String newParty)
    {
        party = newParty;
    }
    
    @Override
   public String toString() {
        return (this.getID() + "," + this.getParty());
                   
   }
}
