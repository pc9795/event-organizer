import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/internal/Subject';
import {NavigationStart, Router} from '@angular/router';
import {Observable} from 'rxjs/internal/Observable';
import {Error} from '../models/error';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private subject = new Subject<any>();
  private keepAfterNavigationChange = false;

  constructor(private router: Router) {
    //Clear alert messages on route change
    router.events.subscribe(
      event => {
        if (event instanceof NavigationStart) {
          if (this.keepAfterNavigationChange) {
            //only keep for a single location change
            this.keepAfterNavigationChange = false;
          } else {
            //clear alert
            this.subject.next();
          }
        }
      }
    )
  }

  success(message: string, keepAfterNavigationChange = false) {
    this.keepAfterNavigationChange = keepAfterNavigationChange;
    this.subject.next({type: 'success', text: message});
  }

  error(error: Error, keepAfterNavigationChange = false) {
    this.keepAfterNavigationChange = keepAfterNavigationChange;
    this.subject.next({type: 'error', text: error.message});
  }

  getMessage(): Observable<any> {
    return this.subject.asObservable();
  }
}
