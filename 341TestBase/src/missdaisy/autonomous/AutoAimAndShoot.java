package missdaisy.autonomous;

import missdaisy.loops.AutoAimDriveController;
//import missdaisy.loops.AutoAimShooterController;
import missdaisy.subsystems.Drive;
//import missdaisy.subsystems.Intake;
//import missdaisy.subsystems.Shooter;

public class AutoAimAndShoot extends State {
	private Drive mDrive;
	//private Shooter mShooter;
	//private Intake mIntake;
	private AutoAimDriveController mDriveController;
	//private AutoAimShooterController mShooterController;
	private boolean mHasBall;
	private boolean mBallFired;

	public AutoAimAndShoot() {
		super("AutoAimAndShoot");
		mDrive = Drive.getInstance();
		//mShooter = Shooter.getInstance();
		//mIntake = Intake.getInstance();
		mDriveController = AutoAimDriveController.getInstance();
		//mShooterController = AutoAimShooterController.getInstance();
		mBallFired = false;
	}
	
	public void enter() {
		mDrive.setCurrentController(mDriveController);
//		mShooter.setCurrentController(mShooterController);
	}
	
	@Override
	public void running() {
/*		mHasBall = mIntake.seesBall();
		if (!mHasBall) 
			mIntake.setConveyorSpeed(0.5);
		else
			mIntake.setConveyorSpeed(0.0);

		if (mDriveController.onTarget() && mShooterController.onTarget()
				&& mHasBall) {
			mIntake.setConveyorSpeed(1.0);
			mBallFired = true;
		}
		*/
	}
	

	@Override
	public boolean isDone() {
		return mBallFired;
	}

}
