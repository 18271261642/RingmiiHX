package com.guider.baselib.utils.permissions;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.permissions
 * email：893855882@qq.com
 * data：2017/5/31
 */

public class Permission {
    public final String name;
    public final boolean granted;
    public final boolean shouldShowRequestPermissionRationale;

    Permission(String name, boolean granted) {
        this(name, granted, false);
    }

    Permission(String name, boolean granted, boolean shouldShowRequestPermissionRationale) {
        this.name = name;
        this.granted = granted;
        this.shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale;
    }

    Permission(List<Permission> permissions) {
        this.name = this.combineName(permissions);
        this.granted = this.combineGranted(permissions);
        this.shouldShowRequestPermissionRationale = this.combineShouldShowRequestPermissionRationale(permissions);
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Permission that = (Permission)o;
            if (this.granted != that.granted) {
                return false;
            } else {
                return this.shouldShowRequestPermissionRationale == that.shouldShowRequestPermissionRationale && this.name.equals(that.name);
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (granted ? 1 : 0);
        result = 31 * result + (shouldShowRequestPermissionRationale ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", granted=" + granted +
                ", shouldShowRequestPermissionRationale=" + shouldShowRequestPermissionRationale +
                '}';
    }

    private String combineName(List<Permission> permissions) {
        return ((StringBuilder) Observable.fromIterable(permissions)
                .map(permission -> permission.name).collectInto(new StringBuilder(), (s, s2) -> {
            if (s.length() == 0) {
                s.append(s2);
            } else {
                s.append(", ").append(s2);
            }

        }).blockingGet()).toString();
    }

    private Boolean combineGranted(List<Permission> permissions) {
        return (Boolean)Observable.fromIterable(permissions)
                .all(permission -> permission.granted).blockingGet();
    }

    private Boolean combineShouldShowRequestPermissionRationale(List<Permission> permissions) {
        return (Boolean)Observable.fromIterable(permissions)
                .any(permission -> permission.shouldShowRequestPermissionRationale).blockingGet();
    }
}
