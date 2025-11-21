Pull from Jackie, which has bugs related to the doors and displays fixed.

NOTE ALL TODOS IN THE ACTUAL CODE.



TODO:



* POLLING: In both multiplexers, we need to constantly poll certain states.

-Building MUX: for each FloorCallButtons:

&nbsp;	-isUpPressed()

&nbsp;	-isDownPressed()

-Elevator MUX:

&nbsp;	-isFireKeyActive()

&nbsp;	-getPressedFloors()

&nbsp;	-isObstructed()

&nbsp;	-isOverloaded()

&nbsp;	-current position (need to call stop when we reach the desired floor).

* getPressedFloors() needs to be written to return the head of the queue. Message Body can't hold a list.
* FloorCallButtons needs to have isUpCallPressed() and isDownCallPressed() turn around and ask the GUI if those buttons are active. No longer utilize pressUpCall() and pressDownCall().
* **Integrate motor assembly**. handleCarDispatch(), handleCarPosition rely on Motor and Sensors.
* **Implement handleSelectionReset in Elevator MUX**. Needs to call the reset in the CabinPassengerPanel for the given button, which will then tell the GUI to turn off the light on that button.
* Implement responses to the following messages:

&nbsp;	-113 Calls Enabled, subtopic 0, Body 0 = disabled 1 = enabled. Building MUX. Turns the buttons on the floor call buttons off/on.

&nbsp;	-114 Selections Enabled, subtopic 0, Body 0 = disabled 1 = enabled. Elevator MUXs. Turns the buttons on the cabin passenger panels off/on. NEEDS TO APPLY TO ALL 4 ELEVATORS.

&nbsp;	-115 Selections type, subtopic 0, Body 0 = single 1 = multiple. Elevator MUXs. Makes it so only 1 selection can be made on the passenger panel, or multiple selections can. NEEDS TO APPLY TO ALL 4 ELEVATORS.

&nbsp;	-116 Play sound, subtopic 0, Body 0 = arrival chime 1 = overload warning. Building MUX.

* Send out messages according to each 200 level topic.

&nbsp;	-Hall Call 

&nbsp;	-Cabin Select

&nbsp;	-Car Position

&nbsp;	-Door sensor

&nbsp;	-Door status

&nbsp;	-Cabin load

&nbsp;	-Fire Key

&nbsp;	-Car current direction

&nbsp;	-Car movement

* TESTING!

Luxury Update before demo:

* Resize the GUI to a better size. 
* Show the floor numbers next to the floor call buttons, or when hovering over one.
