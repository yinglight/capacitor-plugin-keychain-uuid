package com.valley.keychain.uuid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONArray;

import java.util.List;
import java.util.UUID;

@NativePlugin(
        permissions = {
                Manifest.permission.READ_PHONE_STATE
        }
)
public class KeychainUUID extends Plugin {
    private static final String LOG_TAG = "KeychainUUID";
    private static final int REQUEST_IMAGE_CAPTURE = 2002;

    @PluginMethod
    public void getDeviceID(PluginCall call) {
        Context context = getContext();

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        // dual SIM detection with SubscriptionManager API
        // requires API 22
        // requires permission READ_PHONE_STATE
        JSONArray sims = null;
        Integer phoneCount = null;
        Integer activeSubscriptionInfoCount = null;
        Integer activeSubscriptionInfoCountMax = null;

        try {
            // TelephonyManager.getPhoneCount() requires API 23
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                phoneCount = manager.getPhoneCount();
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {

                if (checkPermission()) {

                    SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                    activeSubscriptionInfoCount = subscriptionManager.getActiveSubscriptionInfoCount();
                    activeSubscriptionInfoCountMax = subscriptionManager.getActiveSubscriptionInfoCountMax();

                    sims = new JSONArray();

                    List<SubscriptionInfo> subscriptionInfos = subscriptionManager.getActiveSubscriptionInfoList();
                    for (SubscriptionInfo subscriptionInfo : subscriptionInfos) {

                        CharSequence carrierName = subscriptionInfo.getCarrierName();
                        String countryIso = subscriptionInfo.getCountryIso();
                        int dataRoaming = subscriptionInfo.getDataRoaming();  // 1 is enabled ; 0 is disabled
                        CharSequence displayName = subscriptionInfo.getDisplayName();
                        String iccId = subscriptionInfo.getIccId();
                        int mcc = subscriptionInfo.getMcc();
                        int mnc = subscriptionInfo.getMnc();
                        String number = subscriptionInfo.getNumber();
                        int simSlotIndex = subscriptionInfo.getSimSlotIndex();
                        int subscriptionId = subscriptionInfo.getSubscriptionId();

                        boolean networkRoaming = subscriptionManager.isNetworkRoaming(simSlotIndex);

                        String deviceId = null;
                        // TelephonyManager.getDeviceId(slotId) requires API 23
                        if (Build.VERSION.SDK_INT >= 29) {
                            deviceId = Settings.Secure.getString(context.getContentResolver(),
                                    Settings.Secure.ANDROID_ID);
                        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            deviceId = manager.getDeviceId(simSlotIndex);
                        }

                        JSObject simData = new JSObject();

                        simData.put("carrierName", carrierName.toString());
                        simData.put("displayName", displayName.toString());
                        simData.put("countryCode", countryIso);
                        simData.put("mcc", mcc);
                        simData.put("mnc", mnc);
                        simData.put("isNetworkRoaming", networkRoaming);
                        simData.put("isDataRoaming", (dataRoaming == 1));
                        simData.put("simSlotIndex", simSlotIndex);
                        simData.put("phoneNumber", number);
                        if (deviceId != null) {
                            simData.put("deviceId", deviceId);
                        }
                        simData.put("simSerialNumber", iccId);
                        simData.put("subscriptionId", subscriptionId);

                        sims.put(simData);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String phoneNumber = null;
        String countryCode = manager.getSimCountryIso();
        String simOperator = manager.getSimOperator();
        String carrierName = manager.getSimOperatorName();

        String deviceId = null;
        String deviceSoftwareVersion = null;
        String simSerialNumber = null;
        String subscriberId = null;

        int callState = manager.getCallState();
        int dataActivity = manager.getDataActivity();
        int networkType = manager.getNetworkType();
        int phoneType = manager.getPhoneType();
        int simState = manager.getSimState();

        boolean isNetworkRoaming = manager.isNetworkRoaming();

        if (checkPermission()) {
            phoneNumber = manager.getLine1Number();
            if (Build.VERSION.SDK_INT >= 29) {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                deviceId = manager.getDeviceId();
                simSerialNumber = manager.getSimSerialNumber();
                subscriberId = manager.getSubscriberId();
            }
            deviceSoftwareVersion = manager.getDeviceSoftwareVersion();
        }

        String mcc = "";
        String mnc = "";

        if (simOperator.length() >= 3) {
            mcc = simOperator.substring(0, 3);
            mnc = simOperator.substring(3);
        }

        JSObject result = new JSObject();

        result.put("carrierName", carrierName);
        result.put("countryCode", countryCode);
        result.put("mcc", mcc);
        result.put("mnc", mnc);

        result.put("callState", callState);
        result.put("dataActivity", dataActivity);
        result.put("networkType", networkType);
        result.put("phoneType", phoneType);
        result.put("simState", simState);

        result.put("isNetworkRoaming", isNetworkRoaming);

        if (phoneCount != null) {
            result.put("phoneCount", (int)phoneCount);
        }
        if (activeSubscriptionInfoCount != null) {
            result.put("activeSubscriptionInfoCount", (int)activeSubscriptionInfoCount);
        }
        if (activeSubscriptionInfoCountMax != null) {
            result.put("activeSubscriptionInfoCountMax", (int)activeSubscriptionInfoCountMax);
        }

        if (sims != null && sims.length() != 0) {
            result.put("cards", sims);
        }

        if (checkPermission()) {
            result.put("phoneNumber", phoneNumber);
            result.put("deviceId", deviceId);
            result.put("deviceSoftwareVersion", deviceSoftwareVersion);
            result.put("simSerialNumber", simSerialNumber);
            result.put("subscriberId", subscriberId);
            call.success(result);
        } else {
            call.reject("没有读取电话权限", "500");
        }
    }

    @PluginMethod
    public void deleteDeviceID(PluginCall call) {
        JSObject status= new JSObject();
        status.put("status", true);
        call.success(status);
    }

    private boolean checkPermission()
    {
        String permission = Manifest.permission.READ_PHONE_STATE;
        int res = getContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * 获取设备唯一标识符，暂时不要用。
     */
    private String getDeviceId() {
        String serialNumber = "35" + Build.BOARD.length() % 10
                + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
                + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10
                + Build.HOST.length() % 10 + Build.ID.length() % 10
                + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
                + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10
                + Build.TYPE.length() % 10 + Build.USER.length() % 10;
        return new UUID(serialNumber.hashCode(),serialNumber.hashCode()).toString();
    }
}
