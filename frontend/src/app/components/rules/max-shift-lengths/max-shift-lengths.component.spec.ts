import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MaxShiftLengthsComponent } from './max-shift-lengths.component';

describe('MaxShiftLengthsComponent', () => {
  let component: MaxShiftLengthsComponent;
  let fixture: ComponentFixture<MaxShiftLengthsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MaxShiftLengthsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MaxShiftLengthsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
