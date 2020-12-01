import { WebPlugin } from '@capacitor/core';
import { KeychainUUIDPlugin } from './definitions';

export class KeychainUUIDWeb extends WebPlugin implements KeychainUUIDPlugin {
  constructor() {
    super({
      name: 'KeychainUUID',
      platforms: ['web'],
    });
  }

  async getDeviceID(options: { value: string }): Promise<any> {
    console.log('getDeviceID', options);
    return options;
  }
  async deleteDeviceID(options: { value: string }): Promise<any> {
    console.log('deleteDeviceID', options);
    return options;
  }
}

const KeychainUUID = new KeychainUUIDWeb();

export { KeychainUUID };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(KeychainUUID);
