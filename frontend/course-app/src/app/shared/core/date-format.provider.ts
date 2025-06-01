import { MAT_DATE_FORMATS } from '@angular/material/core';

export const APP_DATE_FORMATS = {
  parse: {
    dateInput: 'yyyy-MM-dd',
  },
  display: {
    dateInput: 'yyyy-MM-dd',
    monthYearLabel: 'MMM yyyy',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM yyyy',
  }
};

export const DateFormatProvider = {
  provide: MAT_DATE_FORMATS,
  useValue: APP_DATE_FORMATS,
};
