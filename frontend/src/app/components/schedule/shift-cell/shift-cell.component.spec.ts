import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShiftCellComponent } from './shift-cell.component';

describe('ShiftCellComponent', () => {
  let component: ShiftCellComponent;
  let fixture: ComponentFixture<ShiftCellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShiftCellComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ShiftCellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
