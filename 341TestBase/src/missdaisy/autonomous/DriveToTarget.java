package missdaisy.autonomous;

import missdaisy.Vision;
import missdaisy.loops.DriveDistanceController;
import missdaisy.subsystems.Drive;

public class DriveToTarget extends State {
	private Drive mDrive;
	private DriveDistanceController mDriveController;
	private Vision mVision;
	private double mSpeed;
	private double mDesiredDistance;
	private double mCurrentDistance;
	private double mDistanceToTravel;

	public DriveToTarget(double distanceFromTarget, double speed) {
		super("DriveToTarget");
		mDrive = Drive.getInstance();
		mDriveController = DriveDistanceController.getInstance();
		mDesiredDistance = distanceFromTarget;
		mSpeed = speed;
	}
	
	public void enter() {
		mCurrentDistance = mVision.getRange();
		mDistanceToTravel = mCurrentDistance - mDesiredDistance;
		mDriveController.setGoal(mDistanceToTravel, mSpeed);
		mDrive.setCurrentController(mDriveController);
	}
	@Override
	public void running() {
	}
	
	public void exit() {
		mDrive.setOpenLoop();
	}

	@Override
	public boolean isDone() {
		return mDriveController.onTarget();
	}

}
