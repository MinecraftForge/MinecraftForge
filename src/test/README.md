# Writing a Test Mod - Guidelines

All feature-adding PRs need to have a test mod that demonstrates that the new feature works, and how to use it. A good test mod should:

1. be minimal. All that should be demonstrated is that the PR works correctly, nothing more. However test mods do not need to be minified, class and member names should be sensible. Nor is line count an issue, follow the Forge class formatting conventions when writing a test mod.

2. keep all classes in a single file. This can be achieved using nested classes.

3. follow 'best practices' at the time of creation. Test mods should be written using the same 'best practices' used for writing mods, with the exception of keeping all classes in a single file. For example a test mod written for a PR targeting 1.12 Forge should use `ObjectHolder`s & `RegistryEvent`s.

4. if any changes affect gameplay (e.g make apples explode), the test mod writes to the log, or the test mod intentionally causes errors (See 7), the test mod should be disableable. All disableable test mods should be disabled by default. The suggested method of making a test mod disableable is a `private static final boolean` that defaults to false and is checked before running any code. To enable the test mod, the test mod is edited so that the boolean is true.

5. if the change made by the PR is clientside only or serverside only, the test mod should function correctly with a Forge client-vanilla server server connection or a vanilla client-Forge connection, respectively, as well as a Forge server-Forge client connection.

6. be intuitive to use. If the source code doesn't make the testing procedure clear, a comment block should be placed at the top of the file with instructions.

7. test mods should not cause errors, unless the errors are intentionally caused to demonstrate that incorrect usage/implementation of the tested feature causes the appropriate errors. In this case the test mods should also be disableable.
