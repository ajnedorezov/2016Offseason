package missdaisy.utilities;

import edu.wpi.first.wpilibj.Timer;

public class Integrate {

	private static double lastTime = 0;
	private static double nowTime = 0;
	
	public static double Integrate(double currentAcceleration) {
		nowTime = Timer.getFPGATimestamp();
		
		
		lastTime = nowTime;
		return lastTime;
	}
}
