package com.inuker.bluetooth.library.old.model;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/11/17.
 */

public class BleGattCharacterOld implements Parcelable {

    private ParcelUuid uuid;

    private int property;

    private int permissions;

    private List<BleGattDescriptor> descriptors;

    protected BleGattCharacterOld(Parcel in) {
        uuid = in.readParcelable(ParcelUuid.class.getClassLoader());
        property = in.readInt();
        permissions = in.readInt();
        descriptors = in.createTypedArrayList(BleGattDescriptor.CREATOR);
    }

    public BleGattCharacterOld(BluetoothGattCharacteristic characteristic) {
        this.uuid = new ParcelUuid(characteristic.getUuid());
        this.property = characteristic.getProperties();
        this.permissions = characteristic.getPermissions();

        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
            getDescriptors().add(new BleGattDescriptor(descriptor));
        }
    }

    public static final Creator<BleGattCharacterOld> CREATOR = new Creator<BleGattCharacterOld>() {
        @Override
        public BleGattCharacterOld createFromParcel(Parcel in) {
            return new BleGattCharacterOld(in);
        }

        @Override
        public BleGattCharacterOld[] newArray(int size) {
            return new BleGattCharacterOld[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uuid, flags);
        dest.writeInt(property);
        dest.writeInt(permissions);
        dest.writeTypedList(descriptors);
    }

    public UUID getUuid() {
        return uuid.getUuid();
    }

    public void setUuid(ParcelUuid uuid) {
        this.uuid = uuid;
    }

    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public List<BleGattDescriptor> getDescriptors() {
        if (descriptors == null) {
            descriptors = new ArrayList<BleGattDescriptor>();
        }
        return descriptors;
    }

    public void setDescriptors(List<BleGattDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    @Override
    public String toString() {
        return "BleGattCharacter{" +
                "uuid=" + uuid +
                ", property=" + property +
                ", permissions=" + permissions +
                ", descriptors=" + descriptors +
                '}';
    }
}
