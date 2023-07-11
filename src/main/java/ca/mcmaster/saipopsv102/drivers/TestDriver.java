/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.saipopsv102.drivers;
  
import static ca.mcmaster.saipopsv102.Constants.*;
import static ca.mcmaster.saipopsv102.Parameters.*;
import ca.mcmaster.saipopsv102.constraints.*;
import ca.mcmaster.saipopsv102.hueristics.SaiPOPS;
import ca.mcmaster.saipopsv102.utilities.CplexUtils;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import static java.lang.System.exit;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author tamvadss
 */
public class TestDriver {
    
   
    
    public static void main(String[] args) throws Exception{
        
        IloCplex cplex;
     
        TreeMap<String, Double>  objectiveFunctionMap =null;
        TreeMap<String, IloNumVar>  mapOfAllVariablesInTheModel = new TreeMap<String, IloNumVar> ();
        //constraints, smallest first
        TreeMap<Integer, HashSet<LowerBoundConstraint>> mapOfAllConstraintsInTheModel = 
            new TreeMap<Integer, HashSet<LowerBoundConstraint>> ();
        
        cplex = new IloCplex ();
        
        System.out.println ("CPLEX version "+ cplex.getVersion());
        boolean isWindows =   System.getProperty("os.name").toLowerCase().contains("win") ;
        if (!cplex.getVersion().startsWith("22") && ! isWindows){
            System.err.println ("CPLEX version not the latest -- STOP" );
            exit(ONE);
        }
        
        cplex.importModel( PRESOLVED_MIP_FILENAME);
        CplexUtils.setCplexParameters(cplex) ;
         
        objectiveFunctionMap = CplexUtils.getObjective(cplex);
                
        for ( IloNumVar var : CplexUtils.getVariables(cplex)){
            mapOfAllVariablesInTheModel.put (var.getName(), var);
        }
        
        List<LowerBoundConstraint> lbcList = CplexUtils.getConstraints(cplex,objectiveFunctionMap );
                
        //arrange by size
        for (LowerBoundConstraint lbc: lbcList){
            int numVars = lbc.getVariableCount();
                        
            HashSet<LowerBoundConstraint> current =  mapOfAllConstraintsInTheModel.get (numVars);
            if (null==current) current = new HashSet<LowerBoundConstraint>();
            current.add (lbc) ;
            mapOfAllConstraintsInTheModel.put (numVars, current);               
        }
        
        /*for (HashSet<LowerBoundConstraint> cSet : mapOfAllConstraintsInTheModel.values()){
            for (LowerBoundConstraint lbc : cSet){
                lbc.sort();     
                 
            }           
        }*/
       
        TreeMap<String, Boolean> fixings = new  TreeMap<String, Boolean>();
        TreeMap<String, Double>  freeVariables = new TreeMap<String, Double>  ();
        TreeSet <String> fractionalvariables = new TreeSet <String> ();
        
        fractionalvariables.add ("x1");
        fractionalvariables.add ("x2");
        fractionalvariables.add ("x3");
        fractionalvariables.add ("x4");
        fixings.put ("x7", true) ;
        
        freeVariables.put ("x1", HALF);
        freeVariables.put ("x2", HALF);
        freeVariables.put ("x3", HALF);
        freeVariables.put ("x4", HALF);
        freeVariables.put ("x5", DOUBLE_ONE);
        freeVariables.put ("x6", DOUBLE_ONE); 
        
        String branchingVar =  (new SaiPOPS(fixings, freeVariables,fractionalvariables,
                        mapOfAllConstraintsInTheModel, objectiveFunctionMap))
                        .getBranchingVariable(   );
                
    }
    
}
