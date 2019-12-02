import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/internal/Subscription';
import {User} from '../../models/user';
import {AuthenticationService} from '../../services/authentication.service';
import {UsersService} from '../../services/users.service';
import {EventsService} from '../../services/events.service';
import {HttpErrorResponse} from '@angular/common/http';
import {AlertService} from '../../services/alert.service';
import {Event} from '../../models/event';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  currentUser: User;
  currentUserSubscription: Subscription;
  events: Event[];
  refDate: Date = new Date();

  constructor(private authenticationService: AuthenticationService, private userService: UsersService,
              private eventService: EventsService, private alertService: AlertService, private router: Router) {
    this.currentUserSubscription = this.authenticationService.currentUser.subscribe(
      user => {
        this.currentUser = user;
      }
    );
    this.eventService.getActiveEvents().subscribe(data => {
      this.events = <Event[]>data;
      this.events.map(event => {
        event.startTime = new Date(event.startTime);
        event.endTime = new Date(event.endTime);
      });
    }, (error: HttpErrorResponse) => {
      this.alertService.error(error);
    });
  }

  ngOnInit() {
  }

  ngOnDestroy(): void {
    this.currentUserSubscription.unsubscribe();
  }

  objLen(obj): number {
    return Object.keys(obj).length;
  }

  checkEventIsFinished(startDate: Date, endDate: Date): number {
    if (this.refDate > startDate && this.refDate < endDate) {
      return 0;
    } else if (this.refDate > endDate) {
      return 1;
    }
    return 2;
  }


  truncatedStr(str: string, size: number): string {
    if (str == null) {
      return '';
    }
    if (str.length > size) {
      return `${str.substring(0, size)}...`;
    }
    return str;
  }

  getCreatedBy(user: User): string {
    if (this.currentUser.username === user.username) {
      return 'You';
    }
    return user.username;
  }

  isCreatedByMe(createdBy: User): boolean {
    return this.currentUser.username === createdBy.username;
  }

  deleteEvent(eventId: number, mine: boolean) {
    if (confirm('Are you sure to delete this anEvent')) {
      if (mine) {
        this.eventService.deleteEvent(eventId).subscribe(data => {
          this.alertService.success('Event deleted successfully!', true);
          //Refresh hack
          this.router.navigateByUrl('/createEvent', {skipLocationChange: true}).then(() => {
            this.router.navigate(['/']);
          });
        }, error => this.alertService.error(error));
      } else {
        this.eventService.unshareEventWithCurrentUser(eventId).subscribe(data => {
          this.alertService.success('Event unshared successfully!', true);
          //Refresh hack
          this.router.navigateByUrl('/createEvent', {skipLocationChange: true}).then(() => {
            this.router.navigate(['/']);
          });
        }, error => this.alertService.error(error));
      }
    }
  }

  archiveEvent(eventId: number, mine: boolean) {
    console.log('archive:' + eventId);
  }
}
