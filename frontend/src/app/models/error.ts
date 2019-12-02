/**
 * Class to represent standard error packet received from server.
 */
export class Error {
  constructor(public code: number, public message: string) {
  }
}
