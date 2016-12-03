package missdaisy.loops;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import missdaisy.Constants;
import missdaisy.fileio.PropertySet;
import missdaisy.subsystems.Drive;
import missdaisy.utilities.DaisyMath;

/**
 * Turns the robot in a closed loop by using PID calculations to calculate
 * desired output to the drive base motors.
 * 
 * @author Josh Sizer
 */
public class DriveTurnController extends SynchronousPID implements Controller {
	private static DriveTurnController driveTurnControllerInstance = null;
	private Drive mDrive;
	private Navigation mNavigation;
	
	// the allowed angle difference between the goal and actual robot angle
	private double angleTolerance;
	private double currentAngle; // the angle the robot is at now
	private double turn; // the calculated value from the PID 
	private double mGoal; // the angle we want to turn to
	private double mMaxMotorOutput = 0.3;
	private boolean mPIDFromSmartDashboard = false;
	
	/**
	 * Gets the instance of the drive turn controller.
	 * Used in order to never have more than one drive turn controller object, ever.
	 * 
	 * @return The one and only instance of the drive turn controller
	 */
	public static DriveTurnController getInstance() {
		if (driveTurnControllerInstance == null)
			driveTurnControllerInstance = new DriveTurnController();
		return driveTurnControllerInstance;
	}
	
	private DriveTurnController() {
		mDrive = Drive.getInstance();
		mNavigation = Navigation.getInstance();
		loadProperties();
		// the angle of the robot is continuous
		setContinuous(true);
		super.setInputRange(0, 360);
		super.setOutputRange(-mMaxMotorOutput, mMaxMotorOutput);
		SmartDashboard.putNumber("TurnkP", 0.0);
		SmartDashboard.putNumber("TurnkI", 0.0);
		SmartDashboard.putNumber("TurnkD", 0.0);
		SmartDashboard.putNumber("AngleToTurn", 0.0);
	}
	
	/**
	 * Set the angle for the robot to turn to
	 * 
	 * @param angle the desired angle to turn to
	 */
	public void setGoal(double angle) {
		mGoal = DaisyMath.boundAngle0to360Degrees(angle);
		// the angle you want to be at will be considered 0.0.
		// the difference between where you are and where you want to be
		// is calculated, and you want that difference to be 0.0.
		setSetpoint(0.0);
	}
	
	/**
	 * Turns the robot to the desired angle specified by setGoal
	 */
	@Override
	public void run() {
		loadProperties();
		if (!onTarget()) {
			//finds where you are now.
			currentAngle = mNavigation.getHeadingInDegrees();
			// calculate the output for drive motors, based on the difference between 
			// where we are and where we want to be
			turn = super.calculate(DaisyMath.boundAngle0to360Degrees(currentAngle - mGoal));
			SmartDashboard.putNumber("TurnCommand", turn);
			SmartDashboard.putNumber("Error", super.getError());
			SmartDashboard.putNumber("TurnControllerGoal", mGoal);
			mDrive.setSpeedTurn(0.0, turn);
		} else {
			mDrive.setSpeed(0.0, 0.0);
		}
	}

	/**
	 * Resets the turn controller to have the goal be the angle the robot is currently at
	 */
    public synchronized void reset() {
        super.reset(); // resets the PID terms
        // the robots goal is where it is now if reset
        currentAngle = mNavigation.getHeadingInDegrees();
        setGoal(currentAngle);
    }
    
    /**
     * Returns true if the robot is at the goal angle, false if it is not
     */
	@Override
	public boolean onTarget() {
		// part of the PID class, if the robot is within a certain range of the target,
		// it is considered to be on the target.
		return super.onTarget(Constants.kDefaultAngleTolerance);
	}

	@Override
	public void loadProperties() {
		if (!mPIDFromSmartDashboard) {
			PropertySet mPropertySet = PropertySet.getInstance();
			double kp = mPropertySet.getDoubleValue("angleKp", 0.05);
			double ki = mPropertySet.getDoubleValue("angleTurnKi", 0.004);
			double kd = mPropertySet.getDoubleValue("angleTurnKd", 0.0);
			angleTolerance = mPropertySet.getDoubleValue("angleTolerance", Constants.kDefaultAngleTolerance);
			mMaxMotorOutput = mPropertySet.getDoubleValue("turnPIDMaxMotorOutput", Constants.kDefaultTurnPIDMaxMotorOutput);
			setPID(kp, ki, kd);	
		} else {
			double kp = SmartDashboard.getNumber("TurnkP", 0.1);
			double ki = SmartDashboard.getNumber("TurnkI", 0.0);
			double kd = SmartDashboard.getNumber("TurnkD", 0.0);
			setPID(kp, ki, kd);	
		}	
	}
	
	@Override
	public String toString() {
		return "DriveTurnController";
	}
	
	public void logToDashboard() {
		SmartDashboard.putNumber("TurnkP", getP());
		SmartDashboard.putNumber("TurnkI", getI());
		SmartDashboard.putNumber("TurnkD", getD());
	}
}
