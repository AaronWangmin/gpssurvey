/*
 * Copyright (C) 2014 Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.org.rlinsdale.gpssurvey.input;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import uk.org.rlinsdale.gpssurvey.GPSSportsInformationRecorder.Command;
import uk.org.rlinsdale.gpssurvey.input.IRControlAction.Button;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDTService;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDTService.Exitcode;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.MDThread;
import uk.org.rlinsdale.rpiembeddedlibrary.thread.Reporting;

/**
 * The Thread Listening for IR button presses.
 * 
 * @author Richard Linsdale (richard.linsdale at blueyonder.co.uk)
 */
public class IrListener extends MDThread<Command> {

    // i2c protocol device codes;
    private final static byte INTERRUPT_DEVICE = 0;
    private final static byte IR_DEVICE = 1;
    private final static byte BUTTON_DEVICE = 2;
    // i2c button_device button codes
    private final static byte POWERDOWN_BUTTON = 1;
    // i2c IR_device key code mappings (keys are 1 .. 9)
    private final static Button[] mapKey2Button = new Button[]{
        Button.POWER,
        Button.A,
        Button.B,
        Button.C,
        Button.UP,
        Button.LEFT,
        Button.SELECT,
        Button.RIGHT,
        Button.DOWN
    };
    private final GpioController gpio;
    private final GpioPinDigitalInput i2cInt;
    private final I2CDevice device; // the i2c device
    private InterruptListener interruptlistener;
    
    /**
     * Create listening thread and start it.
     * 
     * @throws IOException if problems
     */
    public static void createAndStart() throws IOException {
        IrListener thread = new IrListener();
        thread.start();
        thread.sendMessage(Command.START);
    }

    private IrListener() throws IOException {
        super("IR Listener", Command.CLOSE);
        Reporting.registerControl("IR Listener", 'i');
        gpio = GpioFactory.getInstance();
        i2cInt = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, "I2C interrupt");
        i2cInt.setShutdownOptions(true);
        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        device = bus.getDevice(0x60);
    }

    @Override
    protected void processMessage(Command command, Object commandParameters) throws IOException {
        Reporting.report("IR Listener", 3, "Processing command %s", command);
        switch (command) {
            case START:
                i2cInt.addListener(interruptlistener = new InterruptListener());
                break;
            case CLOSE:
                i2cInt.removeListener(interruptlistener);
                // brute force close down GPIO - needs to be done elsewhere if we ever have multiple GPIO users
                gpio.removeAllListeners();
                gpio.shutdown();
                break;
        }
    }

    private class InterruptListener implements GpioPinListenerDigital {

        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpdsce) {
            PinState pinstate = gpdsce.getState();
            if (pinstate.isLow()) {
                try {
                    // interrupt is a setting int pin to low (0)
                    Reporting.report("IR Listener", 3, "Interrupt raised");
                    byte[] message = new byte[2];
                    int zcount = device.read(message, 0, 2); // get the reporting message
                    if (zcount == 2) {
                        int dev = message[0];
                        int key = message[1];
                        switch (dev) {
                            case INTERRUPT_DEVICE:
                                Reporting.report("IR Listener", 3, "No message on 0x60");
                                break;
                            case BUTTON_DEVICE:
                                if (key == POWERDOWN_BUTTON) {
                                    Reporting.report("IR Listener", 3, "Received Powerdown button");
                                    MDTService.sendMessage("Controller", Command.BUTTON, Button.POWERDOWN);
                                }
                                break;
                            case IR_DEVICE:
                                if (key > 0) {
                                    Button b = mapKey2Button[key - 1];
                                    Reporting.report("IR Listener", 3, "Received IR message: " + b.toString());
                                    MDTService.sendMessage("Controller", Command.BUTTON, b);
                                }
                                break;
                            default:
                                throw new IOException("illegal i2c reporting device - " + message[0]);
                        }
                    } else {
                        throw new IOException("illegal i2c message length");
                    }
                } catch (IOException ex) {
                    MDTService.reportExceptionAndExit(ex, Exitcode.EXIT_PROGFAIL);
                }
            }
        }
    }
}
