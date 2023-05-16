Getting Support Surrounding Forge
=================================

**If you do not want to read the following wall of text, no matter its relevance for helping get your issue resolved quickly, please refer to the [Forge Forums][Bug-Reports] for support.**

Before reporting an issue on this repository's bug tracker, please carefully read this document. There are various resources that are more likely to be able to help you with certain kinds of issues. This document provides instructions on how to determine which place best to report to.

*Note: While this guide generally is more geared towards end-user problems, modders should also read it before reporting any issue with Forge in order to ensure it actually is one. This does of course not cover feature suggestions.*

### Contents

 1. [Identifying the Source of a Problem](#identifying-the-source-of-a-problem)
    1. [Analyzing a Stacktrace](#analyzing-a-stacktrace)
    2. [Isolating an Issue](#isolating-an-issue)
 2. [Choosing the Correct Support Channel](#choosing-the-correct-support-channel)
    1. [Forge Support](#forge-support)
    2. [Mod Support](#mod-support)
    3. [Vanilla Support](#vanilla-support)
 3. [Writing a Helpful Bug Report](#writing-a-helpful-bug-report)
    1. [Preventing Duplicates and Unnecessary Reports](#preventing-duplicates-and-unnecessary-reports)
    2. [Required Information](#required-information)
    3. [Markdown Issue Template](#markdown-issue-template)
    4. [Waiting for an Issue to Be Resolved](#waiting-for-an-issue-to-be-resolved)

Identifying the Source of a Problem
-----------------------------------

You're most likely to get fast and valuable responses to your issues if you report them in the correct place. For this, you need to find the root cause of the problem.

The most important source for this information are the log files Forge creates for every run of the game. You can find them in the `logs` subdirectory of your Minecraft game directory. The most important log file is `latest.log` which contains all of Forge's and any mod's output for `info` of above. `debug.log` contains all the logs in `latest.log` plus the `debug` logs for more detailed debugging.

If you're faced by a crash, consult these logs files first. Near the end of the log should be a section labelled 'Minecraft Crash Report', and after it, one about `a detailed walkthrough of the error, ...`. In these sections, you'll see a bunch of lines that look like

```
at some.words.related.to.a.mod.MoreWords(MoreWords.java:number)
```

These lines are the most important part of a crash log, called the stacktrace, because they pinpoint the place in the code where something went irrecoverably wrong. Mod authors usually name their code so that these lines describe what the game was doing when it crashed. The further down one of these lines is, the earlier that piece of code ran, so at the bottom you'll see the place where Minecraft started, and at the top is where it stopped. You should use this information to figure out who to report the bug to.

### Analyzing a Stacktrace

You can generally tell pretty quickly what caused the error at the head of your stacktrace, simply follow these rules:

 1. If you can find the name of a mod in the trace, that mod most likely is the culprit.
    - Be aware that you may find multiple mods as participants of a crash. In that case, you can either report to both or try to reason (using the names in the stacktrace) about which mod is at fault.
 2. If you can only find Forge (usually starting with `net.minecraftforge`) alongside Vanilla (usually starting with `net.minecraft`) references, chances are that you have found a bug in Forge. In this case, use one of the various [support channels](#forge-support) listed below.
    - The occurrence of a single call to Forge somewhere in the stacktrace *does not* warrant a report to Forge if there are mods present in the crash report.
 3. If there's no mention of Forge or a mod, but any Vanilla class, you might have found a Vanilla bug. You should probably still report it via the Forge channels first, however, due the nature of modded environments and the rarity of Vanilla crash bugs.
 4. Always check whether there's a mention of coremods at the beginning of a crash report. If there is and you can't find any clear culprit, consider reporting to these coremods first, since they are free to change any of the code executed in your environment.

The rules are numbered in decreasing priority, which means that you should always prefer finding a mod in a crash report over directly jumping to conclusions about Forge or even Vanilla.

### Isolating an Issue

A significant step of reporting an issue, after having found the involved parties, is reliably reproducing it to identify potential incompatibilities or weird interactions between mods. If you're unable to pinpoint a bug to a set of mods and just randomly report it to any mod, chances are it won't get much attention and hence not a fix.

Reliable methods of isolating the issue are listed below:

 - **Retrace your last steps before the crash/bug occurred.** It might sound too simple, but a big part of reliably reproducing an issue is determining the steps required to trigger it. If you can't exactly remember what you last did, looking at the stacktrace again can also help you a bit, since there are often descriptive names in there which reflect an action you could take, such as using an item or placing a block.
 - **Reduce the number of mod interactions.** You can achieve this in multiple ways. One of them is simply disabling any compatibility layers between involved mods via configuration files. The other one, which takes a little more effort, is actually removing mods from your instance. But don't be afraid, you won't have to remove mods one-by-one if you follow these steps (known as a binary search):
   1. Move half the mods out of your instance (obviously keep those definitely participating in the issue).
   2. If the issue persists after another try of reproducing it, repeat from step 1 one by halving the mod list again.
   3. If the issue does not occur anymore without the other half, repeat from step 1 but instead discard the half you just used and split the other one (plus any definitely required mods).
   4. Repeat these steps until you can't remove any more mods from your instance without also 'solving' the issue. This set of mods can serve as a basis for your issue report.

Choosing the Correct Support Channel
-------------------------------------

The place you need to report issues to varies depending on the source of the problem. Below you can find a list of support channels you can usually consult for each of the sources listed in the previous section.

Please note that this list has to be considered incomplete as there is a large number of sub-communities within the Minecraft modding world. You should always prefer channels you know and have access to over any of those listed below which would require extra effort on your part.

### Forge Support

If you're certain you've found an issue that is directly caused by Forge, you have the following three platforms available for dealing with it, with decreasing relevance for end-users:

 1. **Forge Forums:** There's a dedicated [Support & Bug Report][Bug-Reports] section on the Forge forums, which is led by team of moderators and volunteers who will try to deal with your reports as quickly as possible. *As a player you should probably report here first.*
   - For modders, there also is a separate [Modder Support][Modder-Support] section where you can ask all your development-related questions. *You should prefer this section for coding help.*
 2. **Forge Discord Server:** You can find a lot of help on the official [Discord Server][Discord], especially smaller issues that you quickly need help with. *Be aware that the channel is more suited towards development questions, so while you may get support there as a player, you should almost always prefer the forums.*
 3. **Forge Issue Tracker:** If you're absolutely certain that you've found an issue in the Forge codebase (e.g. you're a modder yourself and have analyzed the stacktrace and relevant code), don't refrain from reporting it on this repository's [issue tracker](https://github.com/MinecraftForge/MinecraftForge/issues). *Please do not use the issues as a means of getting coding help. For developers, the IRC and the [official documentation][Documentation] are the best sources of information.*

### Mod Support

A lot of mods also use GitHub or other source management systems and their integrated issue trackers. The following options will give you the best chances of reporting your issue in the right place:

 - **CurseForge:** Many mods are hosted on the [CurseForge platform](https://minecraft.curseforge.com) nowadays. Chances are you already downloaded them from there or as a part of a modpack hosted there. Projects may specify an issue reporting link on their own page, so simply look for it on the mod's project page and you should be directed to the preferred means of error reporting.
 - **Modrinth:** Some modders use [Modrinth](https://modrinth.com/mods) to host their mods, but be mindful that not all mods on this platform are for the Forge modloader.
 - **Searching for a repository directly:** You may need to try and find a repository directly on source control sites. The most common ones are [GitHub](https://github.com), [GitLab](https://gitlab.com), and [BitBucket](https://bitbucket.org). Both come with search capabilities, so simply try to search for the mod's name.

### Vanilla Support

Just like Forge or mods, Mojang has its own issue tracker for Vanilla Minecraft. This official [bug tracker](https://bugs.mojang.com/projects/MC/issues/) has the exclusive purpose of managing any concrete issues with the game's code and functionality. It is *not* a place for requesting any features or getting help with playing the game, similar to the Forge issue tracker.

Writing a Helpful Bug Report
----------------------------

Now that you've identified the source of your issue and decided on the correct support channel, it's time to actually write your report. Before you actually try to blindly create it, however, you should first check if the problem was noticed or even fixed already.

### Preventing Duplicates and Unnecessary Reports
Use the target issue tracker's search functionality to look for issues which have descriptions similar to yours, but make sure to also search for already closed issues. If you can find one, verify it is in fact the same (or a very similar issue) and simply comment on it if you have anything else to contribute to the discussion, such as other steps to reproduce it. *Do not create duplicates of an existing issue, since it only puts extra effort on maintainers to recognize and close/mark them.*

Additionally, always check the *latest* changelogs of Forge and participating mods to find out if the bug was already noticed and fixed by the developer. Often the maintainer may only support newer versions of the game, so also check the changelogs for versions of Minecraft you don't actually actively play. If the issue was addressed already, *do not report it*, even if the fix only exists for a newer version of the game, since the developer probably had a good reason to do so. If your issue only exists with versions of a mod that are for very old versions of the game, also refrain from reporting them, considering most modders only support the two latest versions of Minecraft, if at all.

### Required Information
If you're certain that your issue went unnoticed up until now, you can get to actually writing up the report. Below you can find a list of the *required* information any bug report should contain:

 - **Minecraft Version:** Although many developers can deduce the Minecraft version in use from the mod's version, it is still an important piece of information to include, since it helps maintainers more easily decide whether to look into your issue at all or whether to close it right away due to being for an outdated version.
 - **Forge Version:** The Forge API is in constant flux and while breaking changes are limited to major Minecraft updates, new bugs or regressions might creep in during active development or newer versions than a mod was built with might change semantics slightly that manifest themselves for this mod.
 - **Mod Version:** Just like the Forge and Minecraft version, this is crucial for quickly telling where an issue might come from. *If you're reporting an issue caused by Forge that only occurs with a certain mod installed, include this mod here. No mod version is required if it's a pure Forge bug.*
 - **Complete List of Mods:** In order to reliably analyze a bug, developers need the exact same context as you do when trying to find its cause. Hence, you should always include a *full list of mods with their corresponding version* in your report. Note that you should probably only include the ones you found by [isolating the issue](#isolating-an-issue).
 - **Steps to Reproduce:** You should always include a list of steps required to provoke an issue to occur. Without it, developers can only take rough guesses at what might cause the bug. Try to reduce the amount of steps to a bare minimum, to make reproducing the issue easier. If you require a sophisticated setup to get even close to the issue, you might want to consider including your save file in your report.
 - **Full Log:** This is probably the most important part of your bug report. You *always* have to include the full log (`debug.log` from earlier), since it is the only way of telling what has caused an issue in code. *Do not* paste the full log as plain text in your report, but use [GitHub Gist](https://gist.github.com) instead and link to that from your report.

### Markdown Issue Template
GitHub and BitBucket allow you to use the Markdown format for writing your issues. Below you can find a simple template that you can copy and fill in with your data. `{variables}` are written in braces and are supposed to be replaced by you with the information the variable name indicates. Everything behind two slashes (`//`) has to be interpreted as a comment and should be removed. If you want to include any link, you have to write in the `[title](url)` form. For further information on Markdown, see [this cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet).

```markdown
**Minecraft Version:** {mc version}

**Forge Version:** {forge version}

**Mod Version:** {mod version} // Remove this line if you're reporting to Forge and no specific mod is involved

[**Full Log**]({link to gist/pastebin with full log})

**Mods in This Instance:**
| Name                     | Version     |
| ------------------------ | ----------- |
| [{mod 1}]({download 1})  | {version 1} | // The download links are entirely optional
| {mod 2}                  | {version 2} |
| {mod 3}                  | {version 3} |
| ...                      | ...         |

**Steps to Reproduce:**
 1. Do this
 2. Then do that
 3. ...
```

*Note: The mod list here is formatted as table for easy reading (when displayed) and to look nice in text. Markdown does not care about any alignment of the spaces, so you can simply write individual rows of the table as* `| {mod name} | {version} |` *without having to care about the spacing.*

### Waiting for an Issue to Be Resolved
After you have written your bug report, it is time to *wait*. Please be *patient* after submitting your issue, since it probably isn't the only one. If there's no response after 5 minutes or even a day of writing it, *do not* try to bump the issue by commenting on it, people *will* read it in due time. The only excuse for bumping a report is if it is a *critical* bug that prevents you from playing the game.

Every project has a different way of dealing with issues, so don't wonder if there's no comment on it, but somebody "labels" it with certain markers. This helps developers to more easily keep track of bugs and priorities. When any further input from you is required, you will be asked specifically. Do not bug anyone through other channels about resolving your issue.

Once your issue is closed, you will know whether it was valid or turned out to be unreproducible. If you included all of the data from above in your report, chances are that the latter won't be the case.

[Bug-Reports]: https://forums.minecraftforge.net/forum/18-support-bug-reports/
[Modder-Support]: https://forums.minecraftforge.net/forum/70-modder-support/

[Discord]: https://discord.minecraftforge.net/

[Documentation]: https://docs.minecraftforge.net/
