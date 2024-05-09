import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { employeeGuard } from './employee.guard';

describe('employeeGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => employeeGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
