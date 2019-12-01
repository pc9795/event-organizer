import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class EventsService {

  constructor(private httpClient: HttpClient) {
  }

  getActiveEvents() {
    return this.httpClient.get('http://localhost:8080/api/v1/events');
  }

  getArchivedEvents() {
    return this.httpClient.get('http://localhost:8080/api/v1/events/archive');
  }

  getSharedEvents() {
    return this.httpClient.get('http://localhost:8080/api/v1/events/shared');
  }

  getSharedAndArchivedEvents() {
    return this.httpClient.get('http://localhost:8080/api/v1/events/shared/archive');
  }

  getEvent(eventId: number) {
    return this.httpClient.get(`http://localhost:8080/api/v1/events/${eventId}`);
  }

  createEvent(event: Event) {
    return this.httpClient.post('http://localhost:8080/api/v1/events', event);
  }

  updateEvent(event: Event, eventId: number) {
    return this.httpClient.put(`http://localhost:8080/api/v1/events/${eventId}`, event);
  }

  deleteEvent(eventId: number) {
    return this.httpClient.delete(`http://localhost:8080/api/v1/events/${eventId}`);
  }

  archiveEvent(eventId: number) {
    return this.httpClient.patch(`http://localhost:8080/api/v1/events/${eventId}/archive`, null);
  }

  unarchiveEvent(eventId: number) {
    return this.httpClient.patch(`http://localhost:8080/api/v1/events/${eventId}/unarchive`, null);
  }

  archiveSharedEvent(eventId: number) {
    return this.httpClient.patch(`http://localhost:8080/api/v1/events/${eventId}/shared/archive`, null);
  }

  unarchiveSharedEvent(eventId: number) {
    return this.httpClient.patch(`http://localhost:8080/api/v1/events/${eventId}/shared/unarchive`, null);
  }

  shareEvent(eventId: number, userIds: number[]) {
    return this.httpClient.post(`http://localhost:8080/api/v1/events/${eventId}/share`, userIds);
  }

  unshareEvent(eventId: number, userId: number) {
    return this.httpClient.delete(`http://localhost:8080/api/v1/events/${eventId}/share/${userId}`);
  }

  unshareEventWithCurrentUser(eventId: number) {
    return this.httpClient.delete(`http://localhost:8080/api/v1/events/${eventId}/share/`);
  }

}
