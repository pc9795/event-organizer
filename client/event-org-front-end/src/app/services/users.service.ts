import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {User} from "../models/user";

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  constructor(private httpClient: HttpClient) {
  }

  getUsers(searchStr: string) {
    return this.httpClient.get(`http://localhost:8080/api/v1/users/?search=${searchStr}`);
  }

  getUser(userId: number) {
    return this.httpClient.get(`http://localhost:8080/api/v1/users/${userId}`);
  }

  register(user: User) {
    return this.httpClient.post('http://localhost:8080/api/v1/users/', user);
  }
}
