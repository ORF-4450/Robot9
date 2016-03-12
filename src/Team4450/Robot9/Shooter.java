package Team4450.Robot9;

import Team4450.Lib.*;
import Team4450.Lib.JoyStick.JoyStickButtonIDs;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter
{
	private final Robot		robot;
	private final Teleop	teleop;
	
	//private final CANTalon		pickupMotor = new CANTalon(7);
	private final SpeedController	pickupMotor;
	private final Talon				shooterMotor1 = new Talon(0);
	private final Talon				shooterMotor2 = new Talon(1);
	private final FestoDA			pickupCylinder = new FestoDA(6);
	private final FestoDA			hoodCylinder = new FestoDA(4);
	private final DigitalInput		pickupSwitch = new DigitalInput(0);
	
	private Thread					autoPickupThread, shootThread;

	Shooter(Robot robot, Teleop teleop)
	{
		Util.consoleLog();
		
		this.robot = robot;
		this.teleop = teleop;

		// Handle the fact that the pickup motor is a CANTalon on competition robot
		// and a pwm Talon on clone.
		
		if (robot.robotProperties.getProperty("RobotId").equals("comp")) 
		{
			pickupMotor = new CANTalon(7);
			robot.InitializeCANTalon((CANTalon) pickupMotor);
		}
		else
			pickupMotor = new Talon(7);
		
		PickupArmUp();
		HoodDown();
	}

	public void dispose()
	{
		Util.consoleLog();
		
		if (autoPickupThread != null) autoPickupThread.interrupt();
		if (shootThread != null) shootThread.interrupt();

		if (pickupMotor != null)
		{
			if (robot.robotProperties.getProperty("RobotId").equals("comp"))
				((CANTalon) pickupMotor).delete();
			else
				((Talon) pickupMotor).free();
		}
		
		if (shooterMotor1 != null) shooterMotor1.free();
		if (shooterMotor2 != null) shooterMotor2.free();
		if (pickupCylinder != null) pickupCylinder.dispose();
		if (hoodCylinder != null) hoodCylinder.dispose();
		if (pickupSwitch != null) pickupSwitch.free();
	}

	public void PickupArmUp()
	{
		Util.consoleLog();
		
		pickupCylinder.SetA();
	}
	//---------------------------------------
	public void PickupArmDown()
	{
		Util.consoleLog();
		
		pickupCylinder.SetB();
	}
	//---------------------------------------
	public void HoodDown()
	{
		Util.consoleLog();
		
		hoodCylinder.SetB();
	}
	//----------------------------------------
	public void HoodUp()
	{
		Util.consoleLog();
		
		hoodCylinder.SetA();
	}
	//----------------------------------------
	public void PickupMotorIn(double speed)
	{
		Util.consoleLog("%f", speed);

		pickupMotor.set(speed);
	}
	//----------------------------------------
	public void PickupMotorOut(double speed)
	{
		Util.consoleLog("%f", speed);
		
		pickupMotor.set(Math.abs(speed) * -1);
		SmartDashboard.putBoolean("PickupMotor", true);
	}
	//----------------------------------------
	public void PickupMotorStop()
	{
		Util.consoleLog();
		
		pickupMotor.set(0);
		SmartDashboard.putBoolean("PickupMotor", false);
	}
	//----------------------------------------
	public void ShooterMotorStart(double speed)
	{
		Util.consoleLog("%f", speed);
	
		shooterMotor1.set(speed);
		shooterMotor2.set(speed);
		SmartDashboard.putBoolean("ShooterMotor", true);
	}
	//----------------------------------------
	public void ShooterMotorStop()
	{
		Util.consoleLog();
		
		shooterMotor1.set(0);
		shooterMotor2.set(0);
		
		if (teleop != null) teleop.rightStick.FindButton(JoyStickButtonIDs.TOP_LEFT).latchedState = false;
		SmartDashboard.putBoolean("ShooterMotor", false);
	}
	//----------------------------------------
	public void StartAutoPickup()
	{
		Util.consoleLog();
		
		if (autoPickupThread != null) return;

		autoPickupThread = new AutoPickup();
		autoPickupThread.start();
	}
	//----------------------------------------
	public void StopAutoPickup()
	{
		Util.consoleLog();

		if (autoPickupThread != null) autoPickupThread.interrupt();
		
		autoPickupThread = null;
	}

	// Automatic ball pickup thread.
	
	private class AutoPickup extends Thread
	{
		AutoPickup()
		{
			Util.consoleLog();
			
			this.setName("AutoPickup");
	    }
		
	    public void run()
	    {
	    	Util.consoleLog();
	    	
	    	try
	    	{
	    		PickupArmDown();
	    		sleep(250);
	    		PickupMotorIn(1.0);
	    		
    	    	while (!isInterrupted() && !pickupSwitch.get())
    	    	{
    	            // We sleep since JS updates come from DS every 20ms or so. We wait 50ms so this thread
    	            // does not run at the same time as the teleop thread.
    	            sleep(50);
    	    	}
	    	}
	    	catch (InterruptedException e) {}
	    	catch (Throwable e) {e.printStackTrace(Util.logPrintStream);}

	    	PickupMotorStop();
			PickupArmUp();
			
			autoPickupThread = null;
	    }
	}	// end of AutoPickup thread class.
	//----------------------------------------
	public void StartAutoShoot(boolean hoodUp)
	{
		Util.consoleLog();

//		if (hoodUp)
//			HoodUp();
//		else
//			HoodDown();
		
		if (shootThread != null) return;
		
		shootThread = new Shoot(true);
		shootThread.start();
	}
	//----------------------------------------
	public void StopAutoShoot()
	{
		Util.consoleLog();

		if (shootThread != null) shootThread.interrupt();
		
		shootThread = null;
	}
	//----------------------------------------
	public void StartShoot(boolean hoodUp)
	{
		Util.consoleLog();

//		if (hoodUp)
//			HoodUp();
//		else
//			HoodDown();
		
		if (shootThread != null) return;
		
		shootThread = new Shoot(false);
		shootThread.start();
	}
	//----------------------------------------
	public void StopShoot()
	{
		Util.consoleLog();

		if (shootThread != null) shootThread.interrupt();
		
		shootThread = null;
	}

	// Ball shooting thread.
	
	private class Shoot extends Thread
	{
		boolean			spinUpMotors;
		
		Shoot(boolean spinUpMotors)
		{
			Util.consoleLog();
			
			this.setName("Shoot");
			this.spinUpMotors = spinUpMotors;
		}
		
	    public void run()
	    {
	    	Util.consoleLog();
	    	
	    	try
	    	{
	    		if (spinUpMotors)
	    		{
	    			ShooterMotorStart(1.0);

	    			sleep(2500);
	    		}
	    		
	    		PickupMotorIn(1.0);
	    	
	    		sleep(1000);
	    	}
	    	catch (InterruptedException e) {}
	    	catch (Throwable e) {e.printStackTrace(Util.logPrintStream);}

	    	PickupMotorStop();
    		ShooterMotorStop();
    		
    		shootThread = null;
	    }
	}	// end of Shoot thread class.
}