import {Component, ViewChild} from '@angular/core';
import {MessageService} from "primeng/api";
import {UserService} from "../../services/user.service";
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {User} from "../../interfaces/user";
import {ShiftSwap, ShiftSwapShift, ShiftSwapStatus, SimpleShiftSwap} from "../../interfaces/shiftSwap";
import {ShiftSwapService} from "../../services/shift-swap.service";
import {Calendar} from "primeng/calendar";

@Component({
  selector: 'app-shift-swap',
  templateUrl: './shift-swap.component.html',
  styleUrl: './shift-swap.component.scss'
})
export class ShiftSwapComponent {

  @ViewChild('calendar') calendar!: Calendar;

  userId = '';
  loading = true;
  currentUser : User | undefined;
  shiftSwaps: ShiftSwap[] = [];
  shiftSwapOffers: ShiftSwap[] = [];
  currentShifts: ShiftSwapShift[] = []
  selectedDate: Date | undefined;
  newShiftSwap: ShiftSwap | undefined;
  shiftSwapDialog = false;
  valid = true;

  currentDate: Date = new Date();
  firstDayOfMonth: Date = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth(), 1);
  lastDayOfMonth: Date = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, 0);

  minDate: Date = this.firstDayOfMonth;
  maxDate: Date = this.lastDayOfMonth;


  constructor(private messageService: MessageService,
              private userService: UserService,
              private authorizationService: AuthorizationService,
              private shiftSwapService: ShiftSwapService
  ) {
  }

  ngOnInit(): void {
    this.userId = this.authorizationService.parsedToken().sub;
    this.getUser();
  }

  getUser(): void {
    this.userService.getUserById(this.userId).subscribe({
      next: response => {
        this.currentUser = response;
        this.getAllRequestedShiftSwaps();
        this.getShiftsFromCurrentMonth()
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching data:', error);
      }}
    );
  }

  getAllRequestedShiftSwaps() {
    this.shiftSwapService.getAllRequestedShiftSwapsFromUser().subscribe({
      next: response => {
        console.log(response)
        this.shiftSwaps = response;
        if (this.shiftSwaps.length > 0) {
          this.getAllOfferedShiftSwaps();
        } else {
          this.shiftSwapOffers = [];
        }
      },
      error: (error) => {
        console.error('Error fetching data:', error);
      }}
    );
  }

  getAllOfferedShiftSwaps() {
    this.shiftSwapService.getAllOfferedShiftSwaps().subscribe({
      next: response => {
        console.log(response)
        this.shiftSwapOffers = response;
      },
      error: (error) => {
        console.error('Error fetching data:', error);
      }}
    );
  }

  getShiftsFromCurrentMonth() {
    this.shiftSwapService.getAllShiftsFromCurrentMonth().subscribe({
      next: response =>{
        this.currentShifts = response;
      },
      error: (error) => {
        console.error('Error fetching data:', error);
      }}
    )
  }


  isSpecialDate(date: any): boolean {
    if (date == undefined) return false;
    return this.findShiftFromDate(date) != undefined;
  }

  colorOfShift(date: any): string {
    const shift = this.findShiftFromDate(date)
    if (shift != undefined) {
      return shift.shiftType.color;
    }
    return "";
  }

  findShiftFromDate(date: any): ShiftSwapShift | undefined {
    let currentDate = new Date();

    if (date instanceof Date) {
      currentDate = date;
    } else {
      currentDate = new Date(date.year, date.month, date.day);
    }

    currentDate.setHours(currentDate.getHours() + 2);

    return this.currentShifts.find(shift => {
      const shiftDate = new Date(shift.date);
      return shiftDate.getTime() === currentDate.getTime();
    });
  }



  generateShiftSwap() {
    if (this.selectedDate != undefined) {
      const shift = this.findShiftFromDate(this.selectedDate)
      if (shift !== undefined && this.shiftSwaps.filter(tempShift => tempShift.requestedShift.id == shift.id).length == 0) {
        this.valid = true;
        this.toggleDialog()
        this.newShiftSwap = {
          requestedShift: shift,
          requestedShiftSwapStatus: ShiftSwapStatus.ACCEPTED,
          suggestedShiftSwapStatus: ShiftSwapStatus.PENDING,
          swapRequestingUser: this.currentUser?.id == undefined? "" : this.currentUser.id
        };
      } else {
        this.newShiftSwap = undefined;
        this.valid = false;
      }
    } else {
      this.valid = false;
    }
  }

  createShiftSwapOffer(){
    if (this.newShiftSwap !== undefined) {
      const simpleShiftSwap: SimpleShiftSwap = {
        requestedShift: this.newShiftSwap.requestedShift.id == undefined? "" : this.newShiftSwap.requestedShift.id,
        requestedShiftSwapStatus: ShiftSwapStatus.ACCEPTED,
        suggestedShiftSwapStatus: ShiftSwapStatus.PENDING,
        swapRequestingUser: this.currentUser?.id == undefined? "" : this.currentUser.id
      }
      console.log(simpleShiftSwap)
      this.shiftSwapService.createShiftSwap(simpleShiftSwap).subscribe({
        next: response =>{
          this.messageService.add({severity: 'success', summary: 'Successfully Created Shift Swap Offer '});
          this.getAllRequestedShiftSwaps()
          this.toggleDialog()
        },
        error: (error) => {
          this.toggleDialog()
          this.messageService.add({severity: 'error', summary: 'Error Creating Shift Swap Offer '});
        }});
    }
  }

  toggleDialog() {
    this.shiftSwapDialog = !this.shiftSwapDialog
    this.valid = true;
  }


}
