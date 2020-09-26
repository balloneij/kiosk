package kiosk.scenes;

import java.util.Map;
import kiosk.EventListener;
import kiosk.InputEvent;

public interface Control {

    Map<InputEvent, EventListener> getEventListeners();
}
