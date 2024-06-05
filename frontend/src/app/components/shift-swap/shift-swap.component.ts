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
  currentShifts: ShiftSwapShift[] = []
  selectedDate: Date | undefined;
  newShiftSwap: ShiftSwap | undefined;
  shiftSwapDialog = false;

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
    this.shiftSwapService.getAllRequestedShiftSwaps().subscribe({
      next: response => {
        console.log(response)
        this.shiftSwaps = response;
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


  getDisabledDates(): Date[] {
    // Get the current month and year
    const currentMonth = this.minDate.getMonth();
    const currentYear = this.minDate.getFullYear();

    // Get the first and last day of the current month
    const firstDayOfMonth = new Date(currentYear, currentMonth, 1);
    const lastDayOfMonth = new Date(currentYear, currentMonth + 1, 0);

    // Generate an array of dates for the current month
    const datesOfMonth = [];
    for (let i = firstDayOfMonth.getDate(); i <= lastDayOfMonth.getDate(); i++) {
      const date = new Date(currentYear, currentMonth, i);
      datesOfMonth.push(date);
    }

    // Filter out the dates not present in the currentShifts array
    return  datesOfMonth.filter(date => !this.currentShifts.some(shift => {
      const shiftDate = new Date(shift.date);
      return shiftDate.getFullYear() === date.getFullYear() &&
        shiftDate.getMonth() === date.getMonth() &&
        shiftDate.getDate() === date.getDate();
    }))
  }



  generateShiftSwap() {
    this.toggleDialog()
    if (this.selectedDate != undefined) {
      const shift = this.findShiftFromDate(this.selectedDate)
      if (shift !== undefined) {
        this.newShiftSwap = {
          requestedShift: shift,
          requestedShiftSwapStatus: ShiftSwapStatus.ACCEPT,
          suggestedShiftSwapStatus: ShiftSwapStatus.PENDING,
          swapRequestingUser: this.currentUser?.id == undefined? "" : this.currentUser.id
        };
      } else {
        this.newShiftSwap = undefined;
      }
    }
  }

  createShiftSwapOffer(){
    if (this.newShiftSwap !== undefined) {
      const simpleShiftSwap: SimpleShiftSwap = {
        requestedShift: this.newShiftSwap.requestedShift.id == undefined? "" : this.newShiftSwap.requestedShift.id,
        requestedShiftSwapStatus: ShiftSwapStatus.ACCEPT,
        suggestedShiftSwapStatus: ShiftSwapStatus.PENDING,
        swapRequestingUser: this.currentUser?.id == undefined? "" : this.currentUser.id
      }
      console.log(simpleShiftSwap)
      this.shiftSwapService.createShiftSwap(simpleShiftSwap).subscribe({
        next: response =>{
          console.log("new Shiftswap");
          this.getAllRequestedShiftSwaps()
        },
        error: (error) => {
          console.error('Error fetching data:', error);
        }});
    }
  }

  toggleDialog() {
    this.shiftSwapDialog = !this.shiftSwapDialog
  }


}
