package com.hotcast.vr;

import com.hotcast.vr.pageview.ChangeModeListener;
import com.hotcast.vr.pageview.PlayerContralView;

/**
 * Created by joey on 9/7/15.
 */
public class PlayerCtrMnger {
    private PlayerContralView view1, view2;
    public PlayerCtrMnger(PlayerContralView view1, PlayerContralView view2){
        this.view1 = view1;
        this.view2 = view2;
    }

    public void setIsPlaying(boolean b){
        view1.setIsPlaying(b);
        view2.setIsPlaying(b);
    }

//    public void hide(){
//        view1.hide();
//        view2.hide();
//    }

    public void setStatusPlay(){
        view1.setStatusPlay();
        view2.setStatusPlay();
    }

    public void setChangeMode(ChangeModeListener listener){
        view1.setChangeMode(listener);
        view2.setChangeMode(listener);
    }

    public void setmPlayerContrallerInterface(PlayerContrallerInterface listener){
        view1.setmPlayerContrallerInterface(listener);
        view2.setmPlayerContrallerInterface(listener);
    }

    public void setMin(int min){
        view1.setMin(min);
        view2.setMin(min);
    }

    public void setMax(int max){
        view1.setMax(max);
        view2.setMax(max);
    }

    public void setTotalDuration(int total){
        view1.setTotalDuration(total);
        view2.setTotalDuration(total);
    }

    public void setCurTime(int cur){
        view1.setCurTime(cur);
        view2.setCurTime(cur);
    }
}
