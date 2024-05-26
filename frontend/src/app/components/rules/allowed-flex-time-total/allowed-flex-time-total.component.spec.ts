import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllowedFlexTimeTotalComponent } from './allowed-flex-time-total.component';

describe('AllowedFlexTimeTotalComponent', () => {
  let component: AllowedFlexTimeTotalComponent;
  let fixture: ComponentFixture<AllowedFlexTimeTotalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllowedFlexTimeTotalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AllowedFlexTimeTotalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
