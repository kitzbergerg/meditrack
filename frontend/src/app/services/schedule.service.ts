import {Injectable} from "@angular/core";

import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {

  constructor(private http: HttpClient) { }

  // Mock method to get schedule data
  getSchedule(): Observable<any> {
    const scheduleData = {
      "month": "June",
      "year": 2024,
      "days": [
        {
          "day": 1,
          "shifts": [
            {
              "employee": {
                "firstname": "John",
                "lastname": "Doe",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#FF5733",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Jane",
                "lastname": "Smith",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#33FF57",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        },
        {
          "day": 2,
          "shifts": [
            {
              "employee": {
                "firstname": "Alice",
                "lastname": "Johnson",
                "working_percentage": 0.6,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N3",
                  "hexcode": "#3357FF",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Bob",
                "lastname": "Brown",
                "working_percentage": 0.9,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#FF33A1",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 3,
          "shifts": [
            {
              "employee": {
                "firstname": "John",
                "lastname": "Doe",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#FF5733",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Jane",
                "lastname": "Smith",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#33FF57",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 4,
          "shifts": [
            {
              "employee": {
                "firstname": "Alice",
                "lastname": "Johnson",
                "working_percentage": 0.6,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N3",
                  "hexcode": "#3357FF",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Bob",
                "lastname": "Brown",
                "working_percentage": 0.9,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#FF33A1",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        },
        {
          "day": 5,
          "shifts": [
            {
              "employee": {
                "firstname": "John",
                "lastname": "Doe",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#FF5733",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Jane",
                "lastname": "Smith",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N3",
                  "hexcode": "#33FF57",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            }
          ]
        },
        {
          "day": 6,
          "shifts": [
            {
              "employee": {
                "firstname": "Alice",
                "lastname": "Johnson",
                "working_percentage": 0.6,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#3357FF",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Bob",
                "lastname": "Brown",
                "working_percentage": 0.9,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#FF33A1",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 7,
          "shifts": [
            {
              "employee": {
                "firstname": "John",
                "lastname": "Doe",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N3",
                  "hexcode": "#FF5733",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Jane",
                "lastname": "Smith",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#33FF57",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        },
        {
          "day": 8,
          "shifts": [
            {
              "employee": {
                "firstname": "John",
                "lastname": "Doe",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#FF5733",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Jane",
                "lastname": "Smith",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#33FF57",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        },
        {
          "day": 9,
          "shifts": [
            {
              "employee": {
                "firstname": "Alice",
                "lastname": "Johnson",
                "working_percentage": 0.6,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N3",
                  "hexcode": "#3357FF",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Bob",
                "lastname": "Brown",
                "working_percentage": 0.9,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#FF33A1",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 10,
          "shifts": [
            {
              "employee": {
                "firstname": "John",
                "lastname": "Doe",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#FF5733",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Jane",
                "lastname": "Smith",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#33FF57",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 11,
          "shifts": [
            {
              "employee": {
                "firstname": "Alice",
                "lastname": "Johnson",
                "working_percentage": 0.6,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N3",
                  "hexcode": "#3357FF",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Bob",
                "lastname": "Brown",
                "working_percentage": 0.9,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#FF33A1",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        },
        {
          "day": 12,
          "shifts": [
            {
              "employee": {
                "firstname": "John",
                "lastname": "Doe",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#FF5733",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Jane",
                "lastname": "Smith",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N3",
                  "hexcode": "#33FF57",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            }
          ]
        },
        {
          "day": 13,
          "shifts": [
            {
              "employee": {
                "firstname": "Alice",
                "lastname": "Johnson",
                "working_percentage": 0.6,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#3357FF",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Bob",
                "lastname": "Brown",
                "working_percentage": 0.9,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#FF33A1",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 14,
          "shifts": [
            {
              "employee": {
                "firstname": "John",
                "lastname": "Doe",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N3",
                  "hexcode": "#FF5733",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Johnny",
                "lastname": "Doe",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N3",
                  "hexcode": "#FF5733",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Alina",
                "lastname": "Grassauer",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N3",
                  "hexcode": "#FF5733",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Lucas",
                "lastname": "Wolkersdorfer",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N3",
                  "hexcode": "#FF5733",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Jane",
                "lastname": "Smith",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#33FF57",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        }
      ]
    };
    return of(scheduleData);
  }
}
