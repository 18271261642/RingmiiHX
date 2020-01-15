package hat.bemo.measure.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

public class MeasureBLEDAO
{
  private MeasureCreateTable dbtc = null;
  private SQLiteDatabase db = null;
  
  public ArrayList<VOMeasureDevice> getMeasureDevice(Context context, String deviceNameValue)
  {
    this.dbtc = new MeasureCreateTable(context);
    this.db = this.dbtc.getWritableDatabase();
    Cursor c = null;
    String sql = null;
    if (deviceNameValue == null) {
      sql = "SELECT * FROM measuredevice";
    } else {
      sql = "SELECT * FROM measuredevice WHERE device = '" + deviceNameValue + "'";
    }
    try
    {
      c = this.db.rawQuery(sql, null);
      if (c.moveToFirst())
      {
        ArrayList<VOMeasureDevice> list = new ArrayList();
        do
        {
          VOMeasureDevice data = new VOMeasureDevice();
          data.setDevice(c.getString(c.getColumnIndex("device")));
          data.setCreateDate(c.getString(c.getColumnIndex("createDate")));
          data.setItemno(c.getString(c.getColumnIndex("itemno")));
          list.add(data);
        } while (c.moveToNext());
        return list;
      }
      return null;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
    finally
    {
      if (c != null) {
        c.close();
      }
      this.db.close();
    }
  }
  
  public ArrayList<VOMeasureRecord> getMeasureRecord(Context context)
  {
    this.dbtc = new MeasureCreateTable(context);
    this.db = this.dbtc.getWritableDatabase();
    Cursor c = null;
    String sql = null;
    
    sql = "SELECT * FROM measurerecord ORDER BY itemno desc LIMIT 0,10 ";
    try
    {
      c = this.db.rawQuery(sql, null);
      if (c.moveToFirst())
      {
        ArrayList<VOMeasureRecord> list = new ArrayList();
        do
        {
          VOMeasureRecord data = new VOMeasureRecord();
          data.setMeasureType(c.getString(c.getColumnIndex("measureType")));
          data.setCreateDate(c.getString(c.getColumnIndex("createDate")));
          data.setItemno(c.getString(c.getColumnIndex("itemno")));
          data.setValue(c.getString(c.getColumnIndex("value")));
          list.add(data);
        } while (c.moveToNext());
        return list;
      }
      return null;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
    finally
    {
      if (c != null) {
        c.close();
      }
      this.db.close();
    }
  }
}
