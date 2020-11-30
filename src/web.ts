import { WebPlugin } from '@capacitor/core';
import { KeychainUUIDPlugin } from './definitions';

export class KeychainUUIDWeb extends WebPlugin implements KeychainUUIDPlugin {
  constructor() {
    super({
      name: 'KeychainUUID',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const KeychainUUID = new KeychainUUIDWeb();

export { KeychainUUID };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(KeychainUUID);
