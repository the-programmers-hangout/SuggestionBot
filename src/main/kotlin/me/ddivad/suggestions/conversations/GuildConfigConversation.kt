package me.ddivad.suggestions.conversations

import dev.kord.core.entity.Guild
import dev.kord.x.emoji.Emojis
import me.ddivad.suggestions.dataclasses.Configuration
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.arguments.RoleArg
import me.jakejmattson.discordkt.conversations.conversation

class ConfigurationConversation(private val configuration: Configuration) {
    fun createConfigurationConversation(guild: Guild) = conversation {
        val adminRole = prompt(RoleArg, "Admin role:")
        val staffRole = prompt(RoleArg, "Staff role:")
        val requiredSuggestionRole = prompt(RoleArg, "Required suggestion role:")
        val suggestionChannel = prompt(ChannelArg, "Suggestion channel:")
        val suggestionReviewChannel = prompt(ChannelArg, "Suggestion Review Channel:")

        val showVotes = promptButton {
            embed {
                color = discord.configuration.theme
                title = "Show votes on public suggestions?"
                description = """
                    Enabling this will show votes on public suggestions at all times. 
                    If disabled, votes will only show when suggestion status is something other than "Posted"
                """.trimIndent()
                field {
                    value = "This can be changed using `${configuration.prefix}toggleShowVotes`"
                }
            }
            buttons {
                button("Yes", Emojis.whiteCheckMark, true)
                button("No", Emojis.x, false)
            }
        }

        val removeReactions = promptButton {
            embed {
                color = discord.configuration.theme
                title = "Remove reactions while voting on suggestions?"
                description = """
                    Enabling this will remove reactions when users vote on suggestions. 
                    If disabled, suggestions will be removed to keep votes anonymous.
                """.trimIndent()
                field {
                    value = "This can be changed using `${configuration.prefix}toggleRemoveReactions`"
                }
            }
            buttons {
                button("Yes", Emojis.whiteCheckMark, true)
                button("No", Emojis.x, false)
            }
        }

        val sendDM = promptButton {
            embed {
                color = discord.configuration.theme
                title = "Send voting confirmation DM?"
                description = "Enabling this will send a confirmation DM to a user when they vote."
                field {
                    value = "This can be changed using `${configuration.prefix}toggleVotingDM`"
                }
            }
            buttons {
                button("Yes", Emojis.whiteCheckMark, true)
                button("No", Emojis.x, false)
            }
        }

        configuration.setup(
            guild,
            adminRole,
            staffRole,
            requiredSuggestionRole,
            suggestionChannel,
            suggestionReviewChannel,
            showVotes,
            removeReactions,
            sendDM
        )
    }
}