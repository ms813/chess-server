import {Component, Input} from '@angular/core';
import {Color, WHITE} from '@lubert/chess.ts';

@Component({
  selector: 'app-knight',
  templateUrl: './knight.svg',
  styleUrls: ['../pieces.scss']
})
export class KnightComponent {

  @Input()
  side: Color = WHITE;
}
