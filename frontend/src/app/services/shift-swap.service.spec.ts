import { TestBed } from '@angular/core/testing';

import { ShiftSwapService } from './shift-swap.service';

describe('ShiftSwapService', () => {
  let service: ShiftSwapService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ShiftSwapService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
