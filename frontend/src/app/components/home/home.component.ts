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

/**
 * Component carrying the active events
 */
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
  searched: boolean = false;
  options = new Map();
  canShare = true;

  constructor(private authenticationService: AuthenticationService, private userService: UsersService,
              private eventService: EventsService, private alertService: AlertService, private router: Router) {

    //Subscribe to user
    this.currentUserSubscription = this.authenticationService.currentUser.subscribe(
      user => {
        this.currentUser = user;
      }
    );

    //Get the passed state from other component.
    let state = this.router.getCurrentNavigation().extras.state;
    this.searched = !!(state && state.searchStr);

    //Get active events.
    this.eventService.getActiveEvents(state ? state.searchStr : null).subscribe(data => {
      this.events = <Event[]>data;
      this.events.map(event => {
        event.startTime = new Date(event.startTime);
        event.endTime = new Date(event.endTime);
        this.options.set(event.id, false);
      });
    }, (error: HttpErrorResponse) => {
      this.alertService.error(error);
    })
  }

  ngOnInit() {
  }

  ngOnDestroy(): void {
    this.currentUserSubscription.unsubscribe();
  }

  //Get  the length of an object
  objLen(obj): number {
    if (!obj) {
      return 0;
    }
    return Object.keys(obj).length;
  }

  //Check the status of event in terms of is it finished, yet to start or in progress.
  checkEventIsFinished(startDate: Date, endDate: Date): number {
    if (this.refDate > startDate && this.refDate < endDate) {
      return 0;
    } else if (this.refDate > endDate) {
      return 1;
    }
    return 2;
  }


  //Get truncated representation of a string capped to a size.
  truncatedStr(str: string, size: number): string {
    if (str == null) {
      return '';
    }
    if (str.length > size) {
      return `${str.substring(0, size)}...`;
    }
    return str;
  }

  //A helper method to get personalized string if the given user is logged in one.
  getCreatedBy(user: User): string {
    if (this.currentUser.username === user.username) {
      return 'You';
    }
    return user.username;
  }

  //Check whether given user is logged in user.
  isCreatedByMe(createdBy: User): boolean {
    return this.currentUser.username === createdBy.username;
  }

  //Refresh hack
  refresh() {
    this.router.navigateByUrl('/createEvent', {skipLocationChange: true}).then(
      () => {
        this.router.navigate(['/']);
      }
    );
  }

  //Delete event
  deleteEvent(eventId: number, mine: boolean) {
    if (confirm('Are you sure to delete this Event')) {
      //Delete events created by logged in user.
      if (mine) {
        this.eventService.deleteEvent(eventId).subscribe(data => {
          this.alertService.success('Event deleted successfully!', true);
          this.refresh();
        }, error => this.alertService.error(error));
      }
      //Delete events shared by others. It will not actually delete the event it only deletes the relationship.
      else {
        this.eventService.unshareEventWithCurrentUser(eventId).subscribe(data => {
          this.alertService.success('Event unshared successfully!', true);
          this.refresh();
        }, error => this.alertService.error(error));
      }
    }
  }

  //Archives an event.
  archiveEvent(eventId: number, mine: boolean) {
    if (confirm('Are you sure to archive this Event')) {
      if (mine) {
        //Archive event created by logged in user.
        this.eventService.archiveEvent(eventId).subscribe(data => {
          this.alertService.success('Event archived successfully!', true);
          this.refresh();
        }, error => this.alertService.error(error));
      }
      //Archive event shared by others. It will not actually archive the original event it only manipulates the relationship.
      else {
        this.eventService.archiveSharedEvent(eventId).subscribe(data => {
          this.alertService.success('Event unshared successfully!', true);
          this.refresh();
        }, error => this.alertService.error(error));
      }
    }
  }

  //search an event with title.
  search(searchStr: string) {
    this.router.navigateByUrl('/createEvent', {skipLocationChange: true}).then(
      () => {
        this.router.navigate(['/'], {state: {searchStr: searchStr}});
      }
    );
  }

  //Track record of selected events
  updateOptions(eventId) {
    this.options.set(eventId, !this.options.get(eventId));
  }

  //Share selected events with given user with username.
  share(username: string) {
    this.canShare = false;
    let eventIds: string[] = [];
    //Get all the selected event ids.
    this.options.forEach((value: boolean, key: string) => {
      if (value) {
        eventIds.push(key);
      }
    });
    this.eventService.shareEvent(username, eventIds).subscribe(
      data => {
        this.alertService.success('Event(s) shared successfully', true);
        this.refresh();
      }, error => {
        this.alertService.error(error);
      }
    )
    this.canShare = true;
  }
}
