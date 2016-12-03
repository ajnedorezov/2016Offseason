
package missdaisy;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import missdaisy.autonomous.AutonomousParser;
import missdaisy.autonomous.State;
import missdaisy.autonomous.StateMachine;
import missdaisy.fileio.PropertyReader;
import missdaisy.loops.DriveDistanceController;
import missdaisy.loops.DriveTurnController;
import missdaisy.loops.FastLoopTimer;
import missdaisy.loops.Navigation;
import missdaisy.subsystems.Drive;
/*import missdaisy.loops.ShooterSpeedController;
import missdaisy.subsystems.Drive;
import missdaisy.subsystems.Hanger;
import missdaisy.subsystems.Intake;
import missdaisy.subsystems.Shooter;
*/
/**
 * The robot.
 * 
 * @author Josh Sizer
 */
public class Daisy2016 extends IterativeRobot {
	private PropertyReader mPropertyReader;
	private FastLoopTimer mFastLoopTimer;
	private OperatorInput mOperatorInput;
	private StateMachine mStateMachine;
	
	private static final String kPropertiesFilePath = "/home/lvuser/properties/properties.txt"; //TODO: set properties path to correct properties path
	private static final String kAutonomousFilePath = "/home/lvuser/"; //TODO: set autonomous file path
	
	boolean mLastAButtonState = false;
	int mAutoMode = 1;
	int mNumAutoModes = 2;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	mPropertyReader = new PropertyReader();
    	mPropertyReader.parseFile(kPropertiesFilePath);
    	loadAllProperties();
    	
    	mOperatorInput = OperatorInput.getInstance();
    	mFastLoopTimer = FastLoopTimer.getInstance();
    	mFastLoopTimer.start(); // starts the fast loop timer executing input & output filters and controllers
    	//Compressor compressor = new Compressor(0);
    	//compressor.clearAllPCMStickyFaults();
    }

    public void disabledPeriodic() {    
    	//Allows you to toggle between autonomous modes
    	if (mOperatorInput.mDriveController.getAButton() && !mLastAButtonState) {
    		mAutoMode++;
    		if (mAutoMode > mNumAutoModes) {
    			mAutoMode = 1;
    		}
    		chooseAutoMode(mAutoMode);
    	}
		mLastAButtonState = mOperatorInput.mDriveController.getAButton();		
    }
    
    /**
     * This function is called periodically during autonomous
     */
    public void autonomousInit() {
    	AutonomousParser autonparser = new AutonomousParser();
    	State[] states = autonparser.parseStates();
    	SmartDashboard.putString("State1", states[0].toString());
    	mStateMachine = new StateMachine(states);
    	logToDashboard();
    }
    
    public void autonomousPeriodic() {
    	mStateMachine.run();
    	logToDashboard();
    }

    public void teleopInit() {
    	
    	logToDashboard();
    }
    
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        mOperatorInput.processInputs();
        logToDashboard();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
    public void chooseAutoMode(int autoModeNum) {
    	switch (autoModeNum) {
    		case 1: 
    			 mPropertyReader.parseAutonomousFile(kAutonomousFilePath + "DriveForward.txt");
                 SmartDashboard.putString("AutonomousMode", "DriveForward");
                 break;
    		case 2:
    			 mPropertyReader.parseAutonomousFile(kAutonomousFilePath + "DriveTurn.txt");
                 SmartDashboard.putString("AutonomousMode", "YourAutoModeHere2");
                 break;
            default:
            	mPropertyReader.parseAutonomousFile(kAutonomousFilePath + "DefaultAutoMode");
                SmartDashboard.putString("AutonomousMode", "DefaultAutoMode");
                break;
    	}
    }
    
    public void logToDashboard() {
    	Navigation.getInstance().logToDashboard();
    	Drive.getInstance().logToDashBoard();
    }
    
    public void loadAllProperties() {
    	DriveTurnController.getInstance().loadProperties();
 //   	ShooterSpeedController.getInstance().loadProperties();
    	DriveDistanceController.getInstance().loadProperties();
    }
}
