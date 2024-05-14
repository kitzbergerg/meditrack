import {NgModule} from "@angular/core";
import {ShellRoutingModule} from "./shell-routing.module";
import { ShellComponent } from './shell/shell.component';
import {RouterLink, RouterOutlet} from "@angular/router";
import {ButtonModule} from "primeng/button";
import {RippleModule} from "primeng/ripple";

@NgModule({
  declarations: [
    ShellComponent,
  ],
  imports: [
    ShellRoutingModule,
    RouterOutlet,
    ButtonModule,
    RouterLink,
    RippleModule,
  ],
  providers: [
  ],
})

export class ShellModule {}
