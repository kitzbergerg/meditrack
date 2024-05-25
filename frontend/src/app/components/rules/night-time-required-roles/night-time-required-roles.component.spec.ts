import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NightTimeRequiredRolesComponent } from './night-time-required-roles.component';

describe('NightTimeRequiredRolesComponent', () => {
  let component: NightTimeRequiredRolesComponent;
  let fixture: ComponentFixture<NightTimeRequiredRolesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NightTimeRequiredRolesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(NightTimeRequiredRolesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
