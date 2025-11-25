# Elevator Project

This repository contains a small end-to-end prototype of the elevator system we discussed in class. It includes a software bus for inter-process communication plus three JavaFX-based test apps that exercise the bus in a server/client topology.

## Code layout

- `src/Bus` – lightweight publish/subscribe message bus with TCP networking (`SoftwareBus`, `SoftwareBusInternalNetwork`, `SoftwareBusQueue`).
- `src/Message` – `Message` model used by all components (topic, subtopic, body).
- `src/TestCommandCenter` – JavaFX app that runs the **server** side of the software bus and shows received/sent messages.
- `src/TestProcessor1` and `src/TestProcessor2` – JavaFX clients simulating elevator processors that publish floor requests and react to bus traffic.
- `src/Team7MotionControl` – motion simulation and hardware abstraction used by the processors.
- `src/Main.java` – basic entry point placeholder.

## How the pieces fit

1. The command center (`TestCommandCenterMain`) starts a `SoftwareBus` in **server** mode. It subscribes to two topics (2/0 and 3/1) and renders a UI (`TestCommandCenterDisplay`) that lets you enter messages in `<topic>-<subtopic>-<body>` format.
2. Each processor (`TestProcessorMain1`, `TestProcessorMain2`) starts a `SoftwareBus` in **client** mode. They subscribe to their configured topics and publish messages when buttons are pressed in their respective displays.
3. `SoftwareBusInternalNetwork` uses TCP port `9999` to move serialized `Message` objects between the server and any connected clients. The server rebroadcasts messages to all clients; clients send upstream to the server.
4. `SoftwareBusQueue` buffers incoming messages per process so the UI threads can poll for them without blocking the network threads.

## Running the prototype locally

> These apps depend on JavaFX; ensure `JAVA_HOME` and `PATH_TO_FX` are set for your platform.

Compile all classes (adjust paths to your JavaFX SDK):

```bash
javac --module-path "$PATH_TO_FX" --add-modules javafx.controls -d out $(find src -name "*.java")
```

Run the command center (starts the bus server):

```bash
java --module-path "$PATH_TO_FX" --add-modules javafx.controls -cp out TestCommandCenter.TestCommandCenterMain
```

In two other terminals, launch the processors (they automatically connect to the server):

```bash
java --module-path "$PATH_TO_FX" --add-modules javafx.controls -cp out TestProcessor1.TestProcessorMain1
java --module-path "$PATH_TO_FX" --add-modules javafx.controls -cp out TestProcessor2.TestProcessorMain2
```

With all three windows open, click any processor button or submit a message from the command center to see it propagate over the bus.

## Message format

Messages are serialized as `topic-subtopic-body` (e.g., `2-1-5`). Topics and subtopics are simple integers so you can choose different streams for different elevator subsystems.

## Troubleshooting

- If a client prints `Please launch the Command Center first.`, start `TestCommandCenterMain` before running processors.
- Port `9999` must be free on localhost; change `SoftwareBusInternalNetwork.port` if needed.
