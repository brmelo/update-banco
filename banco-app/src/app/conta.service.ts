import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Conta } from './conta/conta';

import { environment } from '../environments/environment'

@Injectable({
  providedIn: 'root'
})
export class ContaService {

  url: string = environment.apiBaseUrl

  constructor(
    private http: HttpClient
  ) { }

salvar(conta: Conta) : Observable<Conta>{
  return this.http.post<Conta>(this.url, conta);
}

listar() : Observable<Conta[]>{
  return this.http.get<any>(this.url);
}

}
