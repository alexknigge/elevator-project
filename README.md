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
- Images used by the GUI are in the `res/` directory. The `utils.imageLoader`
	loads and sorts them so the GUI can reference them by index.
- Floor numbering: `FloorCallButtons` is constructed with a floor number and
	total floors (e.g. `new FloorCallButtons(1, 10)`).

## Next steps / Debugging
- Down call button doesnt light up even on non-ground level floors.
- Further testing would be wise.