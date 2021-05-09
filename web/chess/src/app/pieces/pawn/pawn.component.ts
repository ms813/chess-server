import {Component, Input} from '@angular/core';
import {Color, WHITE} from '@lubert/chess.ts';

@Component({
  selector: 'app-pawn',
  templateUrl: './pawn.svg',
  styleUrls: ['../pieces.scss']
})
export class PawnComponent {

  @Input()
  side: Color = WHITE;
}
