import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import {AuthorizationService} from "../services/authentication/authorization.service";

@Component({
    selector: 'app-menu',
    templateUrl: './app.menu.component.html'
})
export class AppMenuComponent implements OnInit {

    model: any[] = [];
    isEmployee = false;
    isDM = false;

    constructor(private authorizationService: AuthorizationService ) {
      this.isEmployee = this.authorizationService.hasAuthority(["employee"]);
      this.isDM = this.authorizationService.hasAuthority(["admin", "dm"]);

    }

    ngOnInit() {
        this.model = [
            {
                items: [
                  { label: 'Dashboard', icon: 'pi pi-fw pi-home', routerLink: ['/dashboard'], visible: this.isEmployee || this.isDM },
                  { label: 'Schedule', icon: 'pi pi-fw pi-calendar', routerLink: ['/schedule'], visible: this.isEmployee || this.isDM },
                  { label: 'Employees', icon: 'pi pi-fw pi-users', routerLink: ['/employees'], visible: this.isDM},
                  { label: 'Shift Types', icon: 'pi pi-fw pi-clock', routerLink: ['/todo'], visible: this.isDM },
                  { label: 'Roles', icon: 'pi pi-fw pi-lock', routerLink: ['/roles'], visible: this.isDM },
                  { label: 'Holidays', icon: 'pi pi-fw pi-sun', routerLink: ['/todo'], visible: this.isEmployee  },
                  { label: 'Rules', icon: 'pi pi-fw pi-cog', routerLink: ['/todo'], visible: this.isDM },
                  { label: 'Shift Swapping', icon: 'pi pi-fw pi-sync', routerLink: ['/todo'], visible: this.isEmployee }
                ]
            }
        ];
    }
}
