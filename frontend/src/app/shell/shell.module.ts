import {NgModule} from "@angular/core";
import {ShellRoutingModule} from "./shell-routing.module";
import { ShellComponent } from './shell/shell.component';
import {RouterOutlet} from "@angular/router";
import {ButtonModule} from "primeng/button";

@NgModule({
  declarations: [
    ShellComponent,
  ],
  imports: [
    ShellRoutingModule,
    RouterOutlet,
    ButtonModule,
  ],
  providers: [
  ],
})

export class ShellModule {}
