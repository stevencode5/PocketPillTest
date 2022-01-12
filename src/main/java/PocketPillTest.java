/*

There are cars moving on an infinite wide road. There is a central traffic light.
Whenever it turns red, all the cars on the road should stop.
As soon as the traffic light turns green, all cars should start moving.  The traffic light is supposed to turn red and green alternatively every 60 seconds.
Write a quick code that simulates the above system.
What we are looking for here is how build different entities in the system and how they interact with each other.
You can decide to use any language or framework. Syntax is not required to be exactly correct, it just needs to be logically correct.

*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PocketPillTest {

    public static void main(String[] args) throws InterruptedException {

        int TRAFFIC_LIGHT_ROUNDS = 5; // Times that the traffic light is going to change
        int WAITING_SECONDS = 5; // Seconds to wait before change traffic light

        // Create traffic light
        TrafficLight trafficLight = new TrafficLight();

        // Create cars
        Car tesla = new Car("Tesla");
        Car toyota = new Car("Toyota");
        Car ford = new Car("Ford");

        // Subscribe cars to traffic light changes
        subscribeToAllTrafficLightEvents(trafficLight, tesla);
        subscribeToAllTrafficLightEvents(trafficLight, toyota);
        subscribeToAllTrafficLightEvents(trafficLight, ford);

        // Execute rounds
        for (int i = 0; i < TRAFFIC_LIGHT_ROUNDS; i++) {
            trafficLight.switchLight();
            Thread.sleep(WAITING_SECONDS * 1000);
            System.out.println("---------------");
        }

    }

    private static void subscribeToAllTrafficLightEvents(TrafficLight trafficLight, Car car) {
        trafficLight.getEventManager().subscribe(EventType.TURN_TO_GREEN_LIGHT, car);
        trafficLight.getEventManager().subscribe(EventType.TURN_TO_RED_LIGHT, car);
    }

}

class TrafficLightEventManager {

    private Map<EventType, List<EventListener>> listeners;

    public TrafficLightEventManager() {
        this.listeners = new HashMap<>();
        for (EventType eventType : EventType.values()) {
            this.listeners.put(eventType, new ArrayList<>());
        }
    }

    // Subscribe a listener to an event
    public void subscribe(EventType eventType, EventListener listener) {
        this.listeners.get(eventType).add(listener);
    }

    public void unsubscribe(EventType eventType, EventListener listener) {
        this.listeners.get(eventType).remove(listener);
    }

    // Notify all listeners about a new event
    public void notify(EventType eventType) {
        listeners.get(eventType).forEach(listener -> listener.update(eventType));
    }

}

enum EventType {
    TURN_TO_RED_LIGHT,
    TURN_TO_GREEN_LIGHT;
}

enum TrafficLightStatus {
    GREEN,
    YELLOW,
    RED;
}

interface EventListener {
    void update(EventType eventType);
}

class Car implements EventListener {

    private String id;
    private boolean isMoving;

    Car(String id) {
        this.id = id;
        this.isMoving = false;
    }

    // Car executes this method everytime a new event is notified
    public void update(EventType eventType) {
        switch (eventType) {
            case TURN_TO_GREEN_LIGHT:
                run();
                break;
            case TURN_TO_RED_LIGHT:
                stop();
                break;
        }
    }

    public void stop() {
        System.out.println("Car " + this.id + " Stopped");
        this.isMoving = false;
    }

    public void run() {
        System.out.println("Car " + this.id + " Started running");
        this.isMoving = true;
    }

}

class TrafficLight {

    private TrafficLightEventManager eventManager;
    private TrafficLightStatus status;

    TrafficLight() {
        this.status = TrafficLightStatus.RED;
        this.eventManager = new TrafficLightEventManager();
    }

    // Switches traffic light status between GREEN and RED
    public void switchLight() {
        if (this.status.equals(TrafficLightStatus.GREEN)) {
            changeToRed();
        } else {
            changeToGreen();
        }
    }

    public void changeToGreen() {
        System.out.println("TrafficLight turned to GREEN");
        this.status = TrafficLightStatus.GREEN;
        this.eventManager.notify(EventType.TURN_TO_GREEN_LIGHT);
    }

    public void changeToRed() {
        System.out.println("TrafficLight turned to RED");
        this.status = TrafficLightStatus.RED;
        this.eventManager.notify(EventType.TURN_TO_RED_LIGHT);
    }

    public TrafficLightEventManager getEventManager() {
        return eventManager;
    }
}
