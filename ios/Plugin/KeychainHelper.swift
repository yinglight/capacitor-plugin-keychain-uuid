//
//  KeychainHelper.swift
//  Plugin
//
//  Created by zhaoyz on 2020/11/30.
//  Copyright © 2020 Max Lynch. All rights reserved.
//

import Foundation
import Security
public class KeychainHelper {
    
    class func getKeychainQuery(service: String) -> NSMutableDictionary {
        // 创建一个条件字典
        let keychainQuaryMutableDictionary = NSMutableDictionary.init(capacity: 0)
        // 设置条件存储的类型
        keychainQuaryMutableDictionary.setValue(kSecClassGenericPassword, forKey: kSecClass as String)
        // 设置存储数据的标记
        keychainQuaryMutableDictionary.setValue(service, forKey: kSecAttrService as String)
        keychainQuaryMutableDictionary.setValue(service, forKey: kSecAttrAccount as String)
        // 设置数据访问属性
        keychainQuaryMutableDictionary.setValue(kSecAttrAccessibleAfterFirstUnlock, forKey: kSecAttrAccessible as String)
        // 返回创建条件字典
        return keychainQuaryMutableDictionary
    }
    
    class func save(service: String, data:Any) -> Bool {
        // 获取存储数据的条件
        let keyChainSaveMutableDictionary = self.getKeychainQuery(service: service);
        // 删除旧的存储数据
        SecItemDelete(keyChainSaveMutableDictionary)
        // 设置数据
        keyChainSaveMutableDictionary.setValue(NSKeyedArchiver.archivedData(withRootObject: data), forKey: kSecValueData as String)
        // 进行存储数据
        let saveState = SecItemAdd(keyChainSaveMutableDictionary, nil)
        if saveState == noErr  {
            return true
        }
        return false
    }
    
    class func load(service: String) -> Any {
        var idObject:Any?
        // 获取查询条件
        let keyChainReadmutableDictionary = self.getKeychainQuery(service: service);
        // 提供查询数据的两个必要参数
        keyChainReadmutableDictionary.setValue(kCFBooleanTrue, forKey: kSecReturnData as String)
        keyChainReadmutableDictionary.setValue(kSecMatchLimitOne, forKey: kSecMatchLimit as String)
        // 创建获取数据的引用
        var queryResult: AnyObject?
        // 通过查询是否存储在数据
        let readStatus = withUnsafeMutablePointer(to: &queryResult) { SecItemCopyMatching(keyChainReadmutableDictionary, UnsafeMutablePointer($0))}
        if readStatus == errSecSuccess {
            if let data = queryResult as! NSData? {
                idObject = NSKeyedUnarchiver.unarchiveObject(with: data as Data) as Any
            }
        }
        return idObject as Any;
    }
    
    class func delete(service: String) {
        // 获取删除的条件
        let keyChainDeleteMutableDictionary = self.getKeychainQuery(service: service)
        // 删除数据
        SecItemDelete(keyChainDeleteMutableDictionary);
    }
    
}

