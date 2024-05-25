import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MinRestPeriodRuleComponent } from './min-rest-period-rule.component';

describe('MinRestPeriodRuleComponent', () => {
  let component: MinRestPeriodRuleComponent;
  let fixture: ComponentFixture<MinRestPeriodRuleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MinRestPeriodRuleComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MinRestPeriodRuleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
