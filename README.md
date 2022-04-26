# GitHub Archive App - Aleksander Bartoszek

## Application trail:
- get user input
- get repository id from given name for faster processing`*`
- download requested data
- unzip downloaded files
- process data separated into days
- save results

`*` - I assumed that comparing numbers is faster than strings in Spark, althought I couldn't find definite answer yet. 

## How to use it
I didn't manage to solve all problems with dependencies creating .jar package in time, so app is currently accessible by running it in sbt shell or using IDE (in Intellij make sure that "Add dependencies with "provided" scope to classpath" in run configuration is checked)

Command to run it in sbt shell:
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
- Could use some tidying for more readable code
- Could use some optimizing
- There is a lot of room for new features