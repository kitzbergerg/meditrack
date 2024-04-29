import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LangComponent } from './lang.component';

describe('LangComponent', () => {
  let component: LangComponent;
  let fixture: ComponentFixture<LangComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LangComponent]
    });
    fixture = TestBed.createComponent(LangComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
