package com.tc.keyboard;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jay.easykeyboard.action.KeyBoardActionListener;

public class SystemOnKeyboardActionListenerV2 implements KeyboardView.OnKeyboardActionListener {
    private EditText editText;
    private PopupWindow popupWindow;
    private KeyBoardActionListener mActionListener;

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public void setPopupWindow(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
    }

    public void setKeyActionListener(KeyBoardActionListener listener){
        this.mActionListener = listener;
    }

    @Override
    public void onPress(int primaryCode) {
        _onKey(primaryCode, null);
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        /*
        String keys = primaryCode + " : ";
        if (keyCodes != null) {
            for (int code : keyCodes) {
                keys += code + ", ";
            }
        }
        Toast.makeText(editText.getContext(), keys, Toast.LENGTH_SHORT).show();
         */
    }

    private void _onKey(int primaryCode, int[] keyCodes) {
        if (null != editText) {
            Editable editable = editText.getText();
            int start = editText.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_DONE) { // 隐藏键盘
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }

                if (!next(editText.getNextFocusForwardId())
                        && !next(editText.getNextFocusRightId())
                        && !next(editText.getNextFocusDownId()))
                    mActionListener.onComplete();
            } else if (primaryCode == -1) { // 上一步
                last();
            }
            else if (primaryCode == Keyboard.KEYCODE_DELETE) { // 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
                mActionListener.onClear();
            } else if (primaryCode == 152) {
                editable.clear();
                mActionListener.onClearAll();
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
                mActionListener.onTextChange(editable);
            }
        }
    }

    private void last() {
        if (!next(editText.getNextFocusLeftId())
            && !next(editText.getNextFocusUpId()))
            popupWindow.dismiss();
    }

    private boolean next(int nextFocusId) {
        View viewById = editText.getRootView().findViewById(nextFocusId);
        if (viewById == null) return false;

        editText.clearFocus();
        viewById.requestFocus();
        return true;
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeUp() {
    }
}
