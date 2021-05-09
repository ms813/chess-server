import {ChessMove} from './chess-move.model';

export interface IChessGame {
  id: number;
  whitePlayerName: string;
  blackPlayerName: string;
  currentPositionFEN: string;
  startedTimestamp: Date;
  moves: ChessMove[];
  score: number;
}

export class ChessGame implements IChessGame {

  id: number;
  whitePlayerName: string;
  blackPlayerName: string;
  currentPositionFEN: string;
  moves: ChessMove[];
  score: number;
  startedTimestamp: Date;


  constructor(data: IChessGame) {
    this.id = data.id;
    this.blackPlayerName = data.blackPlayerName;
    this.whitePlayerName = data.whitePlayerName;
    this.currentPositionFEN= data.currentPositionFEN;
    this.moves = data.moves;
    this.score = data.score;
    this.startedTimestamp = data.startedTimestamp;
  }
}
