package ca.mcmaster.saipopsv102.drivers;
   
import static ca.mcmaster.saipopsv102.Constants.*;
import static ca.mcmaster.saipopsv102.Parameters.*;
import static java.lang.System.exit;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author tamvadss
 */
public class SaiPOPS_Driver {
    
    public  static Logger logger;
     
    static   {
        logger=Logger.getLogger(SaiPOPS_Driver.class);
        logger.setLevel(LOGGING_LEVEL);
        PatternLayout layout = new PatternLayout("%5p  %d  %F  %L  %m%n");     
        try {
            RollingFileAppender rfa =new  
                RollingFileAppender(layout,LOG_FOLDER+SaiPOPS_Driver.class.getSimpleName()+ LOG_FILE_EXTENSION);
            rfa.setMaxBackupIndex(SIXTY);
            logger.addAppender(rfa);
            logger.setAdditivity(false);            
             
        } catch (Exception ex) {
            ///
            System.err.println("Exit: unable to initialize logging"+ex);       
            exit(ONE);
        }
    }
    
      
    
    public static void main(String[] args) throws Exception{
        
        logger.info ("Start Cplex with SAI POPS heuristic version 1.02 ..." );
        
        logger.info ("MIP_EMPHASIS is " +MIP_EMPHASIS  );
        
        Solver solver = new Solver (logger) ;
         
        solver.solve ( );
        
    }//end main
     
   
    
}
