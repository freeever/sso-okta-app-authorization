import { InjectionToken } from '@angular/core';
import { SharedLibConfig } from './shared-lib-config.interface';

export const SHARED_LIB_CONFIG = new InjectionToken<SharedLibConfig>('SharedLibConfig');
