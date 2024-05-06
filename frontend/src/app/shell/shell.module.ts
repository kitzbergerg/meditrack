import {NgModule} from "@angular/core";
import {ShellRoutingModule} from "./shell-routing.module";
import { ShellComponent } from './shell/shell.component';
import {RouterOutlet} from "@angular/router";

@NgModule({
  declarations: [
    ShellComponent
  ],
  imports: [
    ShellRoutingModule,
    RouterOutlet,
  ],
  providers: [
  ],
})

export class ShellModule {}
