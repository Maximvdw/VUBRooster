# Faculty REST API
This API allows you to get specific faculties or get all faculties.

## What is a faculty?
A faculty or department is a group containing different study programmes of the same category. Faculties do not sync
like study programmes or groups. They are fetched when the application starts and usually do not change.

## Getting a faculty by id
Faculties have auto incrementing identifiers starting from 1.

### GET request
`https://api.vubrooster.be/faculty/{id}/`

**{id}** is the identifier of the faculty

### Success response
When the identifier is found the HTTP result will be 200 (OK) and the response
will contain the faculty in JSON format.

```
{
  "faculty_id": 1,
  "name_dutch": "Design & Technologie",
  "name_english": "",
  "faculty_code": "DT",
  "url_english": "",
  "url_dutch": ""
}
```

**faculty_id** Faculty identifier
**name_dutch** Faculty name in Dutch
**name_english** Faculty name in English (can be empty)
**faculty_code** Short code of the faculty
**url_english** English URL to the timetables of that faculty (can be empty)
**url_dutch** Dutch URL to the timetables of that faculty (can be empty)

## Getting a faculty by code
Faculties have auto incrementing identifiers starting from 1.

### GET request
`https://api.vubrooster.be/faculty/code/{code}/`

**{code}** is the short code of the faculty

### Optional parameters
`prettyPrint=true` Prints the JSON output in a pretty format

### Success response
When the code is found the HTTP result will be 200 (OK) and the response
will contain the activity in JSON format.

```
{
  "faculty_id": 1,
  "name_dutch": "Design & Technologie",
  "name_english": "",
  "faculty_code": "DT",
  "url_english": "",
  "url_dutch": ""
}
```

**faculty_id** Faculty identifier
**name_dutch** Faculty name in Dutch
**name_english** Faculty name in English (can be empty)
**faculty_code** Short code of the faculty
**url_english** English URL to the timetables of that faculty (can be empty)
**url_dutch** Dutch URL to the timetables of that faculty (can be empty)

## Getting all faculties

`https://api.vubrooster.be/faculty/all/`