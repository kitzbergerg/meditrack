import {NgModule} from "@angular/core";
import {ShellRoutingModule} from "./shell-routing.module";
import { ShellComponent } from './shell/shell.component';

@NgModule({
  declarations: [
    ShellComponent
  ],
  imports: [
    ShellRoutingModule,
  ],
  providers: [
  ],
})

export class ShellModule {}
