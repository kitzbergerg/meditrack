import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduleLegendComponent } from './schedule-legend.component';

describe('ScheduleLegendComponent', () => {
  let component: ScheduleLegendComponent;
  let fixture: ComponentFixture<ScheduleLegendComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScheduleLegendComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ScheduleLegendComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
