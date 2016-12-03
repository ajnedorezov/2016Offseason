package missdaisy.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import missdaisy.loops.DriveTurnController;
import missdaisy.loops.Navigation;
import missdaisy.subsystems.Drive;
import missdaisy.utilities.DaisyMath;

public class Turn extends State {
	private Drive mDrive;
	private Navigation mNavigation;
	private DriveTurnController mDriveController;
	private double mAngle;

	public Turn (double angle) {
		super("Turn");
		mDrive = Drive.getInstance();
		mNavigation = Navigation.getInstance();
		mDriveController = DriveTurnController.getInstance();
		mAngle = angle;
	}
	
	public void enter() {
		mDriveController.setGoal(DaisyMath.boundAngle0to360Degrees(
				mNavigation.getHeadingInDegrees() + mAngle));
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
