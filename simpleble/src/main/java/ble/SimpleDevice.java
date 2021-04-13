package ble;

import android.os.Parcel;
import android.os.Parcelable;

import com.inuker.bluetooth.library1.old.model.BleGattProfile;
import com.inuker.bluetooth.library1.old.search.SearchResult;

import java.util.HashMap;
import java.util.UUID;


public class SimpleDevice implements Parcelable {

    public SearchResult deviceInfo;

    public BleGattProfile gattProfile;

    protected String name;

    protected String mac;

    protected int rssi;

    public boolean isBLE = true; // 是否是BLE蓝牙 默认是BLE蓝牙

    /**
     * 服务UUID和特征值UUID
     * 可以在子类中进行赋值
     */
    protected UUID serviceUUID;
    protected HashMap<UUID, UUID[]> characteristicUUID;

    public SimpleDevice(SearchResult searchInfo) {
        this.deviceInfo = searchInfo;
        name = searchInfo.getName();
        mac = searchInfo.getAddress();
        rssi = searchInfo.rssi;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public int getRssi() {
        return rssi;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleDevice)) {
            return false;
        }
        SimpleDevice d = (SimpleDevice) o;
        return this.name.equals(d.name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.deviceInfo, flags);
        dest.writeParcelable(this.gattProfile, flags);
        dest.writeString(this.name);
        dest.writeString(this.mac);
        dest.writeInt(this.rssi);
        dest.writeSerializable(this.serviceUUID);
        dest.writeSerializable(this.characteristicUUID);
    }

    protected SimpleDevice(Parcel in) {
        this.deviceInfo = in.readParcelable(SearchResult.class.getClassLoader());
        this.gattProfile = in.readParcelable(BleGattProfile.class.getClassLoader());
        this.name = in.readString();
        this.mac = in.readString();
        this.rssi = in.readInt();
        this.serviceUUID = (UUID) in.readSerializable();
        this.characteristicUUID = (HashMap<UUID, UUID[]>) in.readSerializable();
    }

    public static final Creator<SimpleDevice> CREATOR = new Creator<SimpleDevice>() {
        @Override
        public SimpleDevice createFromParcel(Parcel source) {
            return new SimpleDevice(source);
        }

        @Override
        public SimpleDevice[] newArray(int size) {
            return new SimpleDevice[size];
        }
    };
}
