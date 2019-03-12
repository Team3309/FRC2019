package org.usfirst.frc.team3309.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class LightComm extends Subsystem {

    private SerialPort port;

    private boolean wasHoldingGamePiece;
    private boolean wasEnabled;
    private boolean wasConnectedToFMS;
    private boolean wasExtended;
    private boolean sentTime;
    private double lastHeartbeatTime;

    public LightComm() {
        port = new SerialPort(115200, SerialPort.Port.kMXP);
    }

    @Override
    public void periodic() {

        boolean hasGamepiece = Robot.cargoHolder.hasCargo() || Robot.panelHolder.hasPanel();
        boolean isEnabled = DriverStation.getInstance().isEnabled();
        boolean connectedToFMS = DriverStation.getInstance().isFMSAttached();
        boolean isExtended = Robot.cargoIntake.getPosition() == CargoIntake.CargoIntakePosition.Extended;

        if (hasGamepiece != wasHoldingGamePiece) {
            wasHoldingGamePiece = !wasHoldingGamePiece;
            sendHoldingGamePiece(hasGamepiece);
        } else if (isEnabled != wasEnabled) {
            System.out.println("Enabled: " + isEnabled);
            wasEnabled = !wasEnabled;
            sendIsEnabled(isEnabled);
        } else if (connectedToFMS != wasConnectedToFMS) {
            wasConnectedToFMS = !wasConnectedToFMS;
            sendConnectedToFMS(connectedToFMS);
        } else if (isExtended != wasExtended) {
            wasExtended = !wasExtended;
            setExtended(isExtended);
        } else {
            sendHeartbeat();
        }

        double curTime = Timer.getMatchTime();
        if (120 - curTime <= 30 && !sentTime) {
            setTime(new byte[]{(byte) curTime});
            sentTime = true;
        }
    }

    public void sendHeartbeat() {
        double now = Timer.getFPGATimestamp();
        if (now - lastHeartbeatTime > 0.5) {
            port.writeString("h");
            lastHeartbeatTime = now;
        }
    }

    public void sendIsEnabled(boolean isEnabled) {
        port.write(new byte[]{'e', booleanToByte(isEnabled)}, 2);
    }

    private byte booleanToByte(boolean val) {
        return (byte) (val ? 1 : 0);
    }

    public void sendConnectedToFMS(boolean connected) {
        port.write(new byte[]{'f', booleanToByte(connected)}, 2);
    }

    public void setExtended(boolean extended) {
        port.write(new byte[]{'x', booleanToByte(extended)}, 2);
    }

    public void sendHoldingGamePiece(boolean holdingGamePiece) {
        port.write(new byte[]{'g', booleanToByte(holdingGamePiece)}, 2);
    }

    public void setTime(byte[] time) {
        port.write(time, time.length);
    }

}
