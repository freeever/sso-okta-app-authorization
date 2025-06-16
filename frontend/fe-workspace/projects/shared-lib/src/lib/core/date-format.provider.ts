import { MAT_DATE_FORMATS } from '@angular/material/core';

export const APP_DATE_FORMATS = {
  parse: {
    dateInput: 'YYYY-MM-DD',
  },
  display: {
    dateInput: 'YYYY-MM-DD',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  }
};

export const DateFormatProvider = {
  provide: MAT_DATE_FORMATS,
  useValue: APP_DATE_FORMATS,
};
