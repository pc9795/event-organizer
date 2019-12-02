import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Event} from '../models/event';

@Injectable({
  providedIn: 'root'
})
export class EventsService {

  constructor(private httpClient: HttpClient) {
  }

  getActiveEvents() {
    return this.httpClient.get('http://localhost:8080/api/v1/events', {withCredentials: true});
  }

  getArchivedEvents() {
    return this.httpClient.get('http://localhost:8080/api/v1/events/archive', {withCredentials: true});
  }

  getEvent(eventId: number) {
    return this.httpClient.get(`http://localhost:8080/api/v1/events/${eventId}`, {withCredentials: true});
  }

  createEvent(event: Event) {
    return this.httpClient.post('http://localhost:8080/api/v1/events', event, {withCredentials: true});
  }

  updateEvent(event: Event, eventId: number) {
    return this.httpClient.put(`http://localhost:8080/api/v1/events/${eventId}`, event, {withCredentials: true});
  }

  deleteEvent(eventId: number) {
    return this.httpClient.delete(`http://localhost:8080/api/v1/events/${eventId}`, {withCredentials: true});
  }

  archiveEvent(eventId: number) {
    return this.httpClient.patch(`http://localhost:8080/api/v1/events/${eventId}/archive`, null, {withCredentials: true});
  }

  unarchiveEvent(eventId: number) {
    return this.httpClient.patch(`http://localhost:8080/api/v1/events/${eventId}/unarchive`, null, {withCredentials: true});
  }

  archiveSharedEvent(eventId: number) {
    return this.httpClient.patch(`http://localhost:8080/api/v1/events/${eventId}/shared/archive`, null, {withCredentials: true});
  }

  unarchiveSharedEvent(eventId: number) {
    return this.httpClient.patch(`http://localhost:8080/api/v1/events/${eventId}/shared/unarchive`, null, {withCredentials: true});
  }

  shareEvent(eventId: number, userIds: number[]) {
    return this.httpClient.post(`http://localhost:8080/api/v1/events/${eventId}/share`, userIds, {withCredentials: true});
  }

  unshareEvent(eventId: number, userId: number) {
    return this.httpClient.delete(`http://localhost:8080/api/v1/events/${eventId}/unshare/${userId}`, {withCredentials: true});
  }

  unshareEventWithCurrentUser(eventId: number) {
    return this.httpClient.delete(`http://localhost:8080/api/v1/events/${eventId}/unshare/`, {withCredentials: true});
  }

}
