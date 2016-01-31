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
public class PartyRel {
    public int pID;
    public int ppID;
    
    public int getPID()
    {
        return pID;
    }
    public void setAID(int newID)
    {
        this.pID = newID;
    }
     public int getPPID()
    {
        return ppID;
    }
    public void setPPID(int newID)
    {
        this.ppID = newID;
    }
    
    @Override
   public String toString() {
        return (this.getPID() + "," + this.getPPID());
                   
   }
}
