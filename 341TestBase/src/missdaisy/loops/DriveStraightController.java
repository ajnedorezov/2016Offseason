package missdaisy.loops;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import missdaisy.Constants;
import missdaisy.fileio.PropertySet;
import missdaisy.subsystems.Drive;

/**
 * Uses PID calculations to drive in a straight line. 
 * 
 * @author Josh Sizer
 */
// The class's inherited PID is the PID that controls distance.
public class DriveStraightController extends SynchronousPID implements Controller {
	private static DriveStraightController driveStraightControllerInstance = null;
	private Drive mDrive;
	private Navigation mNavigation;
	private double mSpeed = 1.0; // the maximum output to the drive motors to drive straight
	private double mGoalAngle = 0.0;
	private double mCurrentAngle;
	private double mAngleTolerance;
		
	/**
	 * Gets the instance of the drive straight controller.
	 * Used in order to never have more than one drive straight controller object, ever.
	 * 
	 * @return The one and only instance of the drive straight controller
	 */
	public static DriveStraightController getInstance() {
		if (driveStraightControllerInstance == null)
			driveStraightControllerInstance = new DriveStraightController();
		return driveStraightControllerInstance;
	}
	
	private DriveStraightController() {
		mNavigation = Navigation.getInstance();
		loadProperties();
	}
	
	/**
	 * Sets the angle to drive straight at based on what angle you're at now.
	 */
	public void setGoal(double speed) {
		// we'll assume that the angle you wish to drive at is the same
		// as the one when you set the goal
		mGoalAngle = mNavigation.getHeadingInDegrees(); 
		mSpeed = speed;
		// we want the difference between where we want to be and where we are to be 0
		// same with the angle
		super.setSetpoint(0.0);
	}
	
	/**
	 * Attempts to drive the robot in a straight line, at the speed dictated by
	 * <code> setSpeed </code>.
	 */
	@Override
	public void run() {
		// what angle you're at right now
		mCurrentAngle = mNavigation.getHeadingInDegrees(); 		
		double turn;
		
		if (!onTarget()) { //if just angle is not on target
			turn = super.calculate(mCurrentAngle - mGoalAngle);
			mDrive.setSpeedTurn(mSpeed, turn);
		} else // else just drive straight
			mDrive.setSpeedTurn(mSpeed, 0.0);	
	}
	
	/**
	 * Resets PID summation.
	 * Sets the desired speed to drive at to be 0.0.
	 * Sets the goal angle to be 0.0.
	 * Sets the current angle to the current angle of the robot
	 */
	@Override
	public void reset() {
		super.reset();
		mSpeed = 0.0;
		mGoalAngle = 0.0;
		mCurrentAngle = mNavigation.getHeadingInDegrees();
	}

	/**
	 * Returns true if the robot is driving straight.
	 */
	@Override
	public boolean onTarget() {
		return super.onTarget(mAngleTolerance);
	}
	
	/**
	 * Loads the PID multipliers and the angle tolerance.
	 */
	@Override
	public void loadProperties() {
		PropertySet mPropertySet = PropertySet.getInstance();
		double kp = mPropertySet.getDoubleValue("angleKp", 0.05);
		double ki = mPropertySet.getDoubleValue("angleKi", 0.0);
		double kd = mPropertySet.getDoubleValue("angleKd", 0.0001);
		double maxTurnOutput = mPropertySet.getDoubleValue("turnPIDMaxMotorOutput", Constants.kDefaultTurnPIDMaxMotorOutput);
		mAngleTolerance = mPropertySet.getDoubleValue("angleTolerance", Constants.kDefaultAngleTolerance);
		super.setPID(kp, ki, kd);
		super.setOutputRange(-maxTurnOutput, maxTurnOutput);
	}
	
	/**
	 * Returns <i> DriveStraightController </i>.
	 */
	@Override
	public String toString() {
		return "DriveStraightController";
	}
}
