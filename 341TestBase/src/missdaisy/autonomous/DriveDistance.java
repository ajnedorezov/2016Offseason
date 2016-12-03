package missdaisy.autonomous;

import missdaisy.loops.DriveDistanceController;
import missdaisy.subsystems.Drive;

public class DriveDistance extends State {
	private Drive mDrive;
	private DriveDistanceController mDriveController;
	private double mDistance;
	private double mSpeed;
	
	public DriveDistance(double distance, double speed) {
		super("DriveDistance");
		mDrive = Drive.getInstance();
		mDriveController = DriveDistanceController.getInstance();
		mDistance = distance;
		mSpeed = speed;
	}

	public void enter() {
		mDriveController.setGoal(mDistance, mSpeed);
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
