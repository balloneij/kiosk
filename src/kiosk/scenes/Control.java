package kiosk.scenes;

import java.util.Map;
import kiosk.EventListener;
import kiosk.InputEvent;

public interface Control<T> {

    Map<InputEvent, EventListener<T>> getEventListeners();
}
