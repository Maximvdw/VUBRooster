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

`https://api.vubrooster.be/activity/all?group={group_id}`

**{group_id}** This is the group id

### Optional parameters
`prettyPrint=true` Prints the JSON output in a pretty format

### Success response
```
[
  {
    "activity_id": 3669,
    "summary": "Onthaal nieuwe studenten",
    "location": "Q.D, Q.C, Q.A.Van.Geen, Q.B",
    "start_unix": 1474878600,
    "end_unix": 1474884000,
    "start_time": "08:30",
    "end_time": "10:00",
    "weeks_label": "2",
    "groups_label": "",
    "lesson_type": "",
    "week": 2,
    "day": 1,
    "staff": "",
    "last_sync": 1475677075,
    "last_update": 1475677173,
    "active": true
  },
  {
    "activity_id": 33398,
    "summary": "Onthaal TEW &amp; HI",
    "location": "Q.D",
    "start_unix": 1474889400,
    "end_unix": 1474894800,
    "start_time": "11:30",
    "end_time": "13:00",
    "weeks_label": "2",
    "groups_label": "",
    "lesson_type": "",
    "week": 2,
    "day": 1,
    "staff": "BRANSON Joel",
    "last_sync": 1475677162,
    "last_update": 1475677192,
    "active": true
  },
  {
     ...
  },
  ...
]
```

The activity result does not contain a collection of courses and student groups
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

## Getting all activities of a staff member
You can query the API to get all activities of a specific type.
One of those types is a staff member.

`https://api.vubrooster.be/activity/all?staff={staff_id}`

**{staff_id}** This is the staff name

### Optional parameters
`prettyPrint=true` Prints the JSON output in a pretty format

### Success response
```
[
    {
        ...
    },
    {
        ...
    },
    ...
]
```

The activity result is the same as "Getting all activities of a student group"

## Getting all activities of a location
You can query the API to get all activities of a specific type.
One of those types is a location.

`https://api.vubrooster.be/activity/all?location={location_id}`

**{location_id}** This is the location id

### Optional parameters
`prettyPrint=true` Prints the JSON output in a pretty format

### Success response
```
[
    {
        ...
    },
    {
        ...
    },
    ...
]
```

The activity result is the same as "Getting all activities of a student group"