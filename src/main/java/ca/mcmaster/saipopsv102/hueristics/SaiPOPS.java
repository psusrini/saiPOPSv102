/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.saipopsv102.hueristics;
 
import static ca.mcmaster.saipopsv102.Constants.*;
import static ca.mcmaster.saipopsv102.Parameters.*;
import ca.mcmaster.saipopsv102.constraints.*;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 *
 * @author tamvadss
 */
public class SaiPOPS {
    
    //fixed variables and their values
    public TreeMap<String, Boolean> fixedVariables ;
    //free variables and their fractional values
    public TreeMap<String, Double>  freeVariables  ;
    public TreeSet<String> fractionalVariables ;
    public TreeMap<Integer, HashSet<LowerBoundConstraint>> mapOfAllConstraintsInTheModel;
    public TreeMap<String, Double>  objectiveFunctionMap;
    
    private boolean  zzqn = true; 
       
    protected  static Logger logger;
     
    static   {
        logger=Logger.getLogger(SaiPOPS.class);
        logger.setLevel(LOGGING_LEVEL);
        PatternLayout layout = new PatternLayout("%5p  %d  %F  %L  %m%n");     
        try {
            RollingFileAppender rfa =new  
                RollingFileAppender(layout,LOG_FOLDER+SaiPOPS.class.getSimpleName()+ LOG_FILE_EXTENSION);
            rfa.setMaxBackupIndex(SIXTY);
            logger.addAppender(rfa);
            logger.setAdditivity(false);    
            
            
             
        } catch (Exception ex) {
            ///            
            System.err.println("Exit: unable to initialize logging"+ex);       
            exit(ONE);
        }
    }
    
    public SaiPOPS (TreeMap<String, Boolean> fixings ,TreeMap<String, Double>  freeVariables,
            TreeSet<String> fractionalVariables ,
            TreeMap<Integer, HashSet<LowerBoundConstraint>> mapOfAllConstraintsInTheModel ,
            TreeMap<String, Double>  objectiveFunctionMap
            
            
            ){
        this.fixedVariables=fixings;
        this. freeVariables=freeVariables;
        this .fractionalVariables = fractionalVariables;
        this.mapOfAllConstraintsInTheModel=mapOfAllConstraintsInTheModel;
        this . objectiveFunctionMap = objectiveFunctionMap;
        
         
    }
    
    
    
    public  String  getBranchingVariable (   ){
        
         
         
        TreeMap < String, Integer> fractionalPrimaryVars_FrequencyMap
                = new TreeMap < String, Integer>  ();
        TreeMap < String, Integer> fractionalSecondaryVars_FrequencyMap
                = new TreeMap < String, Integer>  ();
     
         
        for (HashSet<LowerBoundConstraint> lbcSet : mapOfAllConstraintsInTheModel.values()){
            for (LowerBoundConstraint lbc :lbcSet){                               

                LowerBoundConstraint lbcCopy = lbc.getCopy(fixedVariables);

                if (null != lbcCopy){

                    if (is_BUV_Feasible(  lbcCopy)) continue;

                    lbcCopy.recordFractionalStatus(fractionalVariables);   

                    //System.err.println(lbcCopy);

                    LBC_Attributes attr = lbcCopy.getAttributes(  );

                    //System.err.println(attr);



                    if ( attr.fractional_PrimaryVariables.size() >ZERO){

                        for (String var : attr.fractional_PrimaryVariables){
                            Integer current = fractionalPrimaryVars_FrequencyMap.get (var);
                            if (null==current)current = ZERO;
                            fractionalPrimaryVars_FrequencyMap.put (var,ONE + current);
                        }
                        for (String var : attr.fractional_SecondaryVariables){
                            Integer current = fractionalSecondaryVars_FrequencyMap.get (var);
                            if (null==current)current = ZERO;
                            //fractionalSecondaryVars_FrequencyMap.put (var,ONE + current);
                            fractionalSecondaryVars_FrequencyMap.put (var, ONE + current);
                        }

                    }

                }
            }
            if (!fractionalPrimaryVars_FrequencyMap.isEmpty()) break;
        }
         
              
              
        TreeSet<String>  candidates = new TreeSet<String> ();
        
        candidates.addAll( fractionalPrimaryVars_FrequencyMap.keySet());
        candidates.retainAll(fractionalSecondaryVars_FrequencyMap.keySet() );
         
        if (candidates.size()>ZERO){
            candidates = this.getHighestFrequencyVars(
                            fractionalPrimaryVars_FrequencyMap,
                            fractionalSecondaryVars_FrequencyMap, 
                            candidates);     
            
        }else {                 
            candidates = this.getHighestFrequencyVars(fractionalPrimaryVars_FrequencyMap );                   
        }
      
        //tie break
        candidates = getHighestObjMagn (candidates) ;    
        
        //random tiebreak        
        String[] candidateArray = candidates.toArray(new String[ZERO]);        
        return candidateArray[ PERF_VARIABILITY_RANDOM_GENERATOR.nextInt(candidates.size())];
    }
  
    private TreeSet<String>    getHighestFrequencyVars  (  TreeMap < String, Integer> frequencyMap ,TreeSet<String> candidates ){
       
        double LARGEST_KNOWN_FREQ = - ONE;
        TreeSet<String>  winners = new TreeSet<String>();
        
        for (String thisVar : candidates){
            int thisFreq =  frequencyMap.get( thisVar) ;
            if (thisFreq> LARGEST_KNOWN_FREQ){
                LARGEST_KNOWN_FREQ =thisFreq;
                winners.clear();
            }
            if (thisFreq == LARGEST_KNOWN_FREQ){
                winners.add (thisVar);
            }
        }
           
        return   (winners ) ;
    }
     
    
    private TreeSet<String>  getHighestFrequencyVars  (  TreeMap < String, Integer> frequencyMap  ){
        TreeSet<String>  highestFreqVars= new  TreeSet<String>  ();
        int HIGHEST_KNOWN_FREQUENCY = -ONE;
          
        for (Map.Entry < String, Integer> entry : frequencyMap.entrySet() ){
            String thisVar = entry.getKey();
            int thisFreq = entry.getValue();
              
            if (thisFreq > HIGHEST_KNOWN_FREQUENCY){
                HIGHEST_KNOWN_FREQUENCY= thisFreq;
                highestFreqVars.clear();
            }
            if (thisFreq == HIGHEST_KNOWN_FREQUENCY){
               highestFreqVars .add (thisVar );
            }
        }
          
        return highestFreqVars; 
    }
       
    private TreeSet<String>  getHighestFrequencyVars  ( 
            TreeMap < String, Integer> frequencyMapLeft,
            TreeMap < String, Integer> frequencyMapRight,
            TreeSet<String>    candidates ){
        
        TreeSet<String>  winners= new  TreeSet<String>  ();
        int HIGHEST_KNOWN_FREQUENCY_SMALLER = -ONE;
        int HIGHEST_KNOWN_FREQUENCY_BIGGER = -ONE;
          
        for (String thisVar : candidates ){
            Integer frequencyLeft  = frequencyMapLeft.get (thisVar);
            if (null==frequencyLeft)frequencyLeft= ZERO;
            Integer frequencyRight  = frequencyMapRight.get (thisVar);
            if (null==frequencyRight)frequencyRight= ZERO;
            
            int lowerFrequency = Math.min( frequencyLeft, frequencyRight );
            int higherFrequency = Math.max( frequencyLeft, frequencyRight );
            
            boolean cond1 = lowerFrequency > HIGHEST_KNOWN_FREQUENCY_SMALLER;
            boolean cond2 = HIGHEST_KNOWN_FREQUENCY_SMALLER == lowerFrequency &&
                   higherFrequency>  HIGHEST_KNOWN_FREQUENCY_BIGGER;
             
            if (cond1 || cond2){
                HIGHEST_KNOWN_FREQUENCY_SMALLER =lowerFrequency;
                HIGHEST_KNOWN_FREQUENCY_BIGGER = higherFrequency;
                winners.clear();
            }
            
            boolean cond3 = HIGHEST_KNOWN_FREQUENCY_SMALLER == lowerFrequency &&
                   higherFrequency==  HIGHEST_KNOWN_FREQUENCY_BIGGER;
            if (cond3){
                winners.add(thisVar);
            }
            
        }
          
        return winners;
    }
    
 
    private TreeSet<String>    getHighestObjMagn ( Set<String>    candidates  ){
       
        double LARGEST_KNOWN_OBJ_MAGN = - ONE;
        TreeSet<String>  winners = new TreeSet<String>();
        
        for (String thisVar : candidates){
            double thisObjMagn = Math.abs (this.objectiveFunctionMap.get(thisVar));
            if (thisObjMagn> LARGEST_KNOWN_OBJ_MAGN){
                LARGEST_KNOWN_OBJ_MAGN =thisObjMagn;
                winners.clear();
            }
            if (thisObjMagn == LARGEST_KNOWN_OBJ_MAGN){
                winners.add (thisVar);
            }
        }
           
        return   (winners ) ;
    }
       
    private boolean is_BUV_Feasible(LowerBoundConstraint lbc) {
        double lhsValue_at_BUV = ZERO; 
        
        for (Triplet triplet : lbc.coefficientList){
            if (triplet.constraintCoefficient > ZERO && triplet.isPrimary){
                
            }else if (triplet.constraintCoefficient > ZERO && !triplet.isPrimary){
                lhsValue_at_BUV += triplet.constraintCoefficient;
            } else if (triplet.constraintCoefficient < ZERO && !triplet.isPrimary){
                
            } else {
                lhsValue_at_BUV += triplet.constraintCoefficient;
            }
        }
        
        return lhsValue_at_BUV >= lbc.lowerBound;
    }
         
}

