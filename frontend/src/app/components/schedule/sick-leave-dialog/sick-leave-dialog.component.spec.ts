import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SickLeaveDialogComponent } from './sick-leave-dialog.component';

describe('SickLeaveDialogComponent', () => {
  let component: SickLeaveDialogComponent;
  let fixture: ComponentFixture<SickLeaveDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SickLeaveDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SickLeaveDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
