package missdaisy.loops;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import missdaisy.fileio.PropertySet;
import missdaisy.subsystems.Drive;
import missdaisy.utilities.DaisyMath;
import missdaisy.utilities.Trajectory;
import missdaisy.utilities.TrajectoryFollower;

/**
 *
 * @author Jared
 */
public class DriveDistanceController implements Controller
{
    private PropertySet mProperties = PropertySet.getInstance();
    private Drive mDrive = Drive.getInstance();
    private TrajectoryFollower mFollower;
    private double kTurn;
    private double distanceThreshold;
    private double heading;
    private double direction;
    
    private static DriveDistanceController instance = null;
    
    public static DriveDistanceController getInstance()
    {
        if( instance == null )
        {
            instance = new DriveDistanceController();
        }
        return instance;
    }
    
    public DriveDistanceController()
    {
        mFollower = new TrajectoryFollower();
        loadProperties();
    }
    
    public void loadProfile(Trajectory profile, double direction, double heading)
    {
        reset();
        mFollower.setTrajectory(profile);        
        this.direction = direction;
        this.heading = heading;
    }

    public void reset() 
    {
        loadProperties();
        mFollower.reset();
        mDrive.resetEncoders();
    }

    public boolean onTarget() 
    {
        return mFollower.isFinishedTrajectory();// && mFollower.onTarget(distanceThreshold);
    }

    public final void loadProperties() 
    {
        mFollower.configure(mProperties.getDoubleValue("distanceKp",0.01),
                mProperties.getDoubleValue("distanceKi",0.01),
                mProperties.getDoubleValue("distanceKd",0.01),
                mProperties.getDoubleValue("distanceKv",0.01),
                mProperties.getDoubleValue("distanceKa",0.01));
        
        distanceThreshold = mProperties.getDoubleValue("distanceTolerance", 1.0);
        kTurn = mProperties.getDoubleValue("DriveDistangleToleranceanceKturn", 0.05);
    }

    public void run() 
    {
        //System.out.println(this.onTarget() + " " + mFollower.isFinishedTrajectory() + " " + mFollower.onTarget(1.0));
    	SmartDashboard.putBoolean("DDC_OnTarget", this.onTarget());
    	SmartDashboard.putBoolean("mFollower_IsFinishedTrajectory", mFollower.isFinishedTrajectory());
    	SmartDashboard.putBoolean("mFollower_onTarget", mFollower.onTarget(1.0));
    	
        if( onTarget() )
        {
            mDrive.setSpeed(0.0, 0.0);
        }
        else
        {
            double distance = direction*mDrive.getAverageDistance();
            double angleDiff = DaisyMath.boundAngleNeg180to180Degrees(heading-mDrive.getGyroAngle());
            
            double speed = direction*mFollower.calculate(distance);
            double turn = kTurn*angleDiff;
            mDrive.setSpeedTurn(speed, turn);
        }
    }
    
}
