package missdaisy;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import missdaisy.loops.DriveDistanceController;
import missdaisy.loops.DriveStraightController;
import missdaisy.loops.DriveTurnController;
import missdaisy.loops.Navigation;
import missdaisy.subsystems.*;
import missdaisy.utilities.DaisyMath;
import missdaisy.utilities.Trajectory;
import missdaisy.utilities.XboxController;

public class OperatorInput {
	private static OperatorInput operatorInputInstance = null;
	protected XboxController mDriveController;
	protected XboxController mOperatorController;
	
	protected DriveDistanceController mDriveDistance = new DriveDistanceController();
	
	// subsystems
	private Drive mDrive;
	
	// controllers
//	private AutoAimShooterController mAutoAimShooter;
//	private ShooterSpeedController mShooterSpeed;
	
	private double mLeftMotorSpeed;
	private double mRightMotorSpeed;
	private double mSpeed;
	private double mTurn;
	private double mIntakeSpeed;
	
	boolean mIsAutoAimControllers = false;
	boolean mIsAutoAimOnTarget = false;
	boolean mReadyToShoot = false;
	boolean mResetHeading = true;
	
	private boolean mArcadeDrive = true;
	private double shootorRPMBlah;
	
	public static OperatorInput getInstance() {
		if (operatorInputInstance == null)
			operatorInputInstance = new OperatorInput();
		return operatorInputInstance;
	}
	
	private OperatorInput() {
		mDriveController = new XboxController(Constants.driveControllerPort);
		mOperatorController = new XboxController(Constants.operatorControllerPort);
		mDrive = Drive.getInstance();
		SmartDashboard.putNumber("shootermotor", 0.0);
	}
	
	public void processInputs() {
		// Driver Inputs
		mLeftMotorSpeed = -1.0 * DaisyMath.applyDeadband(mDriveController.getLeftYAxis(), Constants.deadBand);
		mRightMotorSpeed = -1.0 * DaisyMath.applyDeadband(mDriveController.getRightYAxis(), Constants.deadBand);
		mSpeed = mLeftMotorSpeed;
		mTurn = DaisyMath.applyDeadband(mDriveController.getRightXAxis(), Constants.deadBand);
		// Operator Inputs
		mIntakeSpeed = DaisyMath.applyDeadband(mOperatorController.getRightYAxis(), Constants.deadBand);
		
		//Checks if the auto aim is on target
		// as long as the current controllers are the auto aim ones,
		// and they are on target, set mReadyToShoot to true
		
		/**
		 * Driver Inputs:
		 * 1) Left and right sticks: moving robot
		 * 2) LT: Auto-Aim
		 * 3) RT: Hold for no output filtering for the drive 
		 * 		(disable smooth acceleration)
		 * 
		 */
		if (mDrive.getCurrentController() == null) {
			if (mDriveController.getRightTrigger()){
				mDrive.useAlphaFilter(false);
			}else{ 
				mDrive.useAlphaFilter(true);
			}
			
			if (mDriveController.getAButton()){
				Trajectory mTrajectory = new Trajectory();
				mTrajectory.generate(mDrive.getAverageDistance()+500, Constants.driveMaximumVelocity, Constants.driveMaximumAcceleration, Constants.driveMaximumJerk, Constants.defaultTrajectoryTimeStep);
				
				mDriveDistance.loadProfile(mTrajectory, 1.0, mDrive.getGyroAngle());
				mDrive.setCurrentController(mDriveDistance);
			}else if (mDriveController.getYButton()){
				Trajectory mTrajectory = new Trajectory();
				mTrajectory.generate(mDrive.getAverageDistance()-200, Constants.driveMaximumVelocity, Constants.driveMaximumAcceleration, Constants.driveMaximumJerk, Constants.defaultTrajectoryTimeStep);
				
				mDriveDistance.loadProfile(mTrajectory, -1.0, mDrive.getGyroAngle());
				mDrive.setCurrentController(mDriveDistance);
				
			}else if (!mArcadeDrive){
				mDrive.setSpeed(mLeftMotorSpeed, mRightMotorSpeed);
			}else{ 
				mDrive.setSpeedTurn(mSpeed, mTurn);
			}
		} else {
			mDrive.useAlphaFilter(false);
			if(mDriveDistance.onTarget() || mDriveController.getBButton()){
				mDrive.setCurrentController(null);
			}
		}
	
	}
	
	public void logToDashboard() {
		SmartDashboard.putBoolean("ReadyToShoot", mReadyToShoot);
		SmartDashboard.putBoolean("AutoAimOnTarget", mIsAutoAimOnTarget);
		SmartDashboard.putString("DriveController", mDrive.getCurrentCommand().toString());
		
	}
}