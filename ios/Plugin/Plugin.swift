import Foundation
import Capacitor

@objc(KeychainUUID)
public class KeychainUUID: CAPPlugin {
    static var key = "com.jason-z.test.uuid";
    
    @objc func deleteDeviceID(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? "";
        KeychainHelper.delete(service: value);
        call.success([
            "status": true
        ])
    }
    
    @objc func getDeviceID(_ call: CAPPluginCall) {
        KeychainUUID.key = call.getString("value") ?? KeychainUUID.key;
        // read cache
        var deviceID = KeychainUUID.getUUIDString();
        if deviceID != nil {
            call.success([
                "status": true,
                "deviceID": deviceID
            ]);
        } else {
            deviceID = KeychainUUID.randomUUID().lowercased();
            KeychainUUID.setUUIDString(deviceID);
            call.success([
                "status": true,
                "deviceID": deviceID
            ]);
        }
    }

    class func randomUUID() -> String{
        if NSClassFromString("NSUUID") != nil {
            return UUID().uuidString;
        }
        let uuidRef = CFUUIDCreate(kCFAllocatorDefault)
        let cfuuid = CFUUIDCreateString(kCFAllocatorDefault, uuidRef)
        let uuid = cfuuid! as String
        return uuid;
    }

    class func getUUIDString() -> String? {
        let uuidStr = KeychainHelper.load(service: key)
        if (uuidStr != nil) {
            return uuidStr as? String
        } else {
            return nil
        }
    }

    class func setUUIDString(_ secValue: String?) -> Bool {
        if secValue != nil {
            KeychainHelper.save(service: key, data: secValue)
            return true
        } else {
            return false
        }
    }
    
}
