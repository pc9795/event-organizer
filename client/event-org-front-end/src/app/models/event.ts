export class Event {
  constructor(public id: number, public title: string, public description: string, public location: string,
              public startTime: Date, public endTime: Date) {
  }
}
