import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DayTimeRequiredRolesComponent } from './day-time-required-roles.component';

describe('DayTimeRequiredRolesComponent', () => {
  let component: DayTimeRequiredRolesComponent;
  let fixture: ComponentFixture<DayTimeRequiredRolesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DayTimeRequiredRolesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DayTimeRequiredRolesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
