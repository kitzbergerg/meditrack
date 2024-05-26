import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllowedFlexTimePerMonthComponent } from './allowed-flex-time-per-month.component';

describe('AllowedFlexTimePerMonthComponent', () => {
  let component: AllowedFlexTimePerMonthComponent;
  let fixture: ComponentFixture<AllowedFlexTimePerMonthComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllowedFlexTimePerMonthComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AllowedFlexTimePerMonthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
