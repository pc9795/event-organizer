import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../models/user';

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
    return this.httpClient.post('http://localhost:8080/api/v1/users/', user, {withCredentials: true});
  }
}
