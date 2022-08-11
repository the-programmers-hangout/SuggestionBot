package me.ddivad.suggestions.dataclasses

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions

object BotPermissions {
    val Admin = Permissions(Permission.Administrator)
    val Staff = Permissions(Permission.ManageMessages)
    val Everyone = Permissions(Permission.UseApplicationCommands)
}