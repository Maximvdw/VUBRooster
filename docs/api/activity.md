# Activity REST API
This API allows you to get specific activities or get all activities for a group, staff member or location.

## What is an activity?
An activity is an event of a student group. Usually an activity is part of a course unless the sync tool was
unable to link it to one.

## Getting an activity by id
Activities have auto incrementing identifiers starting from 1. When an activity/event changes it is not removed but
set to inactive. That means you can still query for 'older' activities.

### GET request
`https://api.vubrooster.be/activity/{id}/`

**{id}** is the identifier of the activity

### Optional parameters
`prettyPrint=true` Prints the JSON output in a pretty format

### Success response
When the identifier is found the HTTP result will be 200 (OK) and the response
will contain the activity in JSON format.

```
{
  "activity_id": 1,
  "summary": "1LSO AV: Wiskunde 1: Abstracte redeneertaal SO1 AV",
  "location": "JET\/5905",
  "start_unix": 1475672400,
  "end_unix": 1475686800,
  "start_time": "13:00",
  "end_time": "17:00",
  "weeks_label": "2",
  "groups_label": "IN WI,HB WI,FR WI,EN WI,EC WI,AA WI",
  "lesson_type": "H",
  "week": 2,
  "day": 3,
  "staff": "Verdoodt Bram",
  "last_sync": 1475482625,
  "last_update": 1475482696,
  "courses": [
    {
      ...
    },
    {
      ...
    },
    {
      ...
    },
    ...
  ],
  "studentgroups": [
    {
      ...
    },
    {
      ...
    },
    {
      ...
    },
    ...
  ]
}
```

**activity_id** This is the activity identifier
**summary** This is the activity title/summary
**location** Location/classroom of the activity
**start_unix** GMT Unix timestamp when the event starts (in seconds)*
**end_unix** GMT Unix timestamp when the event ends (in seconds)*
**start_time** The start time (HH:MM) (not required since you have the unix timestamp - but provided for convenience)
**end_time** The start time (HH:MM) (not required since you have the unix timestamp - but provided for convenience)
**weeks_label** The weeks this event is in (format can be a single number, comma separated numbers, range (2-4) or all three combined)
**groups_label** The groups that have this event (comma separated)
**lesson_type** Lesson type (usually H or W - but different variations possible)
**week** Week the event is in (not required since you have the unix timestamp - but provided for convenience)
**day** Day of the week the event is in (1=Monday,...,7=Sunday) (not required since you have the unix timestamp - but provided for convenience)
**staff** Staff member(s)
**last_sync** Last sync in seconds. This is the last time the event was 'CHECKED' with the server
**last_update** Last update in seconds. This is the last time a 'CHANGE' was made
**courses** Courses the activity belongs to - provided in a JSON array.
**studentgroups** Student groups the activity belongs to - provided in a JSON array



* Note that it is very important that the unix timestamps are GMT and not Europe/Brussels!

## Getting all activities of a student group
You can query the API to get all activities of a specific type.
One of those types is a student group.

`https://api.vubrooster.be/activity/all/group/{group_id}`

**{group_id}** This is the group id

### Optional parameters
`prettyPrint=true` Prints the JSON output in a pretty format
`week={n}` Get the activities of a specific week {n}

### Success response
```
{
    "amount":96,
    "week":-1,
    "querytime":35,
    "activities":[
        {
            "activity_id":3226,
            "summary":"Final Work - kickoff",
            "location":"DT/B.2.208",
            "start_unix":1475485200,
            "end_unix":1475496000,
            "start_time":"9:00",
            "end_time":"12:00",
            "weeks_label":"2",
            "groups_label":"3BaMultec-MAW,3BaMultec-AT",
            "lesson_type":"W",
            "week":2,
            "day":1,
            "staff":"Vermeire Jacob, Vanderzijpen Frauke, Van Den Broek Johan, Tilburgs Stefan, Steyaert Pieter, Phlypo Yorick, Heylen Maarten, Geens Arno,DT/NN/vacature,Dickx Peter,Vermeire Jacob,Vanderzijpen Frauke,Van Den Broek Johan,Tilburgs Stefan,Steyaert Pieter,Phlypo Yorick,Heylen Maarten,DT/NN/vacature,Dickx Peter,Vermeire Jacob,Vanderzijpen Frauke,Van Den Broek Johan,Tilburgs Stefan,Steyaert Pieter,Phlypo Yorick,Heylen Maarten,DT/NN/vacature,Dickx Peter,Vermeire Jacob,Vanderzijpen Frauke,Van Den Broek Johan,Tilburgs Stefan,Steyaert Pieter,Phlypo Yorick,Heylen Maarten, DT/NN/vacature, Dickx Peter",
            "last_sync":1475712146,
            "last_update":1475482698,
            "active":true
        },
        {
            "activity_id":3236,
            "summary":"Internship",
            "location":"DT/B.2.208",
            "start_unix":1476090000,
            "end_unix":1476093600,
            "start_time":"9:00",
            "end_time":"10:00",
            "weeks_label":"3",
            "groups_label":"3BaMultec-MAW,3BaMultec-AT",
            "lesson_type":"W",
            "week":3,
            "day":1,
            "staff":"Dhooge Annick",
            "last_sync":1475712146,
            "last_update":1475482698,
            "active":true
        },
        {
            ...
        },
        ...
    ]
}
```

**amount** Amount of returned activities
**week** Queried week (-1 = all weeks)
**querytime** Time it took to get results in milliseconds
**activities** This is an array with activity objects (compact: does not contain courses and studentgroups)

## Getting all activities of a staff member
You can query the API to get all activities of a specific type.
One of those types is a staff member.

`https://api.vubrooster.be/activity/all/staff/{staff_id}`

**{staff_id}** This is the staff name

### Optional parameters
`prettyPrint=true` Prints the JSON output in a pretty format
`week={n}` Get the activities of a specific week {n}

## Getting all activities of a location
You can query the API to get all activities of a specific type.
One of those types is a location.

`https://api.vubrooster.be/activity/all?location={location_id}`

**{location_id}** This is the location id

### Optional parameters
`prettyPrint=true` Prints the JSON output in a pretty format
`week={n}` Get the activities of a specific week {n}