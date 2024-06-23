import {ComponentFixture, TestBed} from '@angular/core/testing';

import {HolidaysComponent} from './holidays.component';

describe('HolidaysComponent', () => {
  let component: HolidaysComponent;
  let fixture: ComponentFixture<HolidaysComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HolidaysComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HolidaysComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
