package missdaisy.utilities;

import missdaisy.Constants;

public class AlphaFilter implements Filter {
	private double mAlpha;
	private double mFilteredSpeed;
	
	public AlphaFilter(double alpha) {
		mAlpha = alpha;
	}
	
	public double run(double speed) {
		if ((Math.signum(speed)*Math.signum(mFilteredSpeed)) < 0)
			reset();
		mFilteredSpeed = (mAlpha*speed) + ((1-mAlpha)*mFilteredSpeed);
		//mFilteredSpeed = DaisyMath.applyDeadband(mFilteredSpeed, Constants.deadBand);
		return mFilteredSpeed;
	}
	
	@Override
	public void reset() {
		mFilteredSpeed = 0.0;
	}
}
