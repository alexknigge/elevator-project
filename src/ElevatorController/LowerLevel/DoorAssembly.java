package ElevatorController.LowerLevel;

import Bus.SoftwareBus;

public class DoorAssembly implements Runnable {
    //Not sure of we need opened and closed
    private boolean opened;
    private boolean closed;
    private boolean obstructed;
    private boolean fullyClosed;
    private boolean fullyOpen;
    private boolean overCapacity;
    private SoftwareBus softwareBus;

    public DoorAssembly(int elevatorNumber, SoftwareBus  softwareBus) {
        //TODO may need to take in int for elevator number for software bus subscription
        //TODO call subscribe on softwareBus w/ relevant topic/subtopic

        this.opened = true;
        this.closed = false;
        this.obstructed = false;
        this.fullyClosed = false;
        this.fullyOpen = true;
        this.overCapacity = false;
        this.softwareBus = softwareBus;

        //Start DoorAssembly Thread
        Thread thread = new Thread(this);
        thread.start();
    }

    //Todo: Write these methods
    public void open(){}

    public void close(){}

    public boolean obstructed(){return obstructed;}

    public boolean fullyClosed(){return fullyClosed;}

    public boolean fullyOpen(){return fullyOpen;}

    public boolean overCapacity(){return overCapacity;}

    /**
     * Runs this operation.
     * query SoftwareBus and set variables appropriately
     */
    @Override
    public void run() {

    }
}
