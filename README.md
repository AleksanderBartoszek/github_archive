# GitHub Archive App - Aleksander Bartoszek

## Application roadmap:
- get user input
- get repository id with given name for faster processing`*`
- download requested data
- unzip downloaded files
- process data separated into days
- save results

`*` - I assumed that comparing numbers is faster than strings in Spark, althought I couldn't find definite answer yet. 

## How to use it
In sbt shell with command:
`run {args}`

for example:
`run spring-projects spring-framework 1 2022 true`

## App arguments
Running app requires 5 arguments:
- repository owner - `spring-projects` [string]
- repository name - `spring-framework` [string]
- month - `1` [as a number from 1 to 12]
- year - `2022` [as a number]
- delete all .json files when finished - `true` [either true or false] => recommended true

## Output format
There is a frame in console for each day with:
- date of processed data
- processed repository name 
- count of unique usernames that starred repository
- opened pull requests for repository

```
+--------------------------+
DATE:	 7.1.2022
project name:	spring-framework
unique username stars:	58
opened pull requests:	1
+--------------------------+
```
Additionally results are stored in `results.txt` file for easy access.
Each day in separate line, with space between entries

Format: `7.1.2022 spring-framework 58 1`

## Unsafe points
- this app does NOT check user input for correctness. 
- app does NOT handle internet connection loss.

## How to upgrade
- App requires thorough testing for missed errors
- Package it into .jar
- Add handling incorrect user input
- Could use some optimizing
- There is a lot of room for new features
