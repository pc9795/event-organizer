import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../models/user';
import {environment} from '../../environments/environment';

/**
 * Interacts with server for user REST resource.
 */
@Injectable({
  providedIn: 'root'
})
export class UsersService {

  constructor(private httpClient: HttpClient) {
  }

  register(user: User) {
    return this.httpClient.post(`${environment.server}api/v1/users/`, user, {withCredentials: true});
  }
}
