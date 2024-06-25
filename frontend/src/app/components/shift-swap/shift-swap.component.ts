import {Component, ViewChild} from '@angular/core';
import {ConfirmationService, MessageService} from "primeng/api";
import {UserService} from "../../services/user.service";
import {AuthorizationService} from "../../services/authorization/authorization.service";
import {User} from "../../interfaces/user";
import {ShiftSwap, ShiftSwapShift, ShiftSwapStatus} from "../../interfaces/shiftSwap";
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
  display = false;

  teamUsers: User[] = []
  ownShiftSwapsOffers: ShiftSwap[] = [];
  requestedShiftSwaps: ShiftSwap[] = [];
  suggestedShiftSwaps: ShiftSwap[] = [];
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

  ownSelectedOffer: ShiftSwap | undefined;
  otherSelectedOffer: ShiftSwap | undefined;

  constructor(private messageService: MessageService,
              private userService: UserService,
              private authorizationService: AuthorizationService,
              private shiftSwapService: ShiftSwapService,
              private confirmationService: ConfirmationService,
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

  findUser(userId: string | undefined) {
    if (userId === undefined) {
      return undefined
    }
    return this.teamUsers.find(user => user.id === userId);
  }

  getUser(): void {
    this.userService.getUserById(this.userId).subscribe({
        next: response => {
          this.currentUser = response;
          this.getAllRequestedShiftSwaps();
          this.getAllOwnShiftSwapOffers();
          this.getShiftsFromCurrentMonth();
          this.getAllUsersFromTeam();
          this.getAllShiftSwapsOffers();
          this.getAllSuggestedShiftSwaps()
        },
        error: (error) => {
          console.error('Error fetching data:', error);
        }
    });
  }

  getAllRequestedShiftSwaps() {
    this.shiftSwapService.getAllShiftSwapRequests().subscribe({
        next: response => {
          this.requestedShiftSwaps = response;
          this.requestedShiftSwaps.sort((a, b) =>
            new Date(a.requestedShift.date).getTime() - new Date(b.requestedShift.date).getTime());
        },
        error: (error) => {
          console.error('Error fetching data:', error);
        }
    });
  }

  getAllSuggestedShiftSwaps() {
    this.shiftSwapService.getAllShiftSwapSuggestions().subscribe({
      next: response => {
        this.suggestedShiftSwaps = response;
        this.suggestedShiftSwaps.sort((a, b) =>
          new Date(a.requestedShift.date).getTime() - new Date(b.requestedShift.date).getTime());
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching data:', error);
        this.loading = false;
      }
    });
  }


  getAllOwnShiftSwapOffers() {
    this.shiftSwapService.getAllOwnShiftSwapOffers().subscribe({
        next: response => {
          this.ownShiftSwapsOffers = response;
          this.ownShiftSwapsOffers.sort((a, b) =>
            new Date(a.requestedShift.date).getTime() - new Date(b.requestedShift.date).getTime());
        },
        error: (error) => {
          console.error('Error fetching data:', error);
        }
    });
  }


  getAllShiftSwapsOffers() {
    this.shiftSwapService.getAllOfferedShiftSwaps().subscribe({
        next: response => {
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
      if (shift !== undefined && this.ownShiftSwapsOffers.filter(tempShift => tempShift.requestedShift.id == shift.id).length == 0) {
        this.valid = true;
        this.toggleDialog()
        this.newShiftSwap = {
          requestedShift: shift,
          requestedShiftSwapStatus: ShiftSwapStatus.ACCEPTED,
          suggestedShiftSwapStatus: ShiftSwapStatus.PENDING,
          swapRequestingUser: this.currentUser?.id == undefined ? "" : this.currentUser.id
        };
      } else {
        console.log("here")
        this.newShiftSwap = undefined;
        this.selectedDate = undefined;
        this.valid = false;
      }
    } else {
      this.valid = false;
    }
  }

  createShiftSwapOffer() {
    if (this.newShiftSwap !== undefined && this.newShiftSwap.requestedShift != undefined) {
      const shiftSwap: ShiftSwap = {
        requestedShift: this.newShiftSwap.requestedShift,
        requestedShiftSwapStatus: ShiftSwapStatus.ACCEPTED,
        suggestedShiftSwapStatus: ShiftSwapStatus.PENDING,
        swapRequestingUser: this.currentUser?.id == undefined ? "" : this.currentUser.id
      };
      this.shiftSwapService.createShiftSwap(shiftSwap).subscribe({
        next: response => {
          this.messageService.add({severity: 'success', summary: 'Successfully Created Shift Swap Offer '});
          this.ownShiftSwapsOffers.push(response);
          this.toggleDialog()
          this.ownSelectedOffer = undefined;
          this.otherSelectedOffer = undefined;
          this.selectedDate = undefined;
        },
        error: (error) => {
          this.toggleDialog()
          this.ownSelectedOffer = undefined;
          this.otherSelectedOffer = undefined;
          this.selectedDate = undefined;
          this.messageService.add({severity: 'error', summary: 'Error Creating Shift Swap Offer '});
        }
      });
    }
  }

  toggleDialog() {
    this.shiftSwapDialog = !this.shiftSwapDialog
  }



  createRequest() {
    if (this.ownSelectedOffer == undefined || this.otherSelectedOffer == undefined) {
      this.messageService.add({severity: 'error', summary: 'Two Shifts Offers have to be selected '});
      return;
    }
    const newRequestSwap : ShiftSwap = {
      id: undefined,
      requestedShift: this.ownSelectedOffer.requestedShift,
      requestedShiftSwapStatus: this.ownSelectedOffer.requestedShiftSwapStatus,
      suggestedShift: this.otherSelectedOffer?.requestedShift,
      suggestedShiftSwapStatus: ShiftSwapStatus.PENDING,
      swapRequestingUser: this.ownSelectedOffer.swapRequestingUser,
      swapSuggestingUser: this.otherSelectedOffer?.swapRequestingUser
    }

    this.shiftSwapService.createShiftSwap(newRequestSwap).subscribe({
      next: (response) => {
        this.hideDialog()
        this.ownSelectedOffer = undefined;
        this.otherSelectedOffer = undefined;
        this.messageService.add({severity: 'success', summary: 'Successfully Created Shift Swap'});
        this.requestedShiftSwaps.push(response);
      },
      error: (error) => {
        this.hideDialog()
        this.ownSelectedOffer = undefined;
        this.otherSelectedOffer = undefined;
        this.messageService.add({severity: 'error', summary: 'Error Creating Shift Swap ', detail: error.error});
      },
    })
  }

  retractOffer(id: string | undefined) {
    if (id != undefined) {

      this.shiftSwapService.deleteShiftSwap(id).subscribe(
        {
          next: response => {
            this.messageService.add({
              severity: 'success',
              summary: 'Successfully Deleted Shift Swap Offer'
            });
            const deletedShiftSwap = this.ownShiftSwapsOffers.find(s => s.id == id)
            this.ownShiftSwapsOffers = this.ownShiftSwapsOffers.filter(s => s.id != id);
            this.requestedShiftSwaps = this.requestedShiftSwaps.filter(s => s.requestedShift.id != deletedShiftSwap?.requestedShift.id);
            this.suggestedShiftSwaps = this.suggestedShiftSwaps.filter(s => s.suggestedShift?.id != deletedShiftSwap?.requestedShift?.id)
          }, error: error => {
            this.messageService.add({
              severity: 'error',
              summary: 'Deleting Shift Swap Offer Failed',
              detail: error.error
            });
          }
        });
    }
  }

  retractRequest(requestSwap : ShiftSwap) {
    if (requestSwap.id != undefined) {
      this.shiftSwapService.retractShiftSwapRequest(requestSwap.id).subscribe(
        {
          next: response => {
            this.messageService.add({
              severity: 'success',
              summary: 'Successfully Retract Shift Swap Request'
            });
            this.requestedShiftSwaps = this.requestedShiftSwaps.filter(s => s.id != requestSwap.id);
          }, error: error => {
            this.messageService.add({
              severity: 'error',
              summary: 'Retracting Shift Swap Request Failed',
              detail: error.error
            });
          }
        });
    }
  }

  confirmRetractRequest(event: Event, requestedSwap: ShiftSwap) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Do you want to retract this request?',
      header: 'Retract Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-success p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "pi pi-check mr-2",
      rejectIcon: "none",

      accept: () => {
        this.retractRequest(requestedSwap)
      }
    });
  }

  confirmRetractOffer(event: Event, id: string | undefined) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Do you want to delete this offer?<br>' +
        'This will delete all associated shift swap requests and offers',
      header: 'Delete Confirmation',
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-success p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "pi pi-check mr-2",
      rejectIcon: "none",

      accept: () => {
        this.retractOffer(id);
      }
    });
  }

  confirmSuggestionAction(event: Event, shiftSwap: ShiftSwap, action: string) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: `Do you want to ${action} this suggestion?`,
      header: `${action.charAt(0).toUpperCase() + action.slice(1)} Confirmation`,
      icon: 'pi pi-info-circle',
      acceptButtonStyleClass: "p-button-success p-button-text",
      rejectButtonStyleClass: "p-button-text p-button-text",
      acceptIcon: "pi pi-check mr-2",
      rejectIcon: "none",

      accept: () => {
        this.updateShiftSwap(shiftSwap, action);
      }
    });
  }

  updateShiftSwap(shiftSwap: ShiftSwap, action: string) {
    if (action == "accept") {
      shiftSwap.suggestedShiftSwapStatus = ShiftSwapStatus.ACCEPTED;
    } else if (action == "reject") {
      shiftSwap.suggestedShiftSwapStatus = ShiftSwapStatus.REJECTED;
    } else {
      this.messageService.add({
        severity: 'error', summary: 'Shift swap action is not valid',});
    }
    this.shiftSwapService.updateShiftSwap(shiftSwap).subscribe(
      {
        next: response => {
          if (response == null) {
            this.messageService.add({
              severity: 'success',
              summary: 'Successfully Swapped Shifts'
            });
            this.ownShiftSwapsOffers = this.ownShiftSwapsOffers.filter(s => s.requestedShift.id != shiftSwap.suggestedShift?.id);
            this.requestedShiftSwaps = this.requestedShiftSwaps.filter(s => s.requestedShift.id != shiftSwap.suggestedShift?.id);
            this.shiftSwapOffers = this.shiftSwapOffers.filter(s => s.requestedShift.id != shiftSwap.requestedShift.id);
            this.suggestedShiftSwaps = this.suggestedShiftSwaps.filter(s => s.suggestedShift?.id != shiftSwap.suggestedShift?.id);
            this.currentShifts = this.currentShifts.filter(s => s.id != shiftSwap.suggestedShift?.id);
            //TODO
            const newShift = shiftSwap.requestedShift;
          }
          this.suggestedShiftSwaps = this.suggestedShiftSwaps.filter(s => s.id != shiftSwap.id);
        }, error: error => {
          this.messageService.add({
            severity: 'error',
            summary: 'Swapping Shifts Failed',
            detail: error.error
          });
        }
      });
  }

  checkCondition(): void {
    console.log(this.ownSelectedOffer)
    console.log(this.otherSelectedOffer)
    if (this.otherSelectedOffer !== undefined  && this.ownSelectedOffer !== undefined &&
      this.otherSelectedOffer !== null  && this.ownSelectedOffer !== null) {
      this.showDialog();
    }
  }


  showDialog() {
    this.display = true;
  }

  hideDialog() {
    this.display = false;
    this.ownSelectedOffer = undefined;
    this.otherSelectedOffer = undefined;
  }

  protected readonly ShiftSwapStatus = ShiftSwapStatus;
}
