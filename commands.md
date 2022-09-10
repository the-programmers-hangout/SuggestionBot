# Commands

## Key 
| Symbol      | Meaning                        |
|-------------|--------------------------------|
| [Argument]  | Argument is not required.      |
| /Category   | This is a subcommand group.    |

## /Configuration
| Commands              | Arguments                                                                  | Description                                                  |
|-----------------------|----------------------------------------------------------------------------|--------------------------------------------------------------|
| setChannel            | ChannelType, Channel                                                       | Set the review or public channel to be used for suggestions. |
| setSuggstionRole      | Role                                                                       | Set the minimum required role to make a suggestion.          |
| setup                 | SuggestionChannel, ReviewChannel, ShowVotes, ShowReactions, ConfirmationDM | Configure a guild to use this bot.                           |
| toggleRemoveReactions |                                                                            | Toggle reactions being removed on in-progress suggestions.   |
| toggleShowVotes       |                                                                            | Toggle votes being displayed on in-progress suggestions.     |
| toggleVotingDM        |                                                                            | Toggle DMs being sent upon voting for a suggestions.         |
| view                  |                                                                            | View guild configuration                                     |

## Suggestions
| Commands  | Arguments  | Description                                                      |
|-----------|------------|------------------------------------------------------------------|
| setStatus | ID, Status | Set the status for a suggestion (backup for interaction buttons) |
| stats     |            | Get stats about guild suggestions                                |
| suggest   | Suggestion | Make a suggestion.                                               |

## Utility
| Commands | Arguments | Description                 |
|----------|-----------|-----------------------------|
| Help     | [Command] | Display a help menu.        |
| info     |           | Bot info for Suggestion-Bot |

