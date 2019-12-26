package hat.bemo.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by apple on 2017/11/10.
 */

public class Delete {

    private Create_Table dbtc = null;
    private SQLiteDatabase db = null;

//    public int Delete_0x01(Context context, String Itemno){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        int delete = db.delete(TABLE_0x01_Insert.TABLE_NAME, TABLE_0x01_Insert.ITEMNO + "=" + Itemno,null);
//        db.close();
//        return delete;
//    }
//
//    public int Delete_0x29(Context context, String Itemno){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        int delete = db.delete(TABLE_0x29_Insert.TABLE_NAME, TABLE_0x29_Insert.ITEMNO + "=" + Itemno, null);
//        db.close();
//        return delete;
//    }

    public int Delete_0x30(Context context, String Itemno){
        dbtc = new Create_Table(context);
        db = dbtc.getWritableDatabase();
        int delete = db.delete(TABLE_0x30.TABLE_NAME, TABLE_0x30.ITEMNO + "=" + Itemno, null);
        db.close();
        return delete;
    }

//    public int Delete_801603(Context context, String missing_person_no){
//        dbtc = new Create_Table(context);
//        db = dbtc.getWritableDatabase();
//        int delete = db.delete(TABLE_801603.TABLE_NAME, TABLE_801603.missing_person_no + "=" + missing_person_no, null);
//        db.close();
//        return delete;
//    }

}
