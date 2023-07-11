/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.saipopsv102.callbacks;
     
import static ca.mcmaster.saipopsv102.Constants.ZERO;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author tamvadss
 */
public class EmptyCallback extends IloCplex.BranchCallback{
    
    

    @Override
    protected void main() throws IloException {
        if ( getNbranches()> ZERO ){  
            
           
            
        }
    }
     
  
}
