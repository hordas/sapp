package udatraining.dyomin.com.udaproj1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

public class CompassView extends View {

    private static final double CONSTANT_ANGLE = -90;
    private static final double ANGLE_DEGREES = 15;
    private double radius;
    private double letterRadius;
    private double shortRadius;
    private double mainAngleRadians;
    private double invertedMainAngle;
    private double leftAngleRadians;
    private double rightAngleRadians;
    private Paint p;

    private float angle;
    private float speed;

    private float fullWidth;
    private float fullHeight;
    private float centerX;
    private float centerY;
    private float letterCenterX;
    private float letterCenterY;
    private float arrowThickness;
    private float circleThickness;
    private float letterShift;

    private float textSize;
    private CompassPoint[] letterPoints;
    private static final String[] cardinalDirections = { "N", "E", "S", "W" };

    public CompassView(Context context) {
        super(context);
        init();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context, attrs);
        init();
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readAttributes(context, attrs);
        init();
    }

    private void readAttributes(Context c, AttributeSet attrs) {
        TypedArray a = c.getTheme().obtainStyledAttributes(attrs, R.styleable.CompassView, 0, 0);
        try {
            setAngle(a.getInteger(R.styleable.CompassView_direction, 0), 0);
        } finally {
            a.recycle();
        }
    }

    public void init() {
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.setFocusable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        p.setColor(getResources().getColor(R.color.grey_700));
        p.setStyle(Paint.Style.STROKE);
        updateValues();
        p.setStrokeWidth(arrowThickness);

        float xTail = (float) (centerX + radius * Math.cos(mainAngleRadians));
        float yTail = (float) (centerY + radius * Math.sin(mainAngleRadians));
        float xTip = (float) (centerX + radius * Math.cos(invertedMainAngle));
        float yTip = (float) (centerY + radius * Math.sin(invertedMainAngle));
        float xLeft = (float) (xTip + shortRadius * Math.cos(leftAngleRadians));
        float yLeft = (float) (yTip + shortRadius * Math.sin(leftAngleRadians));
        float xRight = (float) (xTip + shortRadius * Math.cos(rightAngleRadians));
        float yRight = (float) (yTip + shortRadius * Math.sin(rightAngleRadians));
        canvas.drawLine(xTip, yTip, xTail, yTail, p);
        canvas.drawLine(xTip, yTip, xLeft, yLeft, p);
        canvas.drawLine(xTip, yTip, xRight, yRight, p);

        p.setStrokeWidth(circleThickness);
        canvas.drawCircle(centerX, centerY, (float) radius, p);

        p.setStyle(Paint.Style.FILL);
        p.setTextSize(textSize);
        for (int i = 0; i < cardinalDirections.length; i ++) {
            canvas.drawText(cardinalDirections[i], letterPoints[i].getX(), letterPoints[i].getY(), p);
        }

    }

    private void updateValues() {
        fullWidth = getMeasuredWidth();
        fullHeight = getMeasuredHeight();
        centerX = fullWidth / 2;
        centerY = fullHeight / 2;
        radius = fullWidth * 0.35;
        letterRadius = fullWidth * 0.43;
        shortRadius = radius * 0.4;
        arrowThickness = fullWidth * 0.02f;
        circleThickness = fullWidth * 0.01f;

        textSize = fullWidth * 0.1f;
        letterShift = textSize / 2.8f;
        letterCenterX = centerX - letterShift;
        letterCenterY = centerY + letterShift;

        letterPoints = new CompassPoint[4];

        for (int i = 0; i < 4; i++) {
            double letterAngleRadians = Math.toRadians(CONSTANT_ANGLE + CONSTANT_ANGLE * (- i));
            int x = (int) (letterCenterX + letterRadius * Math.cos(letterAngleRadians));
            int y = (int) (letterCenterY + letterRadius * Math.sin(letterAngleRadians));
            letterPoints[i] = new CompassPoint(x, y);
        }
    }

    public void setAngle(float angle, float windSpeed) {
        this.angle = angle;
        this.speed = windSpeed;

        this.mainAngleRadians = Math.toRadians(angle + CONSTANT_ANGLE);
        this.invertedMainAngle = Math.toRadians(angle + CONSTANT_ANGLE + 180);
        this.leftAngleRadians = Math.toRadians(angle + ANGLE_DEGREES + CONSTANT_ANGLE);
        this.rightAngleRadians = Math.toRadians(angle - ANGLE_DEGREES + CONSTANT_ANGLE);

        AccessibilityManager am = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (am.isEnabled()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
        invalidate();
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add(Utility.getFormattedWind(getContext(), this.speed, this.angle));
        return true;
    }

    private class CompassPoint {

        private float x;
        private float y;

        CompassPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }
}
