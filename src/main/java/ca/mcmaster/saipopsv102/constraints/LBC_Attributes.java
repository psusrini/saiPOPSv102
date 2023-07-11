/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.saipopsv102.constraints;
 
import static ca.mcmaster.saipopsv102.Constants.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author tamvadss
 * 
 * 
 * 
 */
public class LBC_Attributes {
    
   
    public String name ;
    
    public TreeSet<String>  fractional_PrimaryVariables      = new TreeSet<String> (); 

    public TreeSet<String>  fractional_SecondaryVariables      = new TreeSet<String> ();     
    
    public int dimension = BILLION;
    public double highestPossibleLHS= ZERO;
    public int numberOfPrimaryVariables = ZERO;
    public double objectiveMagnitudeSumOfPrimaryVariables= ZERO;
        
    public String toString (){
        String result = "Name " + name ;
              
        result +="\n     Fractional Primary: ";
        for (String str :  fractional_PrimaryVariables){
            result += str + ", ";    
        }
        
        result +="\n     Fractional Sceondary: ";
        for (String str :  fractional_SecondaryVariables){
            result += str + ", ";    
        }
        
        
        return result+"\n";
    }     
    
}
