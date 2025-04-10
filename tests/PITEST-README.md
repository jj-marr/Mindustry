# PIT Mutation Testing for Mindustry

## Overview

This project has been configured with PIT (Pitest) for mutation testing. Mutation testing helps identify weaknesses in your test suite by making small changes to your code and checking if tests can detect these modifications.

## What is Mutation Testing?

Mutation testing introduces small changes ("mutations") to your source code, such as:
- Replacing `+` with `-`
- Inverting boolean conditions (`>` to `<=`)
- Removing method calls
- Changing return values

These mutations create "mutants" of your code. When your tests run:
- A "killed mutant" means at least one test failed (good! your tests caught the bug)
- A "survived mutant" means all tests passed (bad! your tests didn't detect the change)

High mutation coverage indicates a robust test suite that can detect real bugs.

## Current Configuration

The PIT configuration targets:
- Classes: `mindustry.game.*` (including MapMarkers)
- Tests: All classes ending with "Tests"
- Reports: HTML and XML formats
- Threshold: 70% mutation coverage, 80% code coverage

## Running Mutation Tests

### Option 1: Using the Shell Script (Recommended)

```bash
cd tests
./run-pitest.sh
```

This runs mutation testing in an ephemeral nix-shell with all necessary dependencies.

### Option 2: Using Gradle Directly

```bash
./gradlew :tests:pitest
```

## Interpreting Results

After running PIT, HTML reports are generated in `tests/build/reports/pitest/`.
Open `index.html` to view:

1. **Package summary** - Overall stats for each package
2. **Class breakdown** - Detailed stats for each class
3. **Line coverage** - Which lines were tested
4. **Mutation coverage** - Which mutations were killed or survived

Focus on classes with low mutation coverage or many surviving mutants.

## Troubleshooting

### No mutations found

If you see "No mutations found":
- Check that the target classes (`mindustry.game.*`) exist
- Verify that tests are properly configured to test those classes
- Ensure the sourceDirs in build.gradle point to the correct locations

### Tests taking too long

- Reduce the number of threads in the pitest configuration
- Increase the timeoutFactor in the pitest configuration
- Target more specific classes or exclude complex ones

### JVM crashes during test

- Increase the JVM memory allocation (`-Xmx` flag)
- Reduce the number of threads
- Check for infinite loops that might be created by mutations

## Extending the Configuration

To test other packages or classes:
1. Edit `tests/build.gradle`
2. Modify the `targetClasses` property
3. Adjust the `targetTests` property if needed

For example, to test another package:

```gradle
pitest {
    targetClasses = ['mindustry.game.*', 'mindustry.core.*']
}
