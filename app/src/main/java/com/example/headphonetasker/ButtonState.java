package com.example.headphonetasker;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class ButtonState {

    abstract class MyTimer extends TimerTask {
        MyTimer(int id) {
            this.id = id;
        }
        int id;
    }


    int mLastBtn = 0;
    boolean mTimerActive = false;
    int mClickCnt = 0;
    int mRepeatCnt = 0;
    Timer mTimer = new Timer();

    public void shortClick(int btn) {
        Log.i("BB", "shortClick: " + btn);
    }
    public void doubleClick(int btn) {
        Log.i("BB", "doubleClick: " + btn);
    }
    public void longClick(int btn) {
        Log.i("BB", "longClick: " + btn);
    }
    public void repeatClick(int btn) {
        Log.i("BB", "repeatClick: " + btn);
    }

    private void startTimer(int cnt)
    {
        mTimerActive = true;
        mTimer.schedule(new MyTimer(cnt) {
            @Override
            public void run() {
                //Log.i("BLA", "BLA: " + id + ", cnt: " + cnt);
                //Log.i("BB", "timer expired");
                mTimerActive = false;
                if (mClickCnt == 1 && mRepeatCnt == 0 && mLastBtn == 0) {
                    //Log.i("BB", "ShortClick");
                    shortClick(id);
                }
            }
        }, 400);

    }

    public void handle(int k) {

        if (k == 0) {
            // release
            if (mClickCnt > 1) {
//                Log.i("BB", "double: " + mClickCnt);
                mClickCnt = mRepeatCnt = 0;
                doubleClick(mLastBtn);

            } else if(mRepeatCnt > 0) {
//                Log.i("BB", "long: " + mRepeatCnt);
                mClickCnt = mRepeatCnt = 0;
                longClick(mLastBtn);
            }
        }
        else if (mLastBtn == 0) {

            mRepeatCnt = 0;

            // new press
            if (!mTimerActive) {
                mClickCnt = 1;
                startTimer(k);
            } else {
                mClickCnt++;
            }
        }
        else if (mLastBtn == k) {
            // repeat
            mRepeatCnt++;
            if(mRepeatCnt==1) {
                repeatClick(k);
            }
        }

        mLastBtn = k;
    }
}
