# Study program REST API
This API allows you to get specific study programmes or get all study programmes in
a faculty.

## What is a study program?
A study program is a collection of groups inside a faculty that 

## Getting a study program by id
Study programmes have auto incrementing identifiers starting from 1. A study program will
be removed if it changes.

### GET request
`https://api.vubrooster.be/studyprogram/{id}/`

**{id}** is the identifier of the study program

### Optional parameters
`prettyPrint=true` Prints the JSON output in a pretty format

### Success response
When the identifier is found the HTTP result will be 200 (OK) and the response
will contain the study program in JSON format.

```
{
  "studyprogram_id": 6,
  "name": "Economische en Sociale Wetenschappen en Solvay Business School",
  "language": "DUTCH",
  "url": "http:\/\/splus.cumulus.vub.ac.be:1184\/2evenjr\/studsetES_evenjr.html",
  "faculty": {
    ...
  }
}
```

**studyprogram_id** Study program identifier
**name** Name of the study program
**language** Language of the study program (Usually Dutch)
**url** URL of the timetables of that study program
**faculty** Faculty object