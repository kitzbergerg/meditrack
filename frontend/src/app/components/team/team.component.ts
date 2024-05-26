import { Component, Input, Output, EventEmitter } from '@angular/core';
import {AuthorizationService} from "../../services/authentication/authorization.service";
import {UserService} from "../../services/user.service";
import {TeamService} from "../../services/team.service";
import {Team} from "../../interfaces/team";


@Component({
  selector: 'app-team',
  templateUrl: './team.component.html',
  styleUrl: './team.component.scss'
})
export class TeamComponent {

  @Output() teamEmitter = new EventEmitter<Team>();
  @Input() teamComponentHeader!: string;
  newTeam: Team = { name: '' };

  constructor(private authorizationService: AuthorizationService,
              private userService: UserService,
              private teamService: TeamService,
  )
  {}

  isTeamNameSet(): boolean {
    const b = !this.newTeam.name.trim();
    return b;
  }

  createTeam() {
    this.teamService.createTeam(this.newTeam).subscribe(
      (team) => {
        this.teamEmitter.emit(team);
      },
      (error) => {
        console.error('Error fetching team:', error);
      }
    );
  }

}
