# Student group REST API
This API allows you to get specific student groups or get all student groups in a study program.

## What is a student group?
A student group is a a group inside a study program.

## Getting a student group by id
Student groups have an ID equal to that assigned by  incrementing identifiers starting from 1. A study program will
be removed if it changes.

### GET request
`https://api.vubrooster.be/studyprogram/{id}/`

**{id}** is the identifier of the study program

### Optional parameters
`prettyPrint=true` Prints the JSON output in a pretty format

### Success response
When the identifier is found the HTTP result will be 200 (OK) and the response
will contain the student group in JSON format.

```
{
  "studentgroup_id": "1 B TEW",
  "name": "1 B TEW",
  "long_name": ""
}
```

**studentgroup_id** Student group identifier
**name** Normal name of the group
**long_name** Unfiltered name of the group (can be empty)