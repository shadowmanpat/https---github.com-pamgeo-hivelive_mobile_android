package gr.extract.hivelive.hiveUtilities;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import static android.support.v7.widget.RecyclerView.*;

public class DragController implements OnItemTouchListener {
    private RecyclerView recyclerViewTop, recyclerViewBottom;
    private SimpleDraweeView overlay;
    private final GestureDetectorCompat gestureDetector;
    private boolean isFirst = true;
    private static final int ANIMATION_DURATION = 100;
    private int draggingItem = -1;
    private float startY = 0f;
    private Rect startBounds = null;

    private boolean isDragging = false;

    public DragController(RecyclerView recyclerViewTop,RecyclerView recyclerViewBottom,  SimpleDraweeView overlay) {
        this.recyclerViewTop = recyclerViewTop;
        this.recyclerViewBottom = recyclerViewBottom;
        this.overlay = overlay;
        GestureDetector.SimpleOnGestureListener longClickGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                isDragging = true;
                dragStart(e.getX(), e.getY());
            }
        };
        this.gestureDetector = new GestureDetectorCompat(recyclerViewTop.getContext(), longClickGestureListener);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (isDragging) {
            return true;
        }
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        View view = recyclerViewBottom.findChildViewUnder(x, y);
        if (e.getAction() == MotionEvent.ACTION_UP) {
            dragEnd(view);
            isDragging = false;
        } else {
            drag(y, view);
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private void dragStart(float x, float y) {
        View draggingView = recyclerViewTop.findChildViewUnder(x, y);
        View first = recyclerViewTop.getChildAt(0);
        isFirst = draggingView == first;
        startY = (y - draggingView.getTop());
        paintViewToOverlay(draggingView);
        overlay.setTranslationY(y - startY);
        draggingView.setVisibility(View.INVISIBLE);
        draggingItem = recyclerViewTop.indexOfChild(draggingView);
        startBounds = new Rect(draggingView.getLeft(), draggingView.getTop(), draggingView.getRight(), draggingView.getBottom());
    }

    private void drag(int y, View view) {
        overlay.setTranslationY(y - startY);
    }

    private void dragEnd(View view) {
        overlay.setImageBitmap(null);
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(overlay.getTranslationY() - view.getTop());
        view.animate().translationY(0f).setDuration(ANIMATION_DURATION).start();
    }

    private void paintViewToOverlay(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        overlay.setImageBitmap(bitmap);
        overlay.setTop(0);
    }
}
