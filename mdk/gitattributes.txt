# Disable autocrlf on generated files, they always generate with LF
# Add any extra files or paths here to make git stop saying they
# are changed when only line endings change.
src/generated/**/.cache/cache text eol=lf
src/generated/**/*.json text eol=lf
