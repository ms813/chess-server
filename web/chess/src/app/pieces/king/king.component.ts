import {Component, Input} from '@angular/core';
import {Color, WHITE} from '@lubert/chess.ts';

@Component({
  selector: 'app-king',
  templateUrl: './king.svg',
  styleUrls: ['../pieces.scss']
})
export class KingComponent {

  @Input()
  side: Color = WHITE;
}
