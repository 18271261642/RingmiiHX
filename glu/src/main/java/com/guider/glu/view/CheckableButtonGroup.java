package com.guider.glu.view;

/**
 * Created by haix on 2019/6/22.
 */

public class CheckableButtonGroup {

    private GLUCheckableButton checkableButtonLeft;
    private GLUCheckableButton checkableButtonRight;

    public void resigterGroupButton(GLUCheckableButton checkableButtonLeft, GLUCheckableButton checkableButtonRight){
        this.checkableButtonLeft =checkableButtonLeft;
        this.checkableButtonRight= checkableButtonRight;
    }

    public void setCheckableButtonStatus(GLUCheckableButton button){

        if (button == null){
            return;
        }
        if (button.getTag().equals(checkableButtonLeft.getTag())){
            if (checkableButtonLeft.isChecked()){
                checkableButtonLeft.setChecked(false);
                checkableButtonRight.setChecked(true);
            }else{
                checkableButtonLeft.setChecked(true);
                checkableButtonRight.setChecked(false);
            }
        }else{
            if (checkableButtonRight.isChecked()){
                checkableButtonRight.setChecked(false);
                checkableButtonLeft.setChecked(true);
            }else{
                checkableButtonRight.setChecked(true);
                checkableButtonLeft.setChecked(false);
            }
        }
    }
}
