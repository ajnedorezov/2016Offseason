package missdaisy;

public final class Constants {
	// PWMs
	public static final int leftDriveMotorPWM = 0;
	public static final int rightDriveMotorPWM = 1;
	public static final int conveyorMotorPWM = 2;
	public static final int intakeMotorPWM = 3;
	public static final int hangerMotorPWM = 4;
	
	// Digital Inputs
	public static final int leftDriveEncoder1DI = 0;
	public static final int leftDriveEncoder2DI = 1;
	public static final int rightDriveEncoder1DI = 2;
	public static final int rightDriveEncoder2DI = 3;
	public static final int shooterEncoderDI = 4;
	public static final int conveyorBallSensorDI = 5;
	
	// Analog
	public static final int gyroAI = 0;
	
	// CAN (ie the device number)
	public static final int shooterWheelMotorCAN = 0;
	
	// Solenoids
	public static final int statusLightSolenoid = 0;
	public static final int visionLightSolenoid = 1;
	public static final int intakeSolenoid1 = 4;
	public static final int intakeSolenoid2 = 5;
	public static final int hoodSolenoid = 6;
	public static final int hangerSolenoid = 7;
	
	// Controller ports
	public static final int driveControllerPort = 0;
	public static final int operatorControllerPort = 1;
	public static final double deadBand = 0.3;
	
	// Other
	/**
	 * The distance the robot moves per shaft rotation.
	 * Used to calculate the speed of the robot in the <code>Drive</code> class.
	 */
	public static final double driveDistancePerPulse = (4*Math.PI)/255;
	public static final long fastLoopTimerLoopLength = 5L;
	public static final double kDriveAlpha = 0.05;
	
	// Default values for properties if they cannot be found
	public static final double kDefaultDistanceTolerance = 1;
	public static final double kDefaultAngleTolerance = 1;
	public static final double kDefaultShooterRPMOuterWorks = 6250;
	public static final double kDefaultShooterRPMBatter = 4000;
	public static final double kDefaultShooterRPMTolerance = 50;
	public static final double kDefaultTurnPIDMaxMotorOutput = 0.3;
	
	public static final double driveMaximumVelocity = 10; // 10 ft/s
	public static final double driveMaximumAcceleration = 1; // 1 ft/s^2
	public static final double driveMaximumJerk = 0.1; // ft/s^3
	public static final double defaultTrajectoryTimeStep = 0.05; // 20ms
}
