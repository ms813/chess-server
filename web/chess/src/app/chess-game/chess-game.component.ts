import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ChessGame} from '../model/chess-game.model';
import {Subscription} from 'rxjs';
import {GameService} from '../service/game.service';
import {Chess, PieceSymbol} from '@lubert/chess.ts';
import {finalize} from 'rxjs/operators';

@Component({
  selector: 'app-chess-game',
  templateUrl: './chess-game.component.html',
  styleUrls: ['./chess-game.component.scss']
})
export class ChessGameComponent implements OnInit, OnDestroy {

  id: number;
  chessGame!: ChessGame;
  gameSubscription: Subscription;
  readonly ranks: string[] = ['8', '7', '6', '5', '4', '3', '2', '1'];
  readonly files: string[] = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
  chess: Chess = new Chess();

  nextMoveSAN: string = '';
  buttonDisabled: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private gameService: GameService
  ) {

    this.id = Number.parseInt(route.snapshot.paramMap.get('id') || '-1');
    console.log(this.id);
    this.gameSubscription = this.gameService.getGame(this.id).subscribe(game => {
      this.chessGame = game;
      console.log(game);
      console.log(game.currentPositionFEN);
      this.chess.load(game.currentPositionFEN);
    });
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.gameSubscription.unsubscribe();
  }

  getPiece(y: number, x: number): PieceSymbol {
    // @ts-ignore
    return this.chess.board()[y][x]?.type;
  }

  getColour(y: number, x: number) {
    // @ts-ignore
    return this.chess.board()[y][x].color;
  }

  isValidMove(san: string): boolean {
    const x = !!this.chess.validateMoves([san]);
    console.log(x)
    return x;
  }

  makeMove(san: string) {
    if (this.isValidMove(san)) {
      this.buttonDisabled = true;
      this.gameService.makeMove(this.id, san).pipe(
        finalize(() => this.buttonDisabled = false)
      ).subscribe((game: ChessGame) => {
        this.chessGame = game;
        this.chess.load(game.currentPositionFEN);
      });
    }
  }
}
