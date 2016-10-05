# Course REST API
This API allows you to get specific courses or get all courses for a group.

## What is a course?
A course is part of a collection of courses belonging to a student group.

## Getting a course by id


### GET request
`https://api.vubrooster.be/course/{id}/`

**{id}** is the identifier of the course

### Optional parameters
`prettyPrint=true` Prints the JSON output in a pretty format

### Success response
When the identifier is found the HTTP result will be 200 (OK) and the response
will contain the course in JSON format.

```
{
  "course_id": "716087",
  "name": "Audiovisual & IT Principles MCT1",
  "long_name": "DT\/EIPBAMCT\/1\/1BaMCT A@S1\/Audiovisual & IT Principles MCT1"
}
```

**course_id** Course identifier
**name** Name of the course
**long_name** Long name of the course (unfiltered)