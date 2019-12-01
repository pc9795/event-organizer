import {Component, OnInit} from '@angular/core';
import {UserLogin} from '../../models/user-login'

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  model: UserLogin;

  constructor() {
  }

  ngOnInit() {
  }

  get diagnostic() {
    return JSON.stringify(this.model);
  }
}
