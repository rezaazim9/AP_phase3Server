package model.movement;

import java.util.concurrent.CopyOnWriteArrayList;

public interface Movable {
    CopyOnWriteArrayList<Movable> movables = new CopyOnWriteArrayList<>();
    long getPositionUpdateTimeDiffCapture();

    void setPositionUpdateTimeDiffCapture(long time);

    long getLastPositionUpdateTime();

    void setLastPositionUpdateTime(long time);
}
