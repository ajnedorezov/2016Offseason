package missdaisy.autonomous;

import missdaisy.fileio.*;
import java.lang.Class;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Parses autonomous modes from an auto mode text file.
 * 
 * Uses reflection to create instances of each auto state based on its name
 * If it cannot be instantiated for various reasons, WaitForTime(0) is instantiated
 * 
 * @author JSizer341
 */
public class AutonomousParser
{
    private final PropertySet mProperties;
    private Class<?> currentState;
    private Constructor<?> mStateConstructor;
    private Type[] mParamTypes;
    int mNumberOfParameters;
    
    public AutonomousParser()
    {
        mProperties = PropertySet.getInstance();        
    }

    public State[] parseStates()
    {
        State[] lStates;
        int lNumStates = mProperties.getIntValue("AutonomousNumStates", -1);
        System.out.println("Found " + Integer.toString(lNumStates) + " auto states");
        if( lNumStates < 1 )
        {
            lStates = new State[1];
            lStates[0] = new WaitForTime(0);
        }
        else
        {
            lStates = new State[lNumStates];
            for( int i = 0; i < lNumStates; i++ )
            {
                String lStateName = mProperties.getStringValue("AutonomousState" + Integer.toString(i+1), "");
            	try 
            	{
					currentState = Class.forName("missdaisy.autonomous." + lStateName);
					//there should only be one constructor
					mStateConstructor = currentState.getConstructors()[0];
					mNumberOfParameters = mStateConstructor.getParameterCount();
					
					Object mParamValues[] = new Object[mNumberOfParameters];
					
					mParamTypes = mStateConstructor.getParameterTypes();
					
					//fills parameter array with values
                	for (int k = 0; k < mStateConstructor.getParameters().length; k++)
                	{
                		//returns 0.0 or 0 if property cannot be found
                		if (mParamTypes[k] == double.class)
                		{
                			mParamValues[k] = mProperties.getDoubleValue("AutonomousState" + Integer.toString(i+1) + 
                														"Param" + Integer.toString(k+1), 0.0);
                		}
                		else if (mParamTypes[k] == int.class)
                		{
                		//truncates value from autonomous file to be an integer (if a number is entered as a double, 
                		//but is meant to be an integer)
                			mParamValues[k] = (int)mProperties.getDoubleValue("AutonomousState" + Integer.toString(i+1) + 
									"Param" + Integer.toString(k+1), 0.0);;
                		}
                	}
                	
                	try 
                	{
						lStates[i] = (State)mStateConstructor.newInstance(mParamValues);
						System.out.println("Instantiated " + lStates[i].toString() +
												" with parameter(s): " + formatParam(mParamValues));
					} 
                	catch (InstantiationException | IllegalAccessException
							| InvocationTargetException | IllegalArgumentException e) 
                	{
						System.out.println("Error instantiating " + "AutonomousState" + Integer.toString(i+1) + 
											": " + lStateName + ". WaitForTime instantiated instead.");
					}
                	
				} 
            	catch (ClassNotFoundException | SecurityException e) 
            	{
					System.out.println("Could not find specified state: " + lStateName +
							". WaitForTime (0) instantiated instead.");
				}
            	
            	if (lStates[i] == null)
            	{
            		lStates[i] = new WaitForTime(0);
            	}
            }
        }
        return lStates;
    }

	private String formatParam(Object[] paramValues)
	{
		String lFormattedString = "";
		for (int k = 0; k < paramValues.length; k++)
		{
			lFormattedString += paramValues[k].toString();
			if (k == paramValues.length - 1)
				break;

			lFormattedString += ", ";
		}
		return lFormattedString;
	}
}