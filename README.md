# TUM FoP Tutorial Rollcall Discord Bot
## Prerequisites
- Java Runtime Environment (This application was developed under the assumption of JRE 17)
- Discord Bot account with the `GUILD_MEMBERS` intent **toggled on**

## Installation
1. Build the bot into a jar file with `./gradlew jar`
2. Move the built jar file into a suitable directory
3. Copy `config.properties.example` to the above directory and rename it to `config.properties`
4. Set all values in the configuration file as desired
5. `java -jar <name.jar>`

## Usage
This bot has one simple function - setup a rollcall and return the names of students who clicked on the "I'm here" button.  
Only server members that have a role whose ID is configured as `tutor-role-id` will be permitted to use the command. Other users will receive an error message.  

- Tutor runs `/rollcall [timeout:int]` in their respective tutorial channel, with timeout being an optional argument that specifies a timeout in minutes that deviates from the default set in the configuration file.
- The bot sends a message to the channel calling for students to mark their own attendance, using a Button interaction
- When the timer ends, the original message is deleted and the bot sends a list of all students (their Discord display names) back to the tutorial channel

## Caveats
- Student identities are not verified. Only the Display Name (nickname) of an account is recorded when the button is clicked. Authorisation solutions such as Shibboleth and TUMonline tokens are not available to me and also may cause privacy concerns.
- No persistence. Nothing is ever saved to persistent storage by the bot. All state is lost upon restart.
- Anyone with access to the channel can mark themselves as present. This means the bot relies on the honour system to mark attendances. There is nothing to gain from faking attendance, anyway.
- Tutors still have to manually transfer attendance records from the output message to Artemis.