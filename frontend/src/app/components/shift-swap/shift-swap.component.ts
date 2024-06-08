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
  currentUser: User | undefined;

  teamUsers: User[] = []
  shiftSwaps: ShiftSwap[] = [];
  requestedShiftSwaps: ShiftSwap[] = [];
  shiftSwapOffers: ShiftSwap[] = [];
  currentShifts: ShiftSwapShift[] = []
  selectedDate: Date | undefined;
  newShiftSwap: ShiftSwap | undefined;
  shiftSwapDialog = false;
  valid = true;
  shiftSwapOffersPresent = false;

  currentDate: Date = new Date();
  firstDayOfMonth: Date = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth(), 1);
  lastDayOfMonth: Date = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, 0);

  minDate: Date = this.firstDayOfMonth;
  maxDate: Date = this.lastDayOfMonth;

  ownSelectedOffer: ShiftSwap | undefined;
  otherSelectedOffer: ShiftSwap | undefined;

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

  getAllUsersFromTeam() {
    this.userService.getAllUserFromTeam().subscribe({
        next: response => {
          this.teamUsers = response;
        },
        error: (error) => {
          console.error('Error fetching data:', error);
        }
      }
    );
  }

  findUser(userId: string) {
    return this.teamUsers.find(user => user.id === userId);
  }


  getUser(): void {
    this.userService.getUserById(this.userId).subscribe({
        next: response => {
          this.currentUser = response;
          this.getAllRequestedShiftSwaps();
          this.getShiftsFromCurrentMonth()
          this.getAllUsersFromTeam()
          this.loading = false;
        },
        error: (error) => {
          console.error('Error fetching data:', error);
        }
      }
    );
  }

  getAllRequestedShiftSwaps() {
    this.shiftSwapService.getAllRequestedShiftSwapsFromUser().subscribe({
        next: response => {
          console.log(response)
          this.shiftSwaps = response;
          this.shiftSwaps.sort((a, b) =>
            new Date(a.requestedShift.date).getTime() - new Date(b.requestedShift.date).getTime());
          this.requestedShiftSwaps = this.filteredShiftSwaps(this.shiftSwaps, "requested");
          if (this.shiftSwaps.length > 0) {
            this.getAllOfferedShiftSwaps();
          } else {
            this.shiftSwapOffers = [];
          }
        },
        error: (error) => {
          console.error('Error fetching data:', error);
        }
      }
    );
  }

  filteredShiftSwaps(toFilterArray: ShiftSwap[], type: string): ShiftSwap[] {
    switch (type) {
      case "requested":
          return toFilterArray.filter(swap => swap.swapRequestingUser == this.userId
            && swap.swapSuggestingUser != null && swap.suggestedShiftSwapStatus == ShiftSwapStatus.PENDING
            && swap.requestedShiftSwapStatus == ShiftSwapStatus.ACCEPTED);
      case "suggested":
        return toFilterArray.filter(swap => swap.swapSuggestingUser == this.userId
          && swap.suggestedShiftSwapStatus == ShiftSwapStatus.PENDING
          && swap.requestedShiftSwapStatus == ShiftSwapStatus.ACCEPTED);
      default:
        console.log("Wrong filter")
        return [];
    }
  }

  getAllOfferedShiftSwaps() {
    this.shiftSwapService.getAllOfferedShiftSwaps().subscribe({
        next: response => {
          console.log(response)
          this.shiftSwapOffers = response;
          this.shiftSwapOffers.sort((a, b) =>
            new Date(a.requestedShift.date).getTime() - new Date(b.requestedShift.date).getTime())
        },
        error: (error) => {
          console.error('Error fetching data:', error);
        }
      }
    );
  }

  getShiftsFromCurrentMonth() {
    this.shiftSwapService.getAllShiftsFromCurrentMonth().subscribe({
        next: response => {
          this.currentShifts = response;
        },
        error: (error) => {
          console.error('Error fetching data:', error);
        }
      }
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


  generateShiftSwapOffer() {
    if (this.selectedDate != undefined) {
      const shift = this.findShiftFromDate(this.selectedDate)
      if (shift !== undefined && this.shiftSwaps.filter(tempShift => tempShift.requestedShift.id == shift.id).length == 0) {
        this.valid = true;
        this.toggleDialog()
        this.newShiftSwap = {
          requestedShift: shift,
          requestedShiftSwapStatus: ShiftSwapStatus.ACCEPTED,
          suggestedShiftSwapStatus: ShiftSwapStatus.PENDING,
          swapRequestingUser: this.currentUser?.id == undefined ? "" : this.currentUser.id
        };
      } else {
        this.newShiftSwap = undefined;
        this.valid = false;
      }
    } else {
      this.valid = false;
    }
  }

  createShiftSwapOffer() {
    if (this.newShiftSwap !== undefined) {
      const simpleShiftSwap: SimpleShiftSwap = {
        requestedShift: this.newShiftSwap.requestedShift.id == undefined ? "" : this.newShiftSwap.requestedShift.id,
        requestedShiftSwapStatus: ShiftSwapStatus.ACCEPTED,
        suggestedShiftSwapStatus: ShiftSwapStatus.PENDING,
        swapRequestingUser: this.currentUser?.id == undefined ? "" : this.currentUser.id
      }
      this.shiftSwapService.createShiftSwap(simpleShiftSwap).subscribe({
        next: response => {
          console.log(response)
          this.messageService.add({severity: 'success', summary: 'Successfully Created Shift Swap Offer '});
          //this.shiftSwaps.push(response);
          this.getAllRequestedShiftSwaps()
          this.toggleDialog()
        },
        error: (error) => {
          this.toggleDialog()
          this.messageService.add({severity: 'error', summary: 'Error Creating Shift Swap Offer '});
        }
      });
    }
  }

  toggleDialog() {
    this.shiftSwapDialog = !this.shiftSwapDialog
    this.valid = true;
  }

  selectOwnOffer(shiftSwap: ShiftSwap) {
    if (this.ownSelectedOffer == shiftSwap) {
      this.ownSelectedOffer = undefined;
    } else {
      this.ownSelectedOffer = shiftSwap;
    }
  }

  selectOtherOffer(shiftSwap: ShiftSwap) {
    if (this.otherSelectedOffer == shiftSwap) {
      this.otherSelectedOffer = undefined;
    } else {
      this.otherSelectedOffer = shiftSwap;
    }
  }

  createRequest() {

    if (this.ownSelectedOffer == undefined  || this.otherSelectedOffer == undefined) {
      this.messageService.add({severity: 'error', summary: 'Two Shifts Offers have to be selected '});
      return;
    }
    // add the shift offer from the other person to the own shift offer to create an actual shift swap request
    this.ownSelectedOffer.swapSuggestingUser = this.otherSelectedOffer?.swapRequestingUser;
    this.ownSelectedOffer.suggestedShift = this.otherSelectedOffer?.requestedShift;
    this.ownSelectedOffer.suggestedShiftSwapStatus = ShiftSwapStatus.PENDING;

    this.shiftSwapService.updateShiftSwap(this.ownSelectedOffer).subscribe({
      next: (response) => {
        this.messageService.add({severity: 'success', summary: 'Successfully Created Shift Swap'});
        console.log(response);

      },
      error: (error) => {
        this.messageService.add({severity: 'error', summary: 'Error Creating Shift Swap ', detail: error.error});
      },
    })

      /*
      }

      this.shiftSwapService.updateShiftSwap(this.ownOffer).subscribe({
        next: response => {
          this.messageService.add({severity: 'success', summary: 'Successfully Created Shift Swap Request '});
        },
        error: (error) => {
          this.toggleDialog()
          this.messageService.add({severity: 'error', summary: 'Error Creating Shift Swap Request '});
        }
      });
       */
  }

  retractOffer(id: string | undefined) {
    if (id != undefined) {
      this.shiftSwapService.deleteShiftSwap(id)
        .subscribe(response => {
          console.log('Shift Swap Offer deleted successfully');
          this.messageService.add({
            severity: 'success',
            summary: 'Successfully Deleted Shift Swap Offer'
          });
          this.shiftSwaps.filter(s => s.id !=id);
          //this.getAllRequestedShiftSwaps();
        }, error => {
          console.error('Error deleting shift swap:', error);
          this.messageService.add({
            severity: 'error',
            summary: 'Deleting Shift Swap Offer Failed',
            detail: error.error
          });
        });
    }
  }
}
