package Team7MotionControl.Elevator_Controler;

import Team7MotionControl.GUI.ElevatorGUI;
import Team7MotionControl.Hardware.Motor;
import Team7MotionControl.Hardware.Sensor;
import Team7MotionControl.Simulation.MotionSimulation;
import Team7MotionControl.Util.Constants;
import Team7MotionControl.Util.Direction;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MotionAPI {
    public Direction direction=null;
    private Integer top_Alignment=null;
    private Integer bottom_Alignment=null;

    private HashMap<Integer, Sensor> sensor_HashMap;
    private int MAX_SENSOR_IDX=19;
    private Motor motor;

    private MotionSimulation motionSimulation;

    public MotionAPI(){
        motor=new Motor();
        sensor_HashMap=new HashMap<>();
        for (int i = 0; i <= MAX_SENSOR_IDX; i++) {
            sensor_HashMap.put(i, new Sensor());
        }
        motionSimulation= new MotionSimulation(1,motor,sensor_HashMap);

    }

    /**
     * Set directions of elevator
     * @param direction Up, down or null
     */
    public void set_direction(Direction direction){
        motor.set_direction(direction);
    }

    /**
     * Returns the floor that the top of elevator is aligned with
     * @return A floor number or null
     */
    public Integer top_alignment(){
        for(int index: sensor_HashMap.keySet()){
            if (sensor_HashMap.get(index).is_triggered()&& index%2==1){
                return index;
            }
        }
        return null;
    }



    /**
     * Returns the floor that the bottom of elevator is aligned with
     * @return A floor number or null
     */
    public Integer bottom_alignment(){
        for(int index: sensor_HashMap.keySet()){
            if (sensor_HashMap.get(index).is_triggered()&& index%2==0){
                return index;
            }
        }
        return null;
    }

    /**
     *Starts the motor and the elevator’s
     * movement at constant_speed either up or down
     */
    public void start(){
        motor.start();
    }

    /**
     *Starts the motor and the elevator’s
     * movement from constant_speed to zero with a decay of deeleration
     */
    public void stop(){
        motor.stop();
    }



}