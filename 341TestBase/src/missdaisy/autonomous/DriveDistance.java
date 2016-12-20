package missdaisy.autonomous;

import missdaisy.Constants;
import missdaisy.loops.DriveDistanceController;
import missdaisy.subsystems.Drive;
import missdaisy.utilities.Trajectory;

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
		Trajectory mTrajectory = new Trajectory();
		mTrajectory.generate(mDistance, mSpeed, Constants.driveMaximumAcceleration, Constants.driveMaximumJerk, Constants.defaultTrajectoryTimeStep);
		
		mDriveController.loadProfile(mTrajectory, 1.0, mDrive.getGyroAngle());
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
