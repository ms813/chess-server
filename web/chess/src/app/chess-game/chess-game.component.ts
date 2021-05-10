import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ChessGame} from '../model/chess-game.model';
import {Subscription} from 'rxjs';
import {GameService} from '../service/game.service';
import {Chess, Color, PieceSymbol} from '@lubert/chess.ts';
import {finalize} from 'rxjs/operators';
import {ChessMove} from '../model/chess-move.model';

@Component({
  selector: 'app-chess-game',
  templateUrl: './chess-game.component.html',
  styleUrls: ['./chess-game.component.scss']
})
export class ChessGameComponent implements OnInit, OnDestroy {

  id!: number;
  chessGame!: ChessGame;
  gameSubscription!: Subscription;
  readonly ranks: string[] = ['8', '7', '6', '5', '4', '3', '2', '1'];
  readonly files: string[] = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];

  // moves in a renderable format
  displayMoves: string[] = [];

  // engine from 3rd party library
  chess: Chess = new Chess();

  nextMoveSAN: string = '';
  buttonDisabled: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private gameService: GameService
  ) {
  }

  ngOnInit(): void {
    this.id = Number.parseInt(this.route.snapshot.paramMap.get('id') || '-1');

    this.gameSubscription = this.gameService.getGame(this.id).subscribe(game => {
      this.chessGame = game;
      this.chess.load(game.currentPositionFEN);
      this.displayMoves = this.parseDisplayMoves(game.moves);
    });
  }

  ngOnDestroy(): void {
    this.gameSubscription.unsubscribe();
  }

  getPiece(y: number, x: number): PieceSymbol | undefined {
    return this.chess.board()[y][x]?.type;
  }

  getColour(y: number, x: number): Color {
    return this.chess.board()[y][x]?.color || 'w';
  }

  isValidMove(san: string): boolean {
    return !!this.chess.validateMoves([san]);
  }

  makeMove(san: string) {
    if (this.isValidMove(san)) {
      this.buttonDisabled = true;
      this.gameService.makeMove(this.id, san).pipe(
        finalize(() => this.buttonDisabled = false)
      ).subscribe((game: ChessGame) => {
        this.chessGame = game;
        this.chess.load(game.currentPositionFEN);
        this.displayMoves = this.parseDisplayMoves(game.moves);
        this.nextMoveSAN = '';
      });
    }
  }

  private parseDisplayMoves(moves: ChessMove[]) {
    const displayMoves = [];

    for (let i = 0; i < moves.length; i += 2) {
      const whiteMove = moves[i];
      const blackMove = moves[i + 1];
      const displayMove = [`${whiteMove.turnCounter}. ${whiteMove.san}`];
      if (blackMove) {
        displayMove.push(`${blackMove.san}`);
      }

      displayMoves.push(displayMove.join(' '));
    }

    return displayMoves;
  }
}
