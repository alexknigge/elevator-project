# Elevator-Project

A small Java/JavaFX API that simulates a set of elevator passenger devices
and a simple GUI that updates images when model events occur. It is intended to
be part of a much larger project.

## Dependencies
- Java 21 (or a compatible JDK)
- JavaFX available on the classpath/module-path

## File structure
```
Elevator-Project/
├─ .dist/
├─ .git/
├─ .gitignore
├─ README.md
├─ docs/
├─ out/
├─ res/
│  ├─ image_files (.png)
├─ runnables/
│  ├─ runPfd.java        # simulation demo file
│  └─ testApi.java       # additional test to just test the api function calls
└─ src/
	├─ CabinPassengerPanel.java
	├─ CabinPassengerPanelAPI.java
	├─ ElevatorDoorsAssembly.java
	├─ ElevatorFloorDisplay.java
	├─ FloorCallButtons.java
	├─ gui.java            # JavaFX GUI with listeners in the api backend files
	└─ utils/
		├─ imageLoader.java
		└─ testImageSort.java
```

## How to run the demo
- Compile all java files
- Run the file runPfd.java

`runPfd.java` starts the GUI and then simulates calls like pressing floor buttons and opening/closing doors. The GUI swaps images from `res/` to show the current state.

## Notes
- Reworked the MUX to be cleaner
- Deleted old gui listener to use only the MUX listener
- - GUI now purely relies on the MUX listner functions
- Reintegrated MUX listeners into the API
- Made the MUX multithreading safe (hopefully)

## TODO Still
- Need firealarm and mtx/fire key API integration
- Need chime sound
- Need to fully implement all the MUX listener event handling functions in the GUI
- Need to integrate official topics/subtopics into MUX
- Need to test external command integrations once official topics are integrated
- Need to test motion sim once integrated