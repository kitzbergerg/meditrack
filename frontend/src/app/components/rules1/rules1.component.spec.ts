import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Rules1Component } from './rules1.component';

describe('Rules1Component', () => {
  let component: Rules1Component;
  let fixture: ComponentFixture<Rules1Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Rules1Component]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(Rules1Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
