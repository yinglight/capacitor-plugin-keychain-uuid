declare module '@capacitor/core' {
  interface PluginRegistry {
    KeychainUUID: KeychainUUIDPlugin;
  }
}

export interface KeychainUUIDPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
