import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ChessGame, IChessGame} from '../model/chess-game.model';
import {environment} from '../../environments/environment';
import {NewGameRequest} from '../model/new-game-request.model';
import {map, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(private http: HttpClient) {
  }

  public createGame(request: NewGameRequest): Observable<ChessGame> {
    const {whitePlayerName: white, blackPlayerName: black} = request;
    if (!(request && white && black)) {
      throw new Error('Failed to create new game - one or both player names were blank');
    }

    return this.http.post<IChessGame>(environment.gameApiUrl, request).pipe(
      this.dataToChessGameMapper,
    );
  }

  public getGame(id: number): Observable<ChessGame> {
    return this.http.get<IChessGame>(`${environment.gameApiUrl}/${id}`);
  }

  private dataToChessGameMapper = map((response: IChessGame) => new ChessGame(response));

  public makeMove(id: number, san: string): Observable<ChessGame> {
    console.debug(`Posting move ${san} for game ${id}`)
    return this.http.post<IChessGame>(`${environment.gameApiUrl}/${id}/move`, san).pipe(
      this.dataToChessGameMapper,
    );
  }
}
