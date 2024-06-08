import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShiftSwapComponent } from './shift-swap.component';

describe('ShiftSwapComponent', () => {
  let component: ShiftSwapComponent;
  let fixture: ComponentFixture<ShiftSwapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShiftSwapComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ShiftSwapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
