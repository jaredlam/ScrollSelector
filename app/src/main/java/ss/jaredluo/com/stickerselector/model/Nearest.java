package ss.jaredluo.com.stickerselector.model;

/**
 * Created by admin on 2017/7/6.
 */

public class Nearest {
    private int nearestPosition;
    private float nearestOffset;

    public Nearest(int nearestPosition, float nearestOffset) {
        this.nearestPosition = nearestPosition;
        this.nearestOffset = nearestOffset;
    }

    public int getNearestPosition() {
        return nearestPosition;
    }

    public void setNearestPosition(int nearestPosition) {
        this.nearestPosition = nearestPosition;
    }

    public float getNearestOffset() {
        return nearestOffset;
    }

    public void setNearestOffset(float nearestOffset) {
        this.nearestOffset = nearestOffset;
    }
}
