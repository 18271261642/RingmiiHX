package hat.bemo;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    private static final String TAG = "Tools";

    public static class KeyboardUtils {

        public static void showKeyboard(View view) {
            InputMethodManager imm = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                view.requestFocus();
                imm.showSoftInput(view, 0);
            }
        }

        public static void hideKeyboard(View view) {
            InputMethodManager imm = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        public static void toggleSoftInput(View view) {
            InputMethodManager imm = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(0, 0);
            }
        }
    }


    public static String atos(String[] mStringa) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mStringa.length; i++) {
            sb.append(mStringa[i]).append(",");
        }

        return sb.toString();
    }

    public static String[] stoa(String mString) {

        return mString.split("/");
    }


    public static String replaceBlank(String src) {
        String dest = "";
        if (src != null) {
            Pattern pattern = Pattern.compile("\t|\r|\n|\\s*");
            Matcher matcher = pattern.matcher(src);
            dest = matcher.replaceAll("");
        }
        return dest;
    }

    /*
     * 正确
     */
    public static void removeListSome(List<String> list, String target) {
        Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
            String item = iter.next();
            if (item.equals(target)) {
                iter.remove();
            }
        }
    }

}
