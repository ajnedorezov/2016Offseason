package missdaisy.autonomous;

/**
 * State machine to execute an autonomous mode.
 * 
 * @author Jared341
 */
public class StateMachine
{
    State[] mStates;
    int mCurrentState;
    boolean mStarted;

    public StateMachine(State[] aStates)
    {
        mStates = aStates;
        mCurrentState = 0;
        mStarted = false;
        //SmartDashboard.log("Not Started", "Autonomous Mode State");
    }
    
    public String getCurrentState()
    {
        if( mCurrentState < mStates.length )
        {
            return mStates[mCurrentState].toString();
        }
        else
        {
            return "Not Active";
        }
    }

    public void run()
    {
        if( mCurrentState < mStates.length )
        {
            if( !mStarted )
            {
                mStates[mCurrentState].enter();
                mStarted = true;
                System.out.println("Entering state: " + mStates[mCurrentState]);
                //SmartDashboard.log(mStates[mCurrentState].toString(), "Autonomous Mode State");
            }
            else if( mStates[mCurrentState].isDone() )
            {
                mStates[mCurrentState].exit();
                System.out.println("Exiting state: " + mStates[mCurrentState]);
                mCurrentState++;
                if( mCurrentState < mStates.length )
                {
                    mStates[mCurrentState].enter();
                    System.out.println("Entering state: " + mStates[mCurrentState]);
                    //SmartDashboard.log(mStates[mCurrentState].toString(), "Autonomous Mode State");
                }
                else
                {
                    //SmartDashboard.log("Finished", "Autonomous Mode State");
                    System.out.println("Finished state machine.");
                }
            }
            else
            {
                mStates[mCurrentState].running();
            }
        }
    }
}
