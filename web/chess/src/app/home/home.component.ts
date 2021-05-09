import {Component, OnDestroy, OnInit} from '@angular/core';
import {GameService} from '../service/game.service';
import {Subscription} from 'rxjs';
import {NewGameRequest} from '../model/new-game-request.model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {

  newGameSubscription!: Subscription;
  newGameRequest: NewGameRequest = {
    whitePlayerName: '',
    blackPlayerName: ''
  };

  constructor(
    private gameService: GameService,
    private router: Router
  ) {}

  ngOnInit(): void {
  }

  newGame() {
    this.newGameSubscription = this.gameService.createGame(this.newGameRequest)
      .subscribe(game => this.router.navigate(['play', game.id]));
  }

  ngOnDestroy() {
    this.newGameSubscription.unsubscribe();
  }
}
