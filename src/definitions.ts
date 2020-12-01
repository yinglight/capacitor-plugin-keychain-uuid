declare module '@capacitor/core' {
  interface PluginRegistry {
    KeychainUUID: KeychainUUIDPlugin;
  }
}

export interface KeychainUUIDPlugin {
  getDeviceID(options: { value: string }): Promise<any>;
  deleteDeviceID(options: { value: string }): Promise<any>;
}
