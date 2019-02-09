/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.io.File;
import static frc.robot.Ports.*;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private File pathFiles[] = new File[12]; //10 is a random number, needs to be how many paths
  private MotionProfiling selectedPaths[] = new MotionProfiling[3];

  private boolean isDriverControlling;  
  public final DriveTrain driveTrain = new DriveTrain(LEFT_DRIVETRAIN_1, LEFT_DRIVETRAIN_2 , RIGHT_DRIVETAIN_1 , RIGHT_DRIVETAIN_2 , GYRO_PORT);
  private final LambdaJoystick joystick1 = new LambdaJoystick(0, driveTrain::updateSpeed);
  
  private final Elevator elevator = new Elevator(ELEVATOR_PORT , ELEVATOR_ZERO_PORT);
  private final Grabber grabber = new Grabber(LEFT_FLYWHEEL_PORT , RIGHT_FLYWHEEL_PORT , CLAW_LEFT , CLAW_RIGHT , CLAW_LEFT_LIMIT_SWITCH , CLAW_RIGHT_LIMIT_SWITCH );
  private final Arm arm = new Arm(ARM_PORT , ARM_LIMIT_SWITCH_PORT);
  private String filePath = "/home/lvuser/deploy/paths/path%s.pf1.csv"; 
  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */ 
  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();
    for (int i = 0; i < pathFiles.length; i++) {
      String fileName = String.format(filePath , i);
      pathFiles[i] = new File(fileName);
    }
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    
    
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    int firstPath = Integer.valueOf(SmartDashboard.getString("DB/String 1", "Path one?"));
    int secondPath = Integer.valueOf(SmartDashboard.getString("DB/String 2", "Path two?"));
    int thirdPath = Integer.valueOf(SmartDashboard.getString("DB/String 3", "Path three?"));
    int chosenPathNumbers[] = new int[]{firstPath, secondPath, thirdPath};

    for (int i = 0; i < selectedPaths.length; i++) {
      selectedPaths[i] = new MotionProfiling(driveTrain, pathFiles[chosenPathNumbers[i]]);
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    int currentPath = 0;

    for (int i = 0 ; i < selectedPaths.length ; i++){
      if(!selectedPaths[i].isFinished()){
        currentPath = i;
        break;
      }
    }
    if (imageRec.isImageRecTriggered()){
      //image rec code here
    } else if (isDriverControlling) { //had to remove driver control now that its path dependant
      joystick1.listen();
    } else {
      selectedPaths[currentPath].update();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    if(imageRec.isImageRecTriggered()){
      //image recognition code here
    }
    else{
      joystick1.listen();  
    }
  }

public void changeDriverControl(){  
    this.isDriverControlling = !isDriverControlling;
}



  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
