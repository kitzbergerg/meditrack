import {NgModule} from "@angular/core";
import {ShellRoutingModule} from "./shell-routing.module";
import { ShellComponent } from './shell/shell.component';
import {RouterLink, RouterOutlet} from "@angular/router";
import {ToolbarComponent} from "./toolbar/toolbar.component";
import {ButtonModule} from "primeng/button";

@NgModule({
  declarations: [
    ShellComponent,
    ToolbarComponent,
  ],
  imports: [
    ShellRoutingModule,
    RouterOutlet,
    ButtonModule,
    RouterLink,
  ],
  providers: [
  ],
})

export class ShellModule {}
