import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MandatoryOffDaysRuleComponent } from './mandatory-off-days-rule.component';

describe('MandatoryOffdaysRuleComponent', () => {
  let component: MandatoryOffDaysRuleComponent;
  let fixture: ComponentFixture<MandatoryOffDaysRuleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MandatoryOffDaysRuleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MandatoryOffDaysRuleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
