 package missdaisy.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import missdaisy.loops.DriveStraightController;
import missdaisy.subsystems.Drive;

public class DriveTime extends State {
	private Drive mDrive;
	private DriveStraightController mDriveController;
	private double mTime;
	private double mSpeed;
	private double startTime;
	private double currentTime;
	
	public DriveTime(double time, double speed) {
		super("DriveTime");
		mSpeed = speed;
		mTime = time;
		mDrive = Drive.getInstance();
		//mDriveController = DriveStraightController.getInstance();
	}

	public void enter() {
		//mDriveController.setGoal(mSpeed);
		//mDrive.setCurrentController(mDriveController);
		mDrive.useAlphaFilter(true);
		startTime = System.currentTimeMillis();
	}
	
	@Override
	public void running() {
		currentTime = System.currentTimeMillis();
		mDrive.setSpeedTurn(mSpeed, 0.0);
	}

	@Override
	public boolean isDone() {
		return (currentTime - startTime)/1000 > mTime;
	}
	
	public void exit() {
		mDrive.setSpeedTurn(0.0, 0.0);
		mDrive.setOpenLoop();
	}
}
