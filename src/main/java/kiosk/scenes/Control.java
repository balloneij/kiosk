package kiosk.scenes;

import java.util.Map;
import kiosk.EventListener;
import kiosk.InputEvent;
import kiosk.TouchScreenEvent;

public interface Control<T, K> {

    Map<InputEvent, EventListener<T>> getEventListeners();

    Map<TouchScreenEvent, EventListener<K>> getTouchEventListeners();
}
