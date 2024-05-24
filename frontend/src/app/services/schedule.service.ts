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
      "month": 6,
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
                  "name": "D1",
                  "hexcode": "#FFA07A",
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
                  "name": "N1",
                  "hexcode": "#20B2AA",
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
                  "name": "N2",
                  "hexcode": "#2F4F4F",
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
                  "name": "D1",
                  "hexcode": "#FFA07A",
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
                  "name": "N1",
                  "hexcode": "#20B2AA",
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
                  "name": "D1",
                  "hexcode": "#FFA07A",
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
                  "name": "N2",
                  "hexcode": "#2F4F4F",
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
                  "hexcode": "#20B2AA",
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
                  "name": "D1",
                  "hexcode": "#FFA07A",
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
                  "hexcode": "#2F4F4F",
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
                  "name": "N1",
                  "hexcode": "#20B2AA",
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
                  "name": "D1",
                  "hexcode": "#FFA07A",
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
                  "name": "N2",
                  "hexcode": "#2F4F4F",
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
                  "name": "N1",
                  "hexcode": "#20B2AA",
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
                  "name": "D1",
                  "hexcode": "#FFA07A",
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
                  "name": "N1",
                  "hexcode": "#20B2AA",
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
                  "name": "N2",
                  "hexcode": "#2F4F4F",
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
                  "name": "D1",
                  "hexcode": "#FFA07A",
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
                  "name": "N1",
                  "hexcode": "#20B2AA",
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
                  "name": "D1",
                  "hexcode": "#FFA07A",
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
                  "name": "N2",
                  "hexcode": "#2F4F4F",
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
                  "hexcode": "#20B2AA",
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
                  "name": "D1",
                  "hexcode": "#FFA07A",
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
                  "hexcode": "#2F4F4F",
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
                  "name": "N1",
                  "hexcode": "#20B2AA",
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
                  "name": "D1",
                  "hexcode": "#FFA07A",
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
                  "name": "N2",
                  "hexcode": "#2F4F4F",
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
                  "name": "N2",
                  "hexcode": "#2F4F4F",
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
                  "name": "N2",
                  "hexcode": "#2F4F4F",
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
                  "name": "N2",
                  "hexcode": "#2F4F4F",
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
                  "name": "N1",
                  "hexcode": "#20B2AA",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        },
        {
          "day": 15,
          "shifts": [
            {
              "employee": {
                "firstname": "Sophia",
                "lastname": "Taylor",
                "working_percentage": 0.7,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "D1",
                  "hexcode": "#FFA07A",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Ethan",
                "lastname": "Williams",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#20B2AA",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        },
        {
          "day": 16,
          "shifts": [
            {
              "employee": {
                "firstname": "Olivia",
                "lastname": "Jones",
                "working_percentage": 0.9,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#2F4F4F",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Mason",
                "lastname": "Davis",
                "working_percentage": 0.8,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "D1",
                  "hexcode": "#FFA07A",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 17,
          "shifts": [
            {
              "employee": {
                "firstname": "Emma",
                "lastname": "Miller",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#20B2AA",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Logan",
                "lastname": "Garcia",
                "working_percentage": 0.7,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "D1",
                  "hexcode": "#FFA07A",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 18,
          "shifts": [
            {
              "employee": {
                "firstname": "Ava",
                "lastname": "Martinez",
                "working_percentage": 0.9,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#2F4F4F",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "James",
                "lastname": "Hernandez",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#20B2AA",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        },
        {
          "day": 19,
          "shifts": [
            {
              "employee": {
                "firstname": "Sophia",
                "lastname": "Taylor",
                "working_percentage": 0.7,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "D1",
                  "hexcode": "#FFA07A",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Ethan",
                "lastname": "Williams",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#2F4F4F",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            }
          ]
        },
        {
          "day": 20,
          "shifts": [
            {
              "employee": {
                "firstname": "Olivia",
                "lastname": "Jones",
                "working_percentage": 0.9,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#20B2AA",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Mason",
                "lastname": "Davis",
                "working_percentage": 0.8,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "D1",
                  "hexcode": "#FFA07A",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 21,
          "shifts": [
            {
              "employee": {
                "firstname": "Emma",
                "lastname": "Miller",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#2F4F4F",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Logan",
                "lastname": "Garcia",
                "working_percentage": 0.7,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#20B2AA",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        },
        {
          "day": 22,
          "shifts": [
            {
              "employee": {
                "firstname": "Ava",
                "lastname": "Martinez",
                "working_percentage": 0.9,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "D1",
                  "hexcode": "#FFA07A",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "James",
                "lastname": "Hernandez",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#2F4F4F",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            }
          ]
        },
        {
          "day": 23,
          "shifts": [
            {
              "employee": {
                "firstname": "Sophia",
                "lastname": "Taylor",
                "working_percentage": 0.7,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#20B2AA",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Ethan",
                "lastname": "Williams",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "D1",
                  "hexcode": "#FFA07A",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 24,
          "shifts": [
            {
              "employee": {
                "firstname": "Olivia",
                "lastname": "Jones",
                "working_percentage": 0.9,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#2F4F4F",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Mason",
                "lastname": "Davis",
                "working_percentage": 0.8,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#20B2AA",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        },
        {
          "day": 25,
          "shifts": [
            {
              "employee": {
                "firstname": "Emma",
                "lastname": "Miller",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "D1",
                  "hexcode": "#FFA07A",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Logan",
                "lastname": "Garcia",
                "working_percentage": 0.7,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#2F4F4F",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            }
          ]
        },
        {
          "day": 26,
          "shifts": [
            {
              "employee": {
                "firstname": "Ava",
                "lastname": "Martinez",
                "working_percentage": 0.9,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#20B2AA",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "James",
                "lastname": "Hernandez",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "D1",
                  "hexcode": "#FFA07A",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 27,
          "shifts": [
            {
              "employee": {
                "firstname": "Sophia",
                "lastname": "Taylor",
                "working_percentage": 0.7,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#2F4F4F",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Ethan",
                "lastname": "Williams",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#20B2AA",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            }
          ]
        },
        {
          "day": 28,
          "shifts": [
            {
              "employee": {
                "firstname": "Olivia",
                "lastname": "Jones",
                "working_percentage": 0.9,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "D1",
                  "hexcode": "#FFA07A",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Mason",
                "lastname": "Davis",
                "working_percentage": 0.8,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#2F4F4F",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            }
          ]
        },
        {
          "day": 29,
          "shifts": [
            {
              "employee": {
                "firstname": "Emma",
                "lastname": "Miller",
                "working_percentage": 0.8,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#20B2AA",
                  "start_time": "16:00",
                  "end_time": "00:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "Logan",
                "lastname": "Garcia",
                "working_percentage": 0.7,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "D1",
                  "hexcode": "#FFA07A",
                  "start_time": "08:00",
                  "end_time": "16:00"
                }
              }
            }
          ]
        },
        {
          "day": 30,
          "shifts": [
            {
              "employee": {
                "firstname": "Ava",
                "lastname": "Martinez",
                "working_percentage": 0.9,
                "role": "nurse"
              },
              "shift": {
                "type": {
                  "name": "N2",
                  "hexcode": "#2F4F4F",
                  "start_time": "00:00",
                  "end_time": "08:00"
                }
              }
            },
            {
              "employee": {
                "firstname": "James",
                "lastname": "Hernandez",
                "working_percentage": 1.0,
                "role": "qualified nurse"
              },
              "shift": {
                "type": {
                  "name": "N1",
                  "hexcode": "#20B2AA",
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
