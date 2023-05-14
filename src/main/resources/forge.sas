# Forge Sided Annotation Stripper Config
# Please keep this file organized. And use the forge:checkAndFixSAS task to generate/validate inheritance.
# So only add the root function that needs to have the annotation stripped.
# checkAndFixSAS will populate all overrides as nessasary. Prefixing added lines with \t
# checkAndFixSAS also supports names using . so simplest way to find the correct line to add is to get the AT line from the bot
#   and remove the access modifier.
# You need to run the setup task again after running checkAndFixSAS for genPatches to pick up the changes.
#==================================================================================================================================
net/minecraft/client/server/LanServerPinger