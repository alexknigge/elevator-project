package Team7MotionControl.Simulation;

import Team7MotionControl.Hardware.Elevator;
import Team7MotionControl.Hardware.Motor;
import Team7MotionControl.Hardware.Sensor;
import Team7MotionControl.Util.Constants;
import Team7MotionControl.Util.Direction;
import Team7MotionControl.Util.Observable;
import Team7MotionControl.Util.Observer;

import java.util.HashMap;

/**
 * This class simulates motion, for demonstration purposes
 * will be deleted later and replaced with working software
 */

public class MotionSimulation implements Runnable, Observer {
    // Convert floor indicator to Sensor
    private HashMap<Integer, Sensor> sensor_HashMap =new HashMap<>();
    // Convert Sensors to y positions
    private final HashMap<Integer, Double>  sensor_pos_Map = new HashMap<>();

    // The motor object (to be replaced by Hardware)
    private final Motor motor;

    // The elevator object (for simulation purposes) also hardware
    private final Elevator elevator;

    // How long the thread sleeps before updating position, velocity, etc.
//    private final int SLEEP_MILLIS = 100;

    // Top Level
    private final int MAX_SENSOR_IDX = 19;

    // The elevator's current speed
    private double current_speed = 0.0;

    // 1 if accelerating, -1 if decelerating,
    private int accelerating_indicator = 0;

    // Which direction the elevator is going
    private Direction direction = Direction.NULL;

    // The floor number associated (-1 when unset)
    private volatile int top_idx = 1;
    private volatile int bottom_idx = 0;


    private double speedFactor=1;

    // Sensor tolerance
    private final double TOLERANCE = 0.5;

    /**
     * Makes a motion simulation
     */
    public MotionSimulation(double speedFactor, Motor motor,HashMap<Integer, Sensor> sensor_HashMap){
        this.motor =motor;
        elevator = new Elevator();
        this.speedFactor=speedFactor;
        this.sensor_HashMap=sensor_HashMap;
        motor.subscribe(this);

        // Initializing the Hash Maps
        double y_pos = 0;
        for (int i = 0; i <= MAX_SENSOR_IDX; i++) {
            //sensor_HashMap.put(i, new Sensor());

            if (i % 2 == 0){
                // Lower level sensor
                y_pos += Constants.FLOOR_THICKNESS;
            } else {
                // Upper level sensor
                y_pos += Constants.HEIGHT;
            }
            sensor_pos_Map.put(i, y_pos);
        }

        //this.start();


    }

    /**
     *  motion simulation is runnable
     */
    @Override
    public void run() {
        boolean running = true;
        update_sensors();

        while(running){
            // Update
            tick();
            try {
                // Sleep
                Thread.sleep((long) ((int)Constants.SIM_SLEEP_TIME*speedFactor));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



    /**
     * Update the elevator, then the sensors
     */
    private void tick() {
        update_elevator();
        update_sensors();
    }

    /**
     * Use the constants, to update the position, and velocity, according to
     * direction
     */
    private void update_elevator() {
        if (accelerating_indicator != 0) {

            current_speed += Constants.ACCELERATION_TICK * accelerating_indicator;
        } else {
            // No acceleration go towards zero, so this is sorta a janky reuse of accleration indicator
            if (current_speed != 0.0) {
                // Get the sign of the current speed (1) positive,  (-1) negative
                double steppre = Constants.ACCELERATION_TICK * Math.signum(current_speed);
                if (Math.abs(steppre) >= Math.abs(current_speed)) {
                    current_speed = 0.0;
                } else {
                    current_speed -= steppre;
                }
            }
        }

        //goes to max speed negative or positive account for going over so we
        // dont have to worry about rounding
        if (Math.abs(current_speed) > Constants.MAX_SPEED_TICK) {
            current_speed = Math.copySign(Constants.MAX_SPEED_TICK, current_speed);
        }

        // Change in y position, based on speed and
        if (current_speed != 0.0) {
            double delta_Y = current_speed;
            // Tell observers
            //elevator.set_y_position(elevator.getY_position() + delta_Y);
        } else {
            //If we've come to a full stop and previously were decelerating, reset indicator
            if (accelerating_indicator < 0) {
                accelerating_indicator = 0;
            }
        }
//        System.out.println("current speed: "+ current_speed);
        elevator.set_y_position(elevator.getY_position() + current_speed);
//        System.out.println(elevator.getY_position() + " + "+ current_speed);
    }

    private synchronized void update_sensors() {
        int newBottom = -1;
        int newTop = -1;
        double yBottom = elevator.getY_position();
        double yTop = elevator.upper_bound();

        for (Integer idx : sensor_pos_Map.keySet()) {
            double sensorY = sensor_pos_Map.get(idx);

            if (sensorY+ TOLERANCE >= yBottom && sensorY- TOLERANCE <= yTop) {
                sensor_HashMap.get(idx).set_triggered(true);

                if(newBottom==-1){
                    newBottom=idx;

                }else{
                    newTop=idx;
                }

            } else {
                sensor_HashMap.get(idx).set_triggered(false);
            }
        }
        top_idx=newTop;
        bottom_idx=newBottom;

    }

    public Integer top_alignment(){
        if(top_idx==-1){
            return null;
        }
        else{
            return top_idx;
        }
    }

    //when I added this synchronized, it stopped printing nulls
    public Integer bottom_aligment(){
        //System.out.println("In motion sim "+bottom_idx);
        if(bottom_idx==-1){
            return null;
        }
        else{
            return bottom_idx;
        }
    }







    /**
     * @param floor_indicator 0 is bottom of first floor, 1 is top of first
     *                        floor, 2 is bottom of second floor, etc.
     * @return the Sensor object associated
     */
    public Sensor get_sensor(int floor_indicator) {
        return sensor_HashMap.get(floor_indicator);
    }

    /**
     * @return Gets the elevators position based on current speed
     */
    private double elevator_delta_y() {
        return  current_speed * millis_to_seconds(Constants.SIM_SLEEP_TIME);
    }
    /**
     *  Handy converter
     * @param milliseconds the number of milliseconds
     * @return converted to seconds
     */
    private double millis_to_seconds(double milliseconds){
        return milliseconds/1000;
    }

    //Todo: Comment these later
    public HashMap<Integer, Sensor> getSensors() {
        return sensor_HashMap;
    }

    public HashMap<Integer, Double> get_sensor_pos_HashMap() {
        return sensor_pos_Map;
    }

    public Elevator getElevator() {
        return elevator;
    }

    public Motor getMotor() {
        return motor;
    }

    public void stop(){
        motor.stop();
        direction=Direction.NULL;
        accelerating_indicator=0;
    }

    public void start(){
//        at_start=false;
        motor.start();
        if(direction.equals(Direction.UP)){
            accelerating_indicator=1;
        }else if (direction.equals(Direction.DOWN)){
            accelerating_indicator=-1;
        }else{
            System.out.println("Set direction");
        }

    }

    //TODO: remove this when we implement the software bus
    public void setDirection(Direction direction){
        this.direction=direction;
    }

    /**
     * "so um basically we need to listen to the motor instead of telling it what to do"
     * -Valerie Barker
     * @param viewee the updated observable object (THIS SHOULD BE A MOTOR!!!1)
     */
    @Override
    public void update(Observable viewee) {
        Motor beloved = ((Motor) viewee);
        if (viewee instanceof Motor) {
            System.out.println("Motion sim being updated");
            if (beloved.is_off()) {
                accelerating_indicator = 0;
                direction = null;
            } else {
                //"dont even worry about it, im sure its fine" -Natalie Runyan
                //"uhh you forgot an apostrophe" -Youssef Amin
                //at_start = false;
                if (beloved.get_direction() == Direction.UP) {
                    accelerating_indicator = 1;
                } else {
                    accelerating_indicator = -1;
                }
            }
        } else {
            System.err.println("HOLY CRAP THATS NOT A MOTOR!!1!");
        }
    }




















    private void tick(double cycle_time) {
        if (accelerating_indicator != 0) {

            current_speed += Constants.ACCELERATION * cycle_time * accelerating_indicator;
        } else {
            //no acceleration go towards zero, so this is sorta a janky reuse of accleration indicator

            if (current_speed != 0.0) {
                double steppre = Constants.ACCELERATION * cycle_time * Math.signum(current_speed); //get the sig nif number of the current speed
                if (Math.abs(steppre) >= Math.abs(current_speed)) {
                    current_speed = 0.0;
                } else {
                    current_speed -= steppre;
                }
            }
        }

        //goes to max speed negative or positive account for going over so we
        // don't have to worry about rounding
        if (Math.abs(current_speed) > Constants.MAX_SPEED) {
            current_speed = Math.copySign(Constants.MAX_SPEED, current_speed);
        }

        //positive->up, negative-> down
        if (current_speed != 0.0) {
            double delta_Y = current_speed * cycle_time;
            //tell obsetcvers
            elevator.set_y_position(elevator.getY_position() + delta_Y);
        } else {
            //If we've come to a full stop and previously were decelerating, reset indicator
            if (accelerating_indicator < 0) {

                accelerating_indicator = 0;
            }
        }
    }

    public synchronized int[] getAlignment() {
        return new int[]{ bottom_idx, top_idx };
    }




}
