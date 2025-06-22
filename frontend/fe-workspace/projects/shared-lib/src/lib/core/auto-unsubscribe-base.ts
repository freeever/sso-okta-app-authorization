// auto-unsubscribe-base.ts
import { Directive } from '@angular/core';
import { Subject } from 'rxjs';

@Directive()
export abstract class AutoUnsubscribeBase {
  protected destroy$ = new Subject<void>();

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
