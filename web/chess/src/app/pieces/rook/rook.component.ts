import {Component, Input} from '@angular/core';
import {Color, WHITE} from '@lubert/chess.ts';

@Component({
  selector: 'app-rook',
  templateUrl: './rook.svg',
  styleUrls: ['../pieces.scss']
})
export class RookComponent {

  @Input()
  side: Color = WHITE;
}
