package com.example.planetzeapp;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class PieChartView extends View {

    private Paint paint;
    private Paint textPaint;
    private ArrayList<Float> values;
    private ArrayList<String> labels;

    // Predefined theme colors: shades of blue, green, and purple
    private final ArrayList<Integer> colors;

    private RectF rectF;
    private float animationProgress = 0f; // Controls the animation progress
    private int padding = 50; // Padding from edges in pixels

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize paint for pie slices
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Initialize paint for text
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Initialize empty data arrays
        values = new ArrayList<>();
        labels = new ArrayList<>();

        // Define theme-specific colors: shades of blue, green, and purple
        colors = new ArrayList<>();
        colors.add(Color.rgb(51, 153, 255)); // Light Blue
        colors.add(Color.rgb(0, 204, 153)); // Aquamarine Green
        colors.add(Color.rgb(102, 102, 255)); // Soft Blue
        colors.add(Color.rgb(102, 204, 102)); // Lime Green
        colors.add(Color.rgb(153, 102, 255)); // Medium Purple
        colors.add(Color.rgb(0, 102, 204)); // Deep Blue
        colors.add(Color.rgb(102, 255, 204)); // Mint Green
        colors.add(Color.rgb(153, 51, 255)); // Vibrant Purple
    }

    /**
     * Sets the data for the pie chart and starts the animation.
     *
     * @param values ArrayList of slice values (e.g., percentages or weights).
     * @param labels ArrayList of labels corresponding to each value.
     */
    public void setData(ArrayList<Float> values, ArrayList<String> labels) {
        if (values == null || labels == null) {
            throw new IllegalArgumentException("Values and labels cannot be null");
        }
        if (values.size() != labels.size()) {
            throw new IllegalArgumentException("Values and labels must have the same size");
        }

        this.values = values;
        this.labels = labels;

        if (values.size() > colors.size()) {
            throw new IllegalArgumentException("Not enough predefined colors for the provided data.");
        }

        // Start the animation
        startAnimation();

        invalidate(); // Request a redraw of the view
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1500); // Duration of the animation (1.5 seconds)
        animator.addUpdateListener(animation -> {
            animationProgress = (float) animation.getAnimatedValue();
            invalidate(); // Redraw the chart as the animation progresses
        });
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Get the total height of the screen
        int totalHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Set the height to be half the screen height minus padding
        int desiredHeight = totalHeight / 2 - padding * 2;

        // Set dimensions
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = resolveSize(desiredHeight, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Handle the case where no data is provided
        if (values == null || values.isEmpty()) {
            drawPlaceholderMessage(canvas, "No data to display");
            return;
        }

        // Calculate total value
        float total = 0;
        for (Float value : values) {
            total += value;
        }
        if (total == 0) {
            drawPlaceholderMessage(canvas, "No data to display");
            return;
        }

        // Define chart dimensions
        rectF = new RectF(padding, padding, getWidth() - padding, getHeight() - padding);

        // Draw each slice of the pie chart with animation
        float startAngle = 0;
        for (int i = 0; i < values.size(); i++) {
            // Calculate the sweep angle for the current slice
            float sweepAngle = (values.get(i) / total) * 360;

            // Scale the sweep angle based on the animation progress
            sweepAngle *= animationProgress;

            // Set the paint color for the current slice
            paint.setColor(colors.get(i));
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

            // Calculate the position for the label
            if (animationProgress == 1f) { // Draw labels only after the animation is complete
                float angle = startAngle + sweepAngle / 2;
                float labelX = (float) (getWidth() / 2 + (rectF.width() / 2.5) * Math.cos(Math.toRadians(angle)));
                float labelY = (float) (getHeight() / 2 + (rectF.height() / 2.5) * Math.sin(Math.toRadians(angle)));

                // Draw the label
                textPaint.setColor(Color.BLACK);
                canvas.drawText(labels.get(i), labelX, labelY, textPaint);
            }

            // Move to the next slice
            startAngle += sweepAngle;
        }
    }

    /**
     * Draws a placeholder message when no data is available.
     *
     * @param canvas The canvas on which to draw the message.
     * @param message The message to display.
     */
    private void drawPlaceholderMessage(Canvas canvas, String message) {
        textPaint.setTextSize(50);
        canvas.drawText(message, getWidth() / 2f, getHeight() / 2f, textPaint);
    }
}