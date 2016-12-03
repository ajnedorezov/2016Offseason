package missdaisy.loops;

import missdaisy.Constants;
import missdaisy.fileio.PropertySet;
import missdaisy.subsystems.Drive;

/**
 * Uses PID calculations to drive a certain distance in a straight line. 
 * 
 * @author Josh Sizer
 */
// The class's inherited PID is the PID that controls distance.
public class DriveDistanceController extends SynchronousPID implements Controller {
	private static DriveDistanceController driveDistanceControllerInstance = null;
	private Drive mDrive;
	private Navigation mNavigation;
	private SynchronousPID mTurnPID; // This makes sure we drive in a straight line
	private double mMaxOutput = 1.0; // the maximum output to the drive motors
	private double mGoalDistance = 0.0;
	private double mGoalAngle = 0.0;
	private double mDistanceTraveled = 0.0;
	private double mCurrentAngle;
	private double mEncoderStartingDistance;
	
	private double mDistanceTolerance;
	private double mAngleTolerance;
		
	private boolean mDistanceOnTarget = false;
	private boolean mAngleOnTarget = false;

	/**
	 * Gets the instance of the drive straight controller.
	 * Used in order to never have more than one drive straight controller object, ever.
	 * 
	 * @return The one and only instance of the drive straight controller
	 */
	public static DriveDistanceController getInstance() {
		if (driveDistanceControllerInstance == null)
			driveDistanceControllerInstance = new DriveDistanceController();
		return driveDistanceControllerInstance;
	}
	
	private DriveDistanceController() {
		mTurnPID = new SynchronousPID();
		mDrive = Drive.getInstance();
		mNavigation = Navigation.getInstance();
		loadProperties();
	}
	
	/**
	 * Set the desired distance to travel and the maximum output of the PID, a number between
	 * 0.0 and 1.0. The value between 0.0 and 1.0 indicates speeds between full stop and full power
	 * The distance to travel should be positive or negative, the max power should be only positive
	 * 
	 * @param distance The distance to travel, either backwards or forwards (negative or positive)
	 * @param maxoutput The maximum output for the drive motors
	 */
	public void setGoal(double distance, double speed) {
		mGoalDistance = distance;
		// we'll assume that the angle you wish to drive at is the same
		// as the one when you set the goal
		mGoalAngle = mNavigation.getHeadingInDegrees(); 
		// you can't know how far you've traveled unless you know where you started
		mEncoderStartingDistance = mNavigation.getAverageEncoderDistance();
		
		// makes sure that the maxVelocity is a positive number and not greater than 1.0
		if (speed < 0.0)
			speed *= -1.0;
		if (speed > 1.0)
			speed = 1.0;
		mMaxOutput = speed;
		super.setOutputRange(-mMaxOutput, mMaxOutput);
		mTurnPID.setOutputRange(-mMaxOutput, mMaxOutput);
		
		// we want the difference between where we want to be and where we are to be 0
		super.setSetpoint(0.0);
		// same with the angle
		mTurnPID.setSetpoint(0.0);
		
	}
	
	/**
	 * Attempts to drive the robot a certain distance in a straight line
	 */
	@Override
	public void run() {
		// what angle you're at right now
		mCurrentAngle = mNavigation.getHeadingInDegrees(); 
		// how far you've traveled since you've set your goal
		mDistanceTraveled = mNavigation.getAverageEncoderDistance() - mEncoderStartingDistance; 
		
		double speed;
		double turn;
		
		if (!mDistanceOnTarget && !mAngleOnTarget) { //if both angle and distance are not on target
			speed = super.calculate(mDistanceTraveled - mGoalDistance);
			turn = mTurnPID.calculate(mCurrentAngle - mGoalAngle);
			mDrive.setSpeedTurn(speed, turn);
		} else if (!mDistanceOnTarget) { // or if just distance is not on target
			speed = super.calculate(mDistanceTraveled - mGoalDistance);
			mDrive.setSpeedTurn(speed, 0.0);
		} else if (!mAngleOnTarget) { // or if just angle is not on target
			turn = mTurnPID.calculate(mCurrentAngle - mGoalAngle);
			mDrive.setSpeedTurn(0.0, turn);
		} else {
			mDrive.setSpeedTurn(0.0, 0.0);
		}
		
		// checks to see if we are on target
		mDistanceOnTarget = super.onTarget(mDistanceTolerance);
		mAngleOnTarget = mTurnPID.onTarget(mAngleTolerance);
	}
	
	/**
	 * Resets all internal variables
	 */
	@Override
	public void reset() {
		super.reset();
		mTurnPID.reset();
		mMaxOutput = 1.0;
		mGoalDistance = 0.0;
		mGoalAngle = 0.0;
		mDistanceTraveled = 0.0;
		mCurrentAngle = mNavigation.getHeadingInDegrees();
		mEncoderStartingDistance = mNavigation.getAverageEncoderDistance();
	}

	/**
	 * Returns true if the robot has driven the desired distance and its heading
	 * is the same as it started
	 */
	@Override
	public boolean onTarget() {
		return (mDistanceOnTarget && mAngleOnTarget);
	}
	
	/**
	 * Loads the PID multipliers and the angle and distance tolerances
	 */
	@Override
	public void loadProperties() {
		PropertySet mPropertySet = PropertySet.getInstance();
		double kp = mPropertySet.getDoubleValue("distanceKp", 0.001);
		double ki = mPropertySet.getDoubleValue("distanceKi", 0.0);
		double kd = mPropertySet.getDoubleValue("distanceKd", 0.001);
		super.setPID(kp, ki, kd);	
		kp = mPropertySet.getDoubleValue("angleKp", 0.001);
		ki = mPropertySet.getDoubleValue("angleKi", 0.0);
		kd = mPropertySet.getDoubleValue("angleKd", 0.001);
		mTurnPID.setPID(kp, ki, kd);
		mDistanceTolerance = mPropertySet.getDoubleValue("distanceTolerance", Constants.kDefaultDistanceTolerance);
		mAngleTolerance = mPropertySet.getDoubleValue("angleTolerance", Constants.kDefaultAngleTolerance);
	}
	
	@Override
	public String toString() {
		return "DriveDistanceController";
	}
}
