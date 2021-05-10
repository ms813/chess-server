import {ChessMove} from './chess-move.model';

export interface ChessGame {
  id: number;
  whitePlayerName: string;
  blackPlayerName: string;
  currentPositionFEN: string;
  startedTimestamp: Date;
  moves: ChessMove[];
  score: number;
}
