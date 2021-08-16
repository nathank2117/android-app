package com.appisoft.iperkz.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import com.appisoft.perkz.R;

@SuppressLint("AppCompatCustomView")
public class OTPEntryEditText extends AppCompatEditText {

    float mSpace = 24; //24 dp by default
    float mCharSize = 0;
    float mNumChars = 4;
    float mLineSpacing = 8; //8dp by default

    private float mLineStroke = 1; //1dp by default
    private Paint mLinesPaint;
    int[][] mStates = new int[][]{
            new int[]{android.R.attr.state_selected}, // selected
            new int[]{android.R.attr.state_focused}, // focused
            new int[]{-android.R.attr.state_focused}, // unfocused
    };

    int[] mColors = new int[]{
            Color.GREEN,
            Color.BLACK,
            Color.GRAY
    };

    ColorStateList mColorStates = new ColorStateList(mStates, mColors);


    public OTPEntryEditText(Context context) {
        super(context);
    }

    public OTPEntryEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public OTPEntryEditText(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }



    private int getColorForState(int... states) {
        return mColorStates.getColorForState(states, Color.GRAY);
    }

    /* next = is the current char the next character to be input? */
    private void updateColorForLines(boolean next) {
        if (isFocused()) {
            mLinesPaint.setColor(
                    getColorForState(android.R.attr.state_focused));
            if (next) {
                mLinesPaint.setColor(
                        getColorForState(android.R.attr.state_selected));
            }
        } else {
            mLinesPaint.setColor(
                    getColorForState(-android.R.attr.state_focused));
        }
    }


    private void init(Context context, AttributeSet attrs) {
        setBackgroundResource(0);
        float multi = context.getResources().getDisplayMetrics().density;
        mLineStroke = multi * mLineStroke;
        mLinesPaint = new Paint(getPaint());
        mLinesPaint.setStrokeWidth(mLineStroke);
        mSpace = multi * mSpace; //convert to pixels for our density
        mLineSpacing = multi * mLineSpacing; //convert to pixels
       /*
        mMaxLength = attrs.getAttributeIntValue(
                XML_NAMESPACE_ANDROID, "maxLength", 4);
        */
        mNumChars = 4;

        //Disable copy paste
        super.setCustomSelectionActionModeCallback(
                new ActionMode.Callback() {
                    public boolean onPrepareActionMode(ActionMode mode,
                                                       Menu menu) {
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode mode) {
                    }

                    public boolean onCreateActionMode(ActionMode mode,
                                                      Menu menu) {
                        return false;
                    }

                    public boolean onActionItemClicked(ActionMode mode,
                                                       MenuItem item) {
                        return false;
                    }
                });


    }



    @Override
    protected void onDraw(Canvas canvas) {
       // super.onDraw(canvas);
        int availableWidth =
                getWidth() - getPaddingRight() - getPaddingLeft();
        if (mSpace < 0) {
            mCharSize = (availableWidth / (mNumChars * 2 - 1));
        } else {
            mCharSize =
                    (availableWidth - (mSpace * (mNumChars - 1))) / mNumChars;
        }

        int startX = getPaddingLeft();
        int bottom = getHeight() - getPaddingBottom();

        for (int i=0; i< mNumChars; i++) {
            updateColorForLines(i == 4);
            canvas.drawLine(
                    startX, bottom, startX + mCharSize, bottom, mLinesPaint);


            if (getText().length() > i) {
                float middle = startX + mCharSize / 2;
                canvas.drawText(super.getText(),
                        i,
                        i + 1,
                        middle,
                        bottom - mLineSpacing,
                        getPaint());
            }


            if (mSpace < 0) {
                startX += mCharSize * 2;
            } else {
                startX += mCharSize + mSpace;
            }
        }
    }
}
