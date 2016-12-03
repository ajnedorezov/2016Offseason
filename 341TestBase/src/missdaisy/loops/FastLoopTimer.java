package missdaisy.loops;

import missdaisy.Constants;
import missdaisy.subsystems.DaisySubsystem;
import missdaisy.subsystems.Drive;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author jrussell
 */
public class FastLoopTimer extends TimerTask {
	private static FastLoopTimer instance = null;
	private Timer timer;
	private final DaisySubsystem[] subsystems = { Drive.getInstance()};
	private final Navigation navigation = Navigation.getInstance();

	public static FastLoopTimer getInstance() {
		if (instance == null)
			instance = new FastLoopTimer();
		return instance;
	}

	private FastLoopTimer() {
		timer = new java.util.Timer();
	}

	public synchronized void start() {
		timer.scheduleAtFixedRate(this, 0L, Constants.fastLoopTimerLoopLength);
	}

	public void run() {
		navigation.run();
		for (int i = 0; i < subsystems.length; i++) {
			subsystems[i].runInputFilters();
			subsystems[i].runCurrentController();
			subsystems[i].runOutputFilters();
		}
	}
}