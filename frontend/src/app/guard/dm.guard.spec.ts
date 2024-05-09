import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { dmGuard } from './dm.guard';

describe('dmGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => dmGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
