package missdaisy.subsystems;

import missdaisy.Constants;
import missdaisy.fileio.PropertySet;
import missdaisy.loops.Navigation;
import missdaisy.utilities.AlphaFilter;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The drive base of the robot. Has methods to set the speed of the
 * left and right motors. 
 * @author Josh Sizer
 */
public class Drive extends DaisySubsystem {
	private static Drive driveInstance = null;
	private Talon mLeftDriveMotor;
	private Talon mRightDriveMotor;
	private Navigation mNavigation;
	private AlphaFilter mAlphaFilter;
	private boolean mResetHeading = true;
	private boolean mUseAlphaFilter = true;
	private double mDesiredHeading = 0.0;
	private double kP;
	/**
	 * Gets the instance of the drive base.
	 * Used in order to never have more than one drive base object, ever.
	 * 
	 * @return The one and only instance of the drive base
	 */
	public static Drive getInstance() {
		if (driveInstance == null)
			driveInstance = new Drive();
		return driveInstance;
	}
	
	private Drive() {
		mLeftDriveMotor = new Talon(Constants.leftDriveMotorPWM);
		mRightDriveMotor = new Talon(Constants.rightDriveMotorPWM);
		//either the left or the right drive will be backwards
		mRightDriveMotor.setInverted(true);
		mNavigation = Navigation.getInstance();
		mAlphaFilter = new AlphaFilter(Constants.kDriveAlpha);
		loadProperties();
	}
	
	/**
	 * Set the speed of each motor individually. Useful for tank drive or any other case
	 * that each motor must be set precisely and separate from each other
	 * 
	 * @param leftMotorSpeed a double between -1.0 and 1.0, representing full forward or full reverse
	 * for the left motor.
	 * @param rightMotorSpeed a double between -1.0 and 1.0, representing full forward or full reverse
	 * for the right motor.
	 */
	public void setSpeed(double leftMotorSpeed, double rightMotorSpeed) {
		set(leftMotorSpeed, rightMotorSpeed);
	}
	
	/**
	 * Set the speed and the turn of the robot, rather than each motor individually. Useful for 
	 * arcade drive.
	 * 
	 * @param speed a double between -1.0 and 1.0, representing the full forward or full reverse.
	 * @param turn a double between -1.0 and 1.0, representing turn anti-clockwise or clockwise.
	 */
	public void setSpeedTurn(double speed, double turn) {
		// If the turn is zero (ie if the robot should go straight
		// then we calculate the correct turn value to counter act
		// the robot's inevitable drift over time. 
		// Basically the P term in PID
		
		if (speed != 0.0 && turn < Constants.deadBand) {
			if (mResetHeading){
				mDesiredHeading = mNavigation.getHeadingInDegrees(); 
				mResetHeading = false;
			}
			turn = kP * (mDesiredHeading - mNavigation.getHeadingInDegrees());
			if (mUseAlphaFilter)
				speed = mAlphaFilter.run(speed);
				SmartDashboard.putNumber("AlphaOutput", speed);
		} else {
			mResetHeading = true;
		}
				
		double leftMotorSpeed = speed + turn;
		double rightMotorSpeed = speed - turn;
		set(leftMotorSpeed, rightMotorSpeed);
	}
	
	public void useAlphaFilter(boolean use) {
		mUseAlphaFilter = use;
	}
	
	/** 
	 * This is to simplify other methods that set the motor speed.
	 * The arguments can only be values from -1.0 to 1.0, a scaled number representing
	 * the output voltage of the speed controller (-12v to 12v).
	 */
	private void set(double leftMotorSpeed, double rightMotorSpeed) {
		mLeftDriveMotor.set(leftMotorSpeed);
		mRightDriveMotor.set(rightMotorSpeed);
	}

    public synchronized void reset() {
    	set(0.0, 0.0);
    }
    
    public void loadProperties() {
    	PropertySet mPropertySet = PropertySet.getInstance();
    	kP = mPropertySet.getDoubleValue("driveStraightKp", 0.025);
    }
    
    public void logToDashBoard() {
    	SmartDashboard.putNumber("DriveLeftMotorOutput", mLeftDriveMotor.get());
    	SmartDashboard.putNumber("DriveRightMotorOutput", mRightDriveMotor.get());
    	if (getCurrentController() != null)
    		SmartDashboard.putString("DriveCurrentController", getCurrentController().toString());
    	else
    		SmartDashboard.putString("DriveCurrentController", "OpenLoop");
    }
}
