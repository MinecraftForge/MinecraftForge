![Forge Logo](assets/Forge_logo.svg)

MinecraftForge
=============
[![Stable Release](https://img.shields.io/badge/dynamic/json?url=https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json&label=Stable&prefix=1.20.1&query=$.promos["1.20.1-recommended"]&color=brightgreen&logo=data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyBjbGFzcz0ic3ZnLWlubGluZS0tZmEgZmEtc3RhciBmYS13LTE4IiBhcmlhLWhpZGRlbj0idHJ1ZSIgZGF0YS1pY29uPSJzdGFyIiBkYXRhLXByZWZpeD0iZmFzIiBmb2N1c2FibGU9ImZhbHNlIiByb2xlPSJpbWciIHZpZXdCb3g9IjAgMCA1NzYgNTEyIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPgo8cGF0aCBkPSJNMjU5LjMgMTcuOEwxOTQgMTUwLjIgNDcuOSAxNzEuNWMtMjYuMiAzLjgtMzYuNyAzNi4xLTE3LjcgNTQuNmwxMDUuNyAxMDMtMjUgMTQ1LjVjLTQuNSAyNi4zIDIzLjIgNDYgNDYuNCAzMy43TDI4OCA0MzkuNmwxMzAuNyA2OC43YzIzLjIgMTIuMiA1MC45LTcuNCA0Ni40LTMzLjdsLTI1LTE0NS41IDEwNS43LTEwM2MxOS0xOC41IDguNS01MC44LTE3LjctNTQuNkwzODIgMTUwLjIgMzE2LjcgMTcuOGMtMTEuNy0yMy42LTQ1LjYtMjMuOS01Ny40IDB6IiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4K)][Download]
[![Latest Release](https://img.shields.io/badge/dynamic/json?url=https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json&label=Latest&prefix=1.20.1-&query=$.promos["1.20.1-latest"]&color=blue&logo=data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyBjbGFzcz0ic3ZnLWlubGluZS0tZmEgZmEtYnVnIGZhLXctMTYiIGFyaWEtaGlkZGVuPSJ0cnVlIiBkYXRhLWljb249ImJ1ZyIgZGF0YS1wcmVmaXg9ImZhcyIgZm9jdXNhYmxlPSJmYWxzZSIgcm9sZT0iaW1nIiB2aWV3Qm94PSIwIDAgNTEyIDUxMiIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTUxMS45ODggMjg4LjljLS40NzggMTcuNDMtMTUuMjE3IDMxLjEtMzIuNjUzIDMxLjFINDI0djE2YzAgMjEuODY0LTQuODgyIDQyLjU4NC0xMy42IDYxLjE0NWw2MC4yMjggNjAuMjI4YzEyLjQ5NiAxMi40OTcgMTIuNDk2IDMyLjc1OCAwIDQ1LjI1NS0xMi40OTggMTIuNDk3LTMyLjc1OSAxMi40OTYtNDUuMjU2IDBsLTU0LjczNi01NC43MzZDMzQ1Ljg4NiA0NjcuOTY1IDMxNC4zNTEgNDgwIDI4MCA0ODBWMjM2YzAtNi42MjctNS4zNzMtMTItMTItMTJoLTI0Yy02LjYyNyAwLTEyIDUuMzczLTEyIDEydjI0NGMtMzQuMzUxIDAtNjUuODg2LTEyLjAzNS05MC42MzYtMzIuMTA4bC01NC43MzYgNTQuNzM2Yy0xMi40OTggMTIuNDk3LTMyLjc1OSAxMi40OTYtNDUuMjU2IDAtMTIuNDk2LTEyLjQ5Ny0xMi40OTYtMzIuNzU4IDAtNDUuMjU1bDYwLjIyOC02MC4yMjhDOTIuODgyIDM3OC41ODQgODggMzU3Ljg2NCA4OCAzMzZ2LTE2SDMyLjY2NkMxNS4yMyAzMjAgLjQ5MSAzMDYuMzMuMDEzIDI4OC45LS40ODQgMjcwLjgxNiAxNC4wMjggMjU2IDMyIDI1Nmg1NnYtNTguNzQ1bC00Ni42MjgtNDYuNjI4Yy0xMi40OTYtMTIuNDk3LTEyLjQ5Ni0zMi43NTggMC00NS4yNTUgMTIuNDk4LTEyLjQ5NyAzMi43NTgtMTIuNDk3IDQ1LjI1NiAwTDE0MS4yNTUgMTYwaDIyOS40ODlsNTQuNjI3LTU0LjYyN2MxMi40OTgtMTIuNDk3IDMyLjc1OC0xMi40OTcgNDUuMjU2IDAgMTIuNDk2IDEyLjQ5NyAxMi40OTYgMzIuNzU4IDAgNDUuMjU1TDQyNCAxOTcuMjU1VjI1Nmg1NmMxNy45NzIgMCAzMi40ODQgMTQuODE2IDMxLjk4OCAzMi45ek0yNTcgMGMtNjEuODU2IDAtMTEyIDUwLjE0NC0xMTIgMTEyaDIyNEMzNjkgNTAuMTQ0IDMxOC44NTYgMCAyNTcgMHoiIGZpbGw9IndoaXRlIi8+Cjwvc3ZnPgo=
)][Download] [![Discord](https://img.shields.io/discord/313125603924639766.svg?color=%237289da&label=Discord&logo=discord&logoColor=%237289da)][Discord] [![Support](https://img.shields.io/badge/Patreon-Support-orange.svg?logo=Patreon)][Patreon]

Forge is a free, open-source modding API all of your favourite mods use!

| Version |    Support    |
|---------| ------------- |
| 1.20.x  |    Active     |
| 1.19.x  |    Legacy     |

* [Download]
* [Forum]
* [Discord]
* [Documentation]

# Installing Forge

Go to [the Forge website][Download]
 and select the Minecraft version you wish to get Forge for from the list.

You can download the installer for the *Recommended Build* or the
 *Latest build* there. Latest builds may have newer features but may be
 more unstable as a result. The installer will attempt to install Forge
 into your vanilla launcher environment, where you can then create a new
 profile using that version and play the game!
 
For support and questions, visit [the Support Forum][Support-Forum] or [the Forge Discord server][Discord].

# Creating Mods

[See the "Getting Started" section in the Forge Documentation][Getting-Started].

# Contribute to Forge

If you wish to actually inspect Forge, submit PRs or otherwise work
 with Forge itself, you're in the right place!

 [See the guide to setting up a Forge workspace][ForgeDev].

### Pull requests

[See the "Making Changes and Pull Requests" section in the Forge documentation][Pull-Requests].

Please read the contributing guidelines found [here][Contributing] before making a pull request.

### Contributor License Agreement
We require all contributors to acknowledge the [Forge Contributor License Agreement][CLA]. 
Please ensure you have a valid email address associated with your GitHub account to do this. If you have previously 
 signed it, you should be OK.

#### Donate
*Forge is a large project with many collaborators working on it around the clock. Forge is and will always remain free 
 to use and modify. However, it costs money to run such a large project as this, so please consider 
 [becoming a patron][Patreon].*

[Contributing]: ./CONTRIBUTING.md
[CLA]: https://cla-assistant.io/MinecraftForge/MinecraftForge

[Download]: https://files.minecraftforge.net/

[Forum]: https://forums.minecraftforge.net/
[Support-Forum]: https://forums.minecraftforge.net/forum/18-support-bug-reports/

[Discord]: https://discord.minecraftforge.net/

[Documentation]: https://docs.minecraftforge.net/
[Getting-Started]: https://docs.minecraftforge.net/en/latest/gettingstarted/
[ForgeDev]: https://docs.minecraftforge.net/en/latest/forgedev/
[Pull-Requests]: https://docs.minecraftforge.net/en/latest/forgedev/#making-changes-and-pull-requests

[Patreon]: https://www.patreon.com/LexManos
