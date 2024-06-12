import { TestBed } from '@angular/core/testing';

import { ShiftTypeService } from './shiftType.service';

describe('ShiftService', () => {
  let service: ShiftTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ShiftTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
