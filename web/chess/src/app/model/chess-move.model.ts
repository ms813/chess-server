export interface ChessMove {
  id: number;
  game: number;
  playedAtTimestamp: Date;
  san: string;
  activeColour: boolean;
  turnCounter: number;
}
