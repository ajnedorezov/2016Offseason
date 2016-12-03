package missdaisy;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import missdaisy.loops.AutoAimDriveController;
//import missdaisy.loops.AutoAimShooterController;
import missdaisy.loops.DriveStraightController;
import missdaisy.loops.DriveTurnController;
import missdaisy.loops.Navigation;
//import missdaisy.loops.RevShooterController;
//import missdaisy.loops.ShooterSpeedController;
import missdaisy.subsystems.*;
import missdaisy.utilities.DaisyMath;
import missdaisy.utilities.XboxController;

public class OperatorInput {
	private static OperatorInput operatorInputInstance = null;
	protected XboxController mDriveController;
	protected XboxController mOperatorController;
	
	// subsystems
	private Drive mDrive;
//	private Shooter mShooter;
//	private Intake mIntake;
//	private Hanger mHanger;
	
	// controllers
	private AutoAimDriveController mAutoAimDrive;
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
//		mShooter = Shooter.getInstance();
//		mIntake = Intake.getInstance();
//		mHanger = Hanger.getInstance();
		mAutoAimDrive = AutoAimDriveController.getInstance();
//		mAutoAimShooter = AutoAimShooterController.getInstance();
//		mShooterSpeed = ShooterSpeedController.getInstance();
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
		/*
		mIsAutoAimControllers = (mDrive.getCurrentController() == mAutoAimDrive) &&
									(mShooter.getCurrentController() == mAutoAimShooter);
		mIsAutoAimOnTarget = mAutoAimDrive.onTarget() && mAutoAimShooter.onTarget();
		mReadyToShoot = mIsAutoAimControllers && mIsAutoAimOnTarget;
		mShooter.setStatusLightState(mReadyToShoot);	
		 */
		/**
		 * Driver Inputs:
		 * 1) Left and right sticks: moving robot
		 * 2) LT: Auto-Aim
		 * 3) RT: Hold for no output filtering for the drive 
		 * 		(disable smooth acceleration)
		 * 
		 */
		if (mDrive.getCurrentController() == null) {
			if (mDriveController.getRightTrigger())
				mDrive.useAlphaFilter(false);
			else 
				mDrive.useAlphaFilter(true);
			
			if (!mArcadeDrive)
				mDrive.setSpeed(mLeftMotorSpeed, mRightMotorSpeed);
			else 
				mDrive.setSpeedTurn(mSpeed, mTurn);
		} else {
			mDrive.useAlphaFilter(false);
		}
		
		/**
		 * Operator Inputs:
		 * 1) RT: Shoot (if the auto-aim is on target)
		 * 2) LT: Rev shooter (based on hood position)
		 * 3) RB: Hood down (outerworks position)
		 * 4) LB: Hood up (batter position)
		 * 5) Right Stick: Intake in and out
		 * 6) Left Stick: Hanger Winch
		 * 7) A-Button: Intake to the floor (deploy)
		 * 8) B-Button: Intake up (retract)
		 * 9) X-Button: Extent hanger arms (deploy)
		 * 10) Y-Button: Retract hanger arms (retract)
		 */
		/*
		if (!mIntake.seesBall()) {
			mIntake.setIntakeSpeed(mIntakeSpeed);
			mIntake.setConveyorSpeed(mIntakeSpeed);
		} else if (mIntake.seesBall() && mReadyToShoot) {
			if (mOperatorController.getRightTrigger()) {
				mIntake.setIntakeSpeed(1.0);
				mIntake.setConveyorSpeed(1.0);
			}
		} else {
			mIntake.setIntakeSpeed(0.0);
			mIntake.setConveyorSpeed(0.0);
		}
		
		// intake piston
		if (mOperatorController.getAButton())
			mIntake.deploy();
		else if (mOperatorController.getBButton())
			mIntake.retract();
		
		// shooter hood
		if (mOperatorController.getLB())
			mShooter.setBatterPosition();
		else if (mOperatorController.getRB())
			mShooter.setOuterworksPosition();
		
		// hanger piston
		if (mOperatorController.getXButton())
			mHanger.deploy();
		else if (mOperatorController.getYButton())
			mHanger.retract();
		
		// this is where all controller logic must go
		// if none of the statements are satisfied, then subsystems
		// will be an open loop
		if (mDriveController.getLeftTrigger()) { // auto aim
			mDrive.setCurrentController(mAutoAimDrive);
			mShooter.setCurrentController(mAutoAimShooter);
			mShooter.setVisionLightState(true);
		} else if (mOperatorController.getLeftTrigger()) {
			mShooter.setOpenLoop();
			if (mShooter.isHoodOuterworksPosition()) {
				mShooterSpeed.setGoal(Constants.kDefaultShooterRPMOuterWorks);
			} else if (mShooter.isHoodBatterPosition()) {
				mShooterSpeed.setGoal(Constants.kDefaultShooterRPMBatter);
			}
			mShooter.setCurrentController(mShooterSpeed);
		} else {
			mDrive.setOpenLoop(); 
			mShooter.setOpenLoop();
			mShooter.setSpeed(0.0);
			mShooter.setVisionLightState(false);
		}
		
		shootorRPMBlah = SmartDashboard.getNumber("shootermotor", 0.0);
		*/
	}
	
	public void logToDashboard() {
		SmartDashboard.putBoolean("ReadyToShoot", mReadyToShoot);
		SmartDashboard.putBoolean("AutoAimOnTarget", mIsAutoAimOnTarget);
		
	}
}

/*
if (!mIntake.seesBall()) {
	mIntake.setIntakeSpeed(mIntakeSpeed);
	mIntake.setConveyorSpeed(mIntakeSpeed);
} else if (mIntake.seesBall() && mOperatorController.getRightTrigger()) {
	mIntake.setIntakeSpeed(1.0);
	mIntake.setConveyorSpeed(1.0);
} else {
	mIntake.setIntakeSpeed(0.0);
	mIntake.setConveyorSpeed(0.0);
} */


/*
if (mOperatorController.getRightTrigger()){ // temp speed control for shooter
	ShooterSpeedController.getInstance().setGoal(shootorRPMBlah);
	mShooter.setCurrentController(ShooterSpeedController.getInstance());
} else {
	mShooter.setOpenLoop();
	mShooter.reset();
} */

/* Useful for telling the turn controller a certain angle to turn to
if (mDriveController.getRB())
	mDrive.setCurrentController(DriveTurnController.getInstance());
else {
	DriveTurnController.getInstance().reset();
	DriveTurnController.getInstance().setGoal(
			DaisyMath.boundAngle0to360Degrees(
					Navigation.getInstance().getHeadingInDegrees() + 90));
	mDrive.setOpenLoop();
} */
